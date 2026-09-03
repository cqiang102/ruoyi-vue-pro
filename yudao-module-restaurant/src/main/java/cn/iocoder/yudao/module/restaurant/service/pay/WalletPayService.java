package cn.iocoder.yudao.module.restaurant.service.pay;

import cn.iocoder.yudao.module.pay.api.wallet.dto.PayWalletRespDTO;

/**
 * 餐饮余额（会员储值卡）支付服务
 * <p>
 * 底层复用芋道支付中台的 {@code PayWalletApi}，余额并发安全由芋道保证（Redis 分布式锁 + SQL 级 CAS），
 * 本服务只做餐饮业务视角的编排封装，供 M4 订单模块调用。
 * <p>
 * 金额单位：分。bizType 复用芋道 {@code PayWalletBizTypeEnum}：
 * - RECHARGE(1) 充值 / 充赠
 * - PAYMENT(3) 消费扣减
 * - PAYMENT_REFUND(4) 退款回滚
 *
 * @author 餐饮 SaaS
 */
public interface WalletPayService {

    /**
     * 获取（或自动创建）会员钱包
     */
    PayWalletRespDTO getWallet(Long userId, Integer userType);

    /**
     * 余额充值（含充赠金额，调用方把实际到账总额作为 price 传入）
     *
     * @param price 充值到账总额，单位分，必须为正数
     */
    void recharge(Long userId, Integer userType, String bizId, Integer price);

    /**
     * 余额消费扣减（如订单用余额支付、余额抵扣）
     *
     * @param price 扣减金额，单位分，必须为正数（内部转负数写入钱包）
     */
    void consume(Long userId, Integer userType, String bizId, Integer price);

    /**
     * 退款回滚余额（订单退款时，把曾用余额支付的部分退回）
     *
     * @param price 回滚金额，单位分，必须为正数
     */
    void refund(Long userId, Integer userType, String bizId, Integer price);

}
