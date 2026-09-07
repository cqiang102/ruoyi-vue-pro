package cn.iocoder.yudao.module.restaurant.service.notify;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.notify.vo.NotifyVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.NotifyRecordDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.NotifyTemplateDO;

import java.util.List;
import java.util.Map;

/**
 * 微信订阅消息 Service（M-12）
 * <p>
 * 发送链路复用芋道 {@code SocialClientApi#sendWxaSubscribeMessage}（内部按模板标题匹配 priTmplId、
 * 取小程序 openid、调用 WxJava 下发），本服务只负责：场景→模板映射、内容组装、发送结果落库。
 *
 * @author 餐饮 SaaS
 */
public interface NotifyService {

    /**
     * 场景码：支付成功
     */
    String SCENE_PAY_SUCCESS = "pay_success";
    /**
     * 场景码：出餐完成
     */
    String SCENE_MEAL_READY = "meal_ready";
    /**
     * 场景码：退款成功
     */
    String SCENE_REFUND_SUCCESS = "refund_success";

    /**
     * 按场景发送订阅消息（业务侧调用；<b>永不抛异常</b>，失败仅落库 + 日志，避免阻断支付/出餐主流程）
     *
     * @param userId   会员编号（MemberUserDO.id）
     * @param storeId  门店编号
     * @param scene    场景码
     * @param orderId  订单编号（可空）
     * @param messages 模板字段值（key 如 character_string1/thing2/amount3/time4/phrase5）
     * @param page     跳转页面（为空则用模板配置的 page）
     */
    void send(Long userId, Long storeId, String scene, Long orderId, Map<String, String> messages, String page);

    /**
     * 模板分页（本店 + 平台默认）
     */
    PageResult<NotifyTemplateDO> getTemplatePage(PageParam pageParam, Long storeId);

    /**
     * 创建模板（P1-A：storeId 由登录店员注入）
     */
    Long createTemplate(NotifyVO.TemplateSaveReqVO reqVO, Long storeId);

    /**
     * 更新模板（本店校验）
     */
    void updateTemplate(NotifyVO.TemplateSaveReqVO reqVO, Long storeId);

    /**
     * 删除模板（本店校验）
     */
    void deleteTemplate(Long id, Long storeId);

    /**
     * 发送记录分页
     */
    PageResult<NotifyRecordDO> getRecordPage(PageParam pageParam, Long storeId, String scene, Integer status);

    /**
     * 小程序端：获取需引导用户订阅的模板 ID 列表（供 wx.requestSubscribeMessage 的 tmplIds）
     * <p>
     * 取本租户已启用模板的标题 → 匹配微信公众平台该小程序的模板 ID（priTmplId）。
     *
     * @return 模板 ID 列表（去重；未配置时为空列表）
     */
    List<String> getSubscribeTemplateIds();

}
