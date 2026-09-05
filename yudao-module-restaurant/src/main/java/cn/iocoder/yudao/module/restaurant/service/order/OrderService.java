package cn.iocoder.yudao.module.restaurant.service.order;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.order.vo.OrderVO;

/**
 * 订单 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface OrderService {

    /**
     * 创建订单（含明细、规格/加料快照、自动算价）
     */
    Long createOrder(OrderVO.CreateReqVO createReqVO);

    /**
     * 加菜（往已存在订单追加明细）
     */
    void addOrderItems(Long orderId, java.util.List<OrderVO.ItemCreateVO> items);

    /**
     * 取消订单（仅待支付可取消）
     */
    void cancelOrder(Long orderId);

    /**
     * 接单（已支付 → 制作中）
     */
    void acceptOrder(Long orderId);

    /**
     * 完成订单（制作中 → 已完成）
     */
    void completeOrder(Long orderId);

    /**
     * 扫码核销（门店端凭核销码完成订单）
     * <p>
     * 核销成功即视为订单完成：释放堂食桌台、累加会员消费。已核销订单幂等返回。
     *
     * @param verifyCode 核销码（6 位）
     * @param storeId    门店编号（用于校验订单归属，可空）
     */
    void verifyOrder(String verifyCode, Long storeId);

    /**
     * 叫号（门店端点击"叫号"，记录叫号时间用于展示已叫状态）
     *
     * @param orderId 订单编号
     */
    void callOrder(Long orderId);

    /**
     * 发起微信支付：创建 pay_order 并返回其编号（真正的拉起支付由消费者小程序调 pay 模块完成）
     *
     * @return 关联的 pay_order.id
     */
    Long payByWeixin(Long orderId, String appKey, String userIp, Long userId, Integer userType);

    /**
     * 余额支付（会员储值卡扣减）
     * <p>
     * 带事务与 CAS 幂等：扣款与订单状态更新同生共死，并发重复请求只生效一次。
     *
     * @param userId 当前登录用户编号（服务端从登录态取，钱包按 userId 定位）
     */
    void payByBalance(Long orderId, Long userId);

    /**
     * 现金收讫（收银台 M-04：店员现场收现金，订单待支付 → 已支付）
     * <p>
     * 不走 pay 模块、无支付单；仅校验订单为待支付态（订单归属本店由调用方
     * 通过 {@code StoreAuthService#validateOrderOwnership} 校验，与 P1-A 模式一致）。
     * 支付方式记为现金（payType=4），无账务流水，退款走 refundOrder 时按现金原路人工退还（MVP 边界）。
     *
     * @param orderId 订单编号
     */
    void payByCash(Long orderId);

    /**
     * 发起退款
     * <p>
     * 微信支付的订单走芋道 Pay 退款（原路退回，异步回调 {@link #onRefundSuccess} 置为已退款）；
     * 余额支付的订单直接回滚会员钱包（同步置为已退款）。
     *
     * @param orderId 订单编号
     * @param reason  退款原因
     */
    void refundOrder(Long orderId, String reason);

    /**
     * 微信支付成功回调（由 pay 模块 HTTP 通知接入，作幂等处理）
     *
     * @param merchantOrderId 餐饮业务订单号（即 orderNo）
     * @param payOrderId      芋道支付单编号
     */
    void onPaySuccess(String merchantOrderId, Long payOrderId);

    /**
     * 退款成功回调（由 pay 模块 HTTP 通知接入，作幂等处理）
     *
     * @param merchantOrderId   餐饮业务订单号（即 orderNo）
     * @param merchantRefundId  餐饮退款单号
     * @param payRefundId       芋道退款单编号
     */
    void onRefundSuccess(String merchantOrderId, String merchantRefundId, Long payRefundId);

    /**
     * 获得订单（含明细）
     */
    OrderVO.RespVO getOrder(Long id);

    /**
     * 校验订单归属（消费者端防水平越权：订单必须属于当前登录用户，否则抛 ORDER_NOT_OWNER）
     *
     * @param orderId 订单编号
     * @param userId  当前登录用户编号
     */
    void validateOrderOwner(Long orderId, Long userId);

    /**
     * 分页查询订单
     */
    PageResult<OrderVO.RespVO> getOrderPage(OrderVO.PageReqVO pageReqVO);

}
