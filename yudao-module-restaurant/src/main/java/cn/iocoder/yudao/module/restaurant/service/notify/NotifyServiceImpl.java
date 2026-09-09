package cn.iocoder.yudao.module.restaurant.service.notify;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.notify.vo.NotifyVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.NotifyRecordDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.RestaurantNotifyTemplateDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.notify.NotifyRecordMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.notify.RestaurantNotifyTemplateMapper;
import cn.iocoder.yudao.module.system.api.social.SocialClientApi;
import cn.iocoder.yudao.module.system.api.social.SocialUserApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserRespDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialWxaSubscribeMessageSendReqDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialWxaSubscribeTemplateRespDTO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.NOTIFY_TEMPLATE_NOT_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.NOTIFY_TEMPLATE_SCENE_DUPLICATE;

/**
 * 微信订阅消息 Service 实现（M-12）
 * <p>
 * 设计要点：
 * 1. <b>旁路发送</b>：支付成功/出餐/退款回调中调用，方法内部吞掉所有异常，
 *    绝不让"发通知失败"回滚订单主流程（与打印 M-10 同款设计）；
 * 2. <b>结果自证</b>：芋道在「模板缺失 / openid 缺失」时只打 warn 不抛异常，微信侧错误才抛异常，
 *    故成功与失败都由本服务落库 {@code restaurant_notify_record}，便于排查"用户说没收到"；
 * 3. <b>模板标题匹配</b>：芋道按模板标题找 priTmplId，故库中存标题而非模板 ID，避免硬编码失效。
 *
 * @author 餐饮 SaaS
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    /**
     * 微信订阅消息字段长度上限（超出会报 47003 参数格式错误）：
     * thing≤20、name≤10、character_string≤32、phrase≤5、amount/time 按格式不截断
     */
    private static final Map<String, Integer> FIELD_MAX_LENGTH = new LinkedHashMap<>();

    static {
        FIELD_MAX_LENGTH.put("thing", 20);
        FIELD_MAX_LENGTH.put("name", 10);
        FIELD_MAX_LENGTH.put("character_string", 32);
        FIELD_MAX_LENGTH.put("phrase", 5);
    }

    @Resource
    private RestaurantNotifyTemplateMapper restaurantNotifyTemplateMapper;
    @Resource
    private NotifyRecordMapper notifyRecordMapper;
    @Resource
    private SocialClientApi socialClientApi;
    @Resource
    private SocialUserApi socialUserApi;

    @Override
    public void send(Long userId, Long storeId, String scene, Long orderId,
                     Map<String, String> messages, String page) {
        try {
            doSend(userId, storeId, scene, orderId, messages, page);
        } catch (Exception e) {
            // 兜底：任何异常都不上抛，避免阻断支付/出餐/退款主流程
            log.error("[send][发送订阅消息异常 userId({}) storeId({}) scene({})]", userId, storeId, scene, e);
        }
    }

    private void doSend(Long userId, Long storeId, String scene, Long orderId,
                        Map<String, String> messages, String page) {
        // 1. 场景模板（本店优先，回退平台默认）
        RestaurantNotifyTemplateDO template = restaurantNotifyTemplateMapper.selectEnabled(storeId, scene);
        if (template == null) {
            log.info("[doSend][场景({}) 未配置启用模板，跳过发送 storeId({})]", scene, storeId);
            return;
        }
        // 2. 会员 openid（小程序）
        SocialUserRespDTO socialUser = socialUserApi.getSocialUserByUserId(
                UserTypeEnum.MEMBER.getValue(), userId, SocialTypeEnum.WECHAT_MINI_PROGRAM.getType());
        String openid = socialUser == null ? null : socialUser.getOpenid();
        if (StrUtil.isBlank(openid)) {
            saveRecord(storeId, userId, null, orderId, scene, template, page, messages, 2, "会员未绑定小程序 openid");
            return;
        }
        // 3. 组装并发送（字段值按微信规则清洗，避免 47003）
        SocialWxaSubscribeMessageSendReqDTO reqDTO = new SocialWxaSubscribeMessageSendReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setUserType(UserTypeEnum.MEMBER.getValue());
        reqDTO.setTemplateTitle(template.getTemplateTitle());
        reqDTO.setPage(StrUtil.blankToDefault(page, template.getPage()));
        if (CollUtil.isNotEmpty(messages)) {
            messages.forEach((k, v) -> reqDTO.addMessage(k, sanitize(k, v)));
        }
        try {
            socialClientApi.sendWxaSubscribeMessage(reqDTO);
            saveRecord(storeId, userId, openid, orderId, scene, template, page, messages, 1, null);
        } catch (Exception e) {
            // 微信侧错误（如 43101 用户拒收、47003 参数格式错误）由芋道抛异常
            saveRecord(storeId, userId, openid, orderId, scene, template, page, messages, 2, e.getMessage());
        }
    }

    /**
     * 字段值清洗：按字段名前缀截断到微信允许长度，空值补占位符（微信不允许空值）
     */
    private String sanitize(String key, String value) {
        String val = StrUtil.blankToDefault(value, "-");
        for (Map.Entry<String, Integer> entry : FIELD_MAX_LENGTH.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                return val.length() > entry.getValue() ? val.substring(0, entry.getValue()) : val;
            }
        }
        return val;
    }

    private void saveRecord(Long storeId, Long userId, String openid, Long orderId, String scene,
                            RestaurantNotifyTemplateDO template, String page, Map<String, String> messages,
                            Integer status, String errorMsg) {
        try {
            NotifyRecordDO record = new NotifyRecordDO();
            record.setStoreId(storeId);
            record.setUserId(userId);
            record.setOpenid(openid);
            record.setOrderId(orderId);
            record.setScene(scene);
            record.setTemplateTitle(template.getTemplateTitle());
            record.setPage(StrUtil.blankToDefault(page, template.getPage()));
            record.setContent(messages == null ? "{}" : JSONUtil.toJsonStr(messages));
            record.setStatus(status);
            record.setErrorMsg(errorMsg);
            record.setSendTime(LocalDateTime.now());
            notifyRecordMapper.insert(record);
        } catch (Exception e) {
            // 记录落库失败也不能影响主流程
            log.error("[saveRecord][发送记录落库失败 userId({}) scene({})]", userId, scene, e);
        }
    }

    // ========== 模板管理 ==========

    @Override
    public PageResult<RestaurantNotifyTemplateDO> getTemplatePage(PageParam pageParam, Long storeId) {
        return restaurantNotifyTemplateMapper.selectPage(pageParam, new LambdaQueryWrapperX<RestaurantNotifyTemplateDO>()
                .eq(RestaurantNotifyTemplateDO::getStoreId, storeId)
                .orderByAsc(RestaurantNotifyTemplateDO::getId));
    }

    @Override
    public Long createTemplate(NotifyVO.TemplateSaveReqVO reqVO, Long storeId) {
        // 同门店同场景唯一（平台默认模板 storeId=0 亦然）
        RestaurantNotifyTemplateDO exist = restaurantNotifyTemplateMapper.selectOne(new LambdaQueryWrapperX<RestaurantNotifyTemplateDO>()
                .eq(RestaurantNotifyTemplateDO::getStoreId, storeId)
                .eq(RestaurantNotifyTemplateDO::getScene, reqVO.getScene()));
        if (exist != null) {
            throw exception(NOTIFY_TEMPLATE_SCENE_DUPLICATE);
        }
        RestaurantNotifyTemplateDO template = new RestaurantNotifyTemplateDO();
        template.setStoreId(storeId);
        template.setScene(reqVO.getScene());
        template.setTemplateTitle(reqVO.getTemplateTitle());
        template.setKeywordConfig(reqVO.getKeywordConfig());
        template.setPage(reqVO.getPage());
        template.setStatus(reqVO.getStatus() == null ? 1 : reqVO.getStatus());
        restaurantNotifyTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void updateTemplate(NotifyVO.TemplateSaveReqVO reqVO, Long storeId) {
        RestaurantNotifyTemplateDO template = validateTemplate(reqVO.getId(), storeId);
        template.setScene(reqVO.getScene());
        template.setTemplateTitle(reqVO.getTemplateTitle());
        template.setKeywordConfig(reqVO.getKeywordConfig());
        template.setPage(reqVO.getPage());
        template.setStatus(reqVO.getStatus() == null ? template.getStatus() : reqVO.getStatus());
        restaurantNotifyTemplateMapper.updateById(template);
    }

    @Override
    public void deleteTemplate(Long id, Long storeId) {
        validateTemplate(id, storeId);
        restaurantNotifyTemplateMapper.deleteById(id);
    }

    private RestaurantNotifyTemplateDO validateTemplate(Long id, Long storeId) {
        RestaurantNotifyTemplateDO template = restaurantNotifyTemplateMapper.selectById(id);
        if (template == null || !ObjectUtil.equal(template.getStoreId(), storeId)) {
            throw exception(NOTIFY_TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    @Override
    public PageResult<NotifyRecordDO> getRecordPage(PageParam pageParam, Long storeId, String scene, Integer status) {
        return notifyRecordMapper.selectPage(pageParam, storeId, scene, status);
    }

    @Override
    public List<String> getSubscribeTemplateIds() {
        // 1. 本租户已启用的模板标题（平台默认 + 各门店，去重）
        List<RestaurantNotifyTemplateDO> templates = restaurantNotifyTemplateMapper.selectList(
                new LambdaQueryWrapperX<RestaurantNotifyTemplateDO>().eq(RestaurantNotifyTemplateDO::getStatus, 1));
        if (CollUtil.isEmpty(templates)) {
            return new ArrayList<>();
        }
        // 2. 微信侧该小程序的模板列表 → 按标题匹配取 priTmplId
        List<SocialWxaSubscribeTemplateRespDTO> wxTemplates;
        try {
            wxTemplates = socialClientApi.getWxaSubscribeTemplateList(UserTypeEnum.MEMBER.getValue());
        } catch (Exception e) {
            log.warn("[getSubscribeTemplateIds][拉取微信订阅模板列表失败]", e);
            return new ArrayList<>();
        }
        List<String> ids = new ArrayList<>();
        for (RestaurantNotifyTemplateDO t : templates) {
            if (CollUtil.isEmpty(wxTemplates)) {
                break;
            }
            wxTemplates.stream()
                    .filter(wx -> StrUtil.equals(wx.getTitle(), t.getTemplateTitle()))
                    .map(SocialWxaSubscribeTemplateRespDTO::getId)
                    .findFirst()
                    .ifPresent(id -> {
                        if (!ids.contains(id)) {
                            ids.add(id);
                        }
                    });
        }
        return ids;
    }

}
