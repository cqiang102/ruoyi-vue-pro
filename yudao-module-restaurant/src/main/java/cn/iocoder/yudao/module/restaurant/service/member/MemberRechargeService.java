package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.pay.api.wallet.dto.PayWalletRespDTO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberRechargeDO;

/**
 * 会员储值充值服务
 *
 * @author 餐饮 SaaS
 */
public interface MemberRechargeService {

    /**
     * 创建储值充值单，返回芋道 pay_order.id（供前端拉起微信支付）
     *
     * @param userId     用户编号
     * @param userType   用户类型（芋道 UserTypeEnum.MEMBER=2）
     * @param appKey     支付应用标识
     * @param payAmount  充值本金（单位：分，必须 > 0）
     * @param giftAmount 赠送金额（单位：分，可空，默认 0）
     * @return 支付单编号（pay_order.id）
     */
    Long createRecharge(Long userId, Integer userType, String appKey, Long payAmount, Long giftAmount);

    /**
     * 微信支付成功回调（由 OrderPayNotifyController 按 RCG- 前缀路由）
     *
     * @param merchantOrderId 充值单号
     * @param payOrderId      微信支付单编号
     */
    void onPaySuccess(String merchantOrderId, Long payOrderId);

    /**
     * 获取（或创建）会员钱包与余额
     */
    PayWalletRespDTO getWallet(Long userId, Integer userType);

    /**
     * 我的充值记录分页
     */
    PageResult<MemberRechargeDO> getRechargePage(Long userId, PageParam pageReqVO);

    /**
     * 充值单详情
     * <p>
     * P2-J：调用方必须传入当前登录用户编号，服务层校验充值单归属，
     * 杜绝消费者端跨用户查看他人充值记录（横向越权）
     *
     * @param id     充值单编号
     * @param userId 当前登录用户编号
     */
    MemberRechargeDO getRecharge(Long id, Long userId);

}
