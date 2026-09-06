package cn.iocoder.yudao.module.restaurant.service.delivery;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.delivery.vo.DeliveryVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.delivery.DeliveryConfigDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.delivery.DeliveryOrderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.delivery.DeliveryConfigMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.delivery.DeliveryOrderMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.order.OrderMapper;
import cn.iocoder.yudao.module.restaurant.enums.order.OrderStatusEnum;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.restaurant.service.delivery.client.DadaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.DELIVERY_CALLBACK_SIGN_INVALID;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.DELIVERY_CONFIG_DISABLED;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.DELIVERY_CONFIG_NOT_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.DELIVERY_ORDER_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.DELIVERY_ORDER_NOT_DELIVERY;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.DELIVERY_ORDER_NOT_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.DELIVERY_ORDER_STATUS_INVALID;

/**
 * 达达配送 Service 实现（M-11）
 * <p>
 * 状态映射（达达 → 本地）：1待接单→1、2待取货/100骑士到店→2、3配送中→3、4已完成→4、
 * 5已取消→5、9/10妥投异常→9、1000创建失败→10。
 * 回调处理：无租户上下文（达达不携带租户头），先 executeIgnore 查运单拿 tenantId，
 * 再 execute(tenantId) 更新，避免租户插件误伤。
 *
 * @author 餐饮 SaaS
 */
@Service
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    /**
     * 可发单/进行中的运单状态：0待发单 1待接单 2待取货 3配送中
     */
    private static final List<Integer> ACTIVE_STATUS = Collections.unmodifiableList(
            new ArrayList<>(java.util.Arrays.asList(0, 1, 2, 3)));

    @Value("${yudao.restaurant.dada.callback-url:}")
    private String callbackUrl;

    @Resource
    private DeliveryConfigMapper deliveryConfigMapper;
    @Resource
    private DeliveryOrderMapper deliveryOrderMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private DadaClient dadaClient;

    @Override
    public void saveConfig(DeliveryVO.ConfigSaveReqVO reqVO, Long storeId) {
        DeliveryConfigDO config = deliveryConfigMapper.selectByStoreId(storeId);
        boolean create = config == null;
        if (create) {
            config = new DeliveryConfigDO();
            config.setStoreId(storeId);
        }
        config.setDadaShopNo(reqVO.getDadaShopNo());
        config.setCityCode(reqVO.getCityCode());
        config.setStoreLat(reqVO.getStoreLat());
        config.setStoreLng(reqVO.getStoreLng());
        config.setEnabled(reqVO.getEnabled() == null ? 1 : reqVO.getEnabled());
        if (create) {
            deliveryConfigMapper.insert(config);
        } else {
            deliveryConfigMapper.updateById(config);
        }
    }

    @Override
    public DeliveryVO.ConfigRespVO getConfig(Long storeId) {
        DeliveryConfigDO config = deliveryConfigMapper.selectByStoreId(storeId);
        if (config == null) {
            return null;
        }
        DeliveryVO.ConfigRespVO resp = new DeliveryVO.ConfigRespVO();
        resp.setStoreId(config.getStoreId());
        resp.setDadaShopNo(config.getDadaShopNo());
        resp.setCityCode(config.getCityCode());
        resp.setStoreLat(config.getStoreLat());
        resp.setStoreLng(config.getStoreLng());
        resp.setEnabled(config.getEnabled());
        return resp;
    }

    @Override
    public Long sendDelivery(Long orderId, Long storeId) {
        OrderDO order = orderMapper.selectById(orderId);
        if (order == null || !order.getStoreId().equals(storeId)) {
            throw exception(DELIVERY_ORDER_NOT_EXISTS);
        }
        // 仅外卖订单可发配送
        if (order.getType() == null || order.getType() != 3) {
            throw exception(DELIVERY_ORDER_NOT_DELIVERY);
        }
        // 已支付/制作中才可发单（已完成不可补发）
        Integer st = order.getStatus();
        if (!OrderStatusEnum.PAID.getStatus().equals(st) && !OrderStatusEnum.COOKING.getStatus().equals(st)) {
            throw exception(DELIVERY_ORDER_STATUS_INVALID);
        }
        // 必须有收货信息
        if (StrUtil.isBlank(order.getReceiverName()) || StrUtil.isBlank(order.getReceiverAddress())) {
            throw exception(DELIVERY_ORDER_NOT_DELIVERY);
        }
        // 已有进行中运单则拒绝
        DeliveryOrderDO exist = deliveryOrderMapper.selectByOrderId(orderId);
        if (exist != null && ACTIVE_STATUS.contains(exist.getStatus())) {
            throw exception(DELIVERY_ORDER_EXISTS);
        }
        // 配置检查
        DeliveryConfigDO config = deliveryConfigMapper.selectByStoreId(storeId);
        if (config == null) {
            throw exception(DELIVERY_CONFIG_NOT_EXISTS);
        }
        if (config.getEnabled() == null || config.getEnabled() != 1) {
            throw exception(DELIVERY_CONFIG_DISABLED);
        }
        if (StrUtil.isBlank(callbackUrl)) {
            throw exception(DELIVERY_CONFIG_NOT_EXISTS);
        }
        // 发单（金额转元；不垫付）。收货人经纬度不传（OrderDO 无收货坐标），由达达按收货地址解析
        Map<String, Object> result;
        try {
            result = dadaClient.addOrder(config.getDadaShopNo(), String.valueOf(orderId),
                    config.getCityCode(), fen2yuan(order.getPayPrice()), 0,
                    order.getReceiverName(), order.getReceiverPhone(), order.getReceiverAddress(),
                    null, null, callbackUrl,
                    order.getRemark());
        } catch (Exception e) {
            // 发单失败：记录/更新运单为失败，可重发
            upsertDeliveryOrder(order, exist, 10, null, e.getMessage());
            throw e;
        }
        return upsertDeliveryOrder(order, exist, 1,
                result == null ? null : (String) result.get("client_id"), null);
    }

    /**
     * upsert 运单：无则建（status=指定），有（取消/失败）则复用同 origin_id 重发
     */
    private Long upsertDeliveryOrder(OrderDO order, DeliveryOrderDO exist, int status,
                                     String dadaOrderId, String errorMsg) {
        if (exist == null) {
            exist = new DeliveryOrderDO();
            exist.setOrderId(order.getId());
            exist.setStoreId(order.getStoreId());
            exist.setOriginId(String.valueOf(order.getId()));
            exist.setStatus(status);
            exist.setDadaOrderId(dadaOrderId);
            exist.setErrorMsg(errorMsg);
            deliveryOrderMapper.insert(exist);
        } else {
            exist.setStatus(status);
            exist.setDadaOrderId(dadaOrderId);
            exist.setErrorMsg(errorMsg);
            deliveryOrderMapper.updateById(exist);
        }
        return exist.getId();
    }

    @Override
    public void cancelDelivery(Long orderId, Long storeId) {
        DeliveryOrderDO delivery = deliveryOrderMapper.selectByOrderId(orderId);
        if (delivery == null || !delivery.getStoreId().equals(storeId)) {
            throw exception(DELIVERY_ORDER_NOT_EXISTS);
        }
        // 仅进行中可取消
        if (!ACTIVE_STATUS.contains(delivery.getStatus())) {
            throw exception(DELIVERY_ORDER_STATUS_INVALID);
        }
        // 0 待发单无需调达达
        if (delivery.getStatus() != 0) {
            dadaClient.formalCancel(delivery.getOriginId());
        }
        delivery.setStatus(5);
        delivery.setErrorMsg("商家取消");
        deliveryOrderMapper.updateById(delivery);
    }

    @Override
    public PageResult<DeliveryOrderDO> getDeliveryPage(PageParam pageParam, Long storeId, Integer status) {
        return deliveryOrderMapper.selectPage(pageParam, storeId, status);
    }

    @Override
    public void handleCallback(DeliveryVO.CallbackReqVO reqVO) {
        // 1. 验签：md5(升序排列的 client_id/order_id/update_time 值拼接) 小写
        String expected = verifySign(reqVO);
        if (!expected.equals(StrUtil.nullToEmpty(reqVO.getSignature()))) {
            log.warn("[handleCallback][达达回调验签失败 order_id({})]", reqVO.getOrderId());
            throw exception(DELIVERY_CALLBACK_SIGN_INVALID);
        }
        if (StrUtil.isBlank(reqVO.getOrderId())) {
            return;
        }
        // 2. 忽略租户查运单（达达回调不带租户头）
        DeliveryOrderDO delivery = TenantUtils.executeIgnore(() ->
                deliveryOrderMapper.selectByOriginId(reqVO.getOrderId()));
        if (delivery == null) {
            log.warn("[handleCallback][回调订单({})无运单记录，忽略]", reqVO.getOrderId());
            return;
        }
        // 3. 以运单所属租户上下文更新
        Long tenantId = delivery.getTenantId();
        TenantUtils.execute(tenantId, () -> {
            delivery.setStatus(mapStatus(reqVO.getOrderStatus()));
            delivery.setDadaOrderId(StrUtil.blankToDefault(reqVO.getClientId(), delivery.getDadaOrderId()));
            delivery.setDmName(reqVO.getDmName());
            delivery.setDmMobile(reqVO.getDmMobile());
            if (StrUtil.isNotBlank(reqVO.getCancelReason())) {
                delivery.setErrorMsg(reqVO.getCancelReason());
            }
            delivery.setCallbackTime(LocalDateTime.now());
            deliveryOrderMapper.updateById(delivery);
        });
    }

    /**
     * 达达回调验签：三字段值升序拼接后 md5（小写）
     */
    private String verifySign(DeliveryVO.CallbackReqVO reqVO) {
        List<String> values = new ArrayList<>();
        values.add(StrUtil.nullToEmpty(reqVO.getClientId()));
        values.add(StrUtil.nullToEmpty(reqVO.getOrderId()));
        values.add(reqVO.getUpdateTime() == null ? "" : String.valueOf(reqVO.getUpdateTime()));
        Collections.sort(values);
        return SecureUtil.md5(String.join("", values));
    }

    /**
     * 达达状态 → 本地状态
     */
    private int mapStatus(Integer dadaStatus) {
        if (dadaStatus == null) {
            return 0;
        }
        switch (dadaStatus) {
            case 1:
            case 8:
                return 1; // 待接单
            case 2:
            case 100:
                return 2; // 待取货（含骑士到店）
            case 3:
                return 3; // 配送中
            case 4:
                return 4; // 已送达
            case 5:
                return 5; // 已取消
            case 9:
            case 10:
                return 9; // 妥投异常
            case 1000:
                return 10; // 发单失败（达达侧创建失败）
            default:
                return 0;
        }
    }

    private static BigDecimal fen2yuan(Long fen) {
        return fen == null ? BigDecimal.ZERO : BigDecimal.valueOf(fen).divide(BigDecimal.valueOf(100));
    }

}
