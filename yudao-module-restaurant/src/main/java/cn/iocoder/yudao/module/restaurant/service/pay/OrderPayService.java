package cn.iocoder.yudao.module.restaurant.service.pay;

import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.api.refund.dto.PayRefundRespDTO;

import java.time.LocalDateTime;

/**
 * 餐饮微信支付 / 退款服务
 * <p>
 * 底层复用芋道支付中台的 {@code PayOrderApi} / {@code PayRefundApi}（微信渠道，模式 B 下每商户独立微信商户号）。
 * 本服务只做餐饮业务视角的封装：
 * <ul>
 *     <li>创建支付单：仅生成 pay_order，真正的"拉起微信支付"由消费者小程序直接调芋道 pay 模块的
 *         {@code AppPayOrderController.submitPayOrder(channelCode=wx_lite)} 完成（拿 prepay 参数）。</li>
 *     <li>退款：创建退款单，微信原路退回。</li>
 * </ul>
 * 余额抵扣型组合支付（余额 + 微信）由 M4 订单服务编排：先 {@code WalletPayService.consume} 扣余额，
 * 再对本服务创建的微信支付单支付差额。
 *
 * @author 餐饮 SaaS
 */
public interface OrderPayService {

    /**
     * 创建微信支付单（仅生成 pay_order，不发起微信调用）
     *
     * @param appKey          支付应用标识（每租户一个 PayApp，由 M1 商户开通时创建）
     * @param userIp          用户 IP
     * @param userId          用户编号（消费者会员 id）
     * @param userType        用户类型（MemberUserDO 对应类型）
     * @param merchantOrderId 餐饮业务订单号（商户内唯一，幂等键）
     * @param subject         商品标题（≤32 字）
     * @param body            商品描述（≤128 字）
     * @param price           支付金额，单位分，必须大于 0
     * @param expireTime      支付过期时间
     * @return 支付单编号（pay_order.id）
     */
    Long createWeixinPayOrder(String appKey, String userIp, Long userId, Integer userType,
                              String merchantOrderId, String subject, String body,
                              Integer price, LocalDateTime expireTime);

    /**
     * 查询支付单
     */
    PayOrderRespDTO getOrder(Long id);

    /**
     * 创建退款单（微信原路退回）
     *
     * @param appKey            支付应用标识
     * @param userIp            用户 IP
     * @param userId            用户编号
     * @param userType          用户类型
     * @param merchantOrderId   餐饮业务订单号
     * @param merchantRefundId  餐饮退款单号（商户内唯一，幂等键）
     * @param reason            退款原因
     * @param price             退款金额，单位分，必须大于 0
     * @return 退款单编号
     */
    Long createRefund(String appKey, String userIp, Long userId, Integer userType,
                     String merchantOrderId, String merchantRefundId, String reason, Integer price);

    /**
     * 查询退款单
     */
    PayRefundRespDTO getRefund(Long id);

}
