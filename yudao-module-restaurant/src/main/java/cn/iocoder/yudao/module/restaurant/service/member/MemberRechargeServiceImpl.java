package cn.iocoder.yudao.module.restaurant.service.member;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.pay.api.wallet.dto.PayWalletRespDTO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberRechargeDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.MemberRechargeMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.restaurant.service.pay.OrderPayService;
import cn.iocoder.yudao.module.restaurant.service.pay.WalletPayService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 会员储值充值服务实现
 *
 * @author 餐饮 SaaS
 */
@Service
public class MemberRechargeServiceImpl implements MemberRechargeService {

    /**
     * 充值单号前缀（支付回调据此路由到充值处理）
     */
    private static final String ORDER_PREFIX = "RCG-";

    /**
     * 单笔充值金额上限（分）：100 万元。钱包入参为 int，超限需明确报错而非静默溢出
     */
    private static final long MAX_RECHARGE_AMOUNT = 100_000_000L;

    @Resource
    private MemberRechargeMapper memberRechargeMapper;
    @Resource
    private OrderPayService orderPayService;
    @Resource
    private WalletPayService walletPayService;

    @Override
    public Long createRecharge(Long userId, Integer userType, String appKey, Long payAmount, Long giftAmount) {
        if (payAmount == null || payAmount <= 0) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_RECHARGE_AMOUNT_INVALID);
        }
        long gift = giftAmount == null ? 0L : giftAmount;
        // P1-10：支付渠道与钱包入参均为 int，超限时明确报错，避免静默溢出产生错乱金额
        if (payAmount > MAX_RECHARGE_AMOUNT || payAmount + gift > MAX_RECHARGE_AMOUNT) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_RECHARGE_AMOUNT_INVALID);
        }
        String orderNo = ORDER_PREFIX + IdUtil.fastSimpleUUID();
        MemberRechargeDO recharge = new MemberRechargeDO()
                .setUserId(userId)
                .setUserType(userType)
                .setAppKey(appKey)
                .setPayAmount(payAmount)
                .setGiftAmount(gift)
                .setTotalAmount(payAmount + gift)
                .setOrderNo(orderNo)
                .setStatus(0)
                .setPayStatus(0);
        memberRechargeMapper.insert(recharge);

        Long payOrderId = orderPayService.createWeixinPayOrder(
                appKey, "127.0.0.1", userId, userType,
                orderNo, "会员储值充值", "会员储值充值",
                payAmount.intValue(), LocalDateTime.now().plusMinutes(30));
        recharge.setPayOrderId(payOrderId);
        memberRechargeMapper.updateById(recharge);
        return payOrderId;
    }

    /**
     * 支付成功回调（由 OrderPayNotifyController 按 RCG- 前缀路由到此）。
     *
     * <p>P1-8：回调链路无登录态、无 tenant-id 请求头，必须先忽略租户定位充值单，
     * 再切回其所属租户执行更新——不能依赖"无租户上下文时全表扫描"的隐式行为。
     *
     * <p>P1-10：钱包入参为 int，金额超限时明确报错而非静默溢出。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onPaySuccess(String merchantOrderId, Long payOrderId) {
        MemberRechargeDO recharge = TenantUtils.executeIgnore(() ->
                memberRechargeMapper.selectByOrderNo(merchantOrderId));
        if (recharge == null) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_RECHARGE_NOT_FOUND);
        }
        // 幂等：已充值直接返回（微信可能重复通知）
        if (recharge.getPayStatus() != null && recharge.getPayStatus() == 1) {
            return;
        }
        long amount = recharge.getTotalAmount() == null ? 0L : recharge.getTotalAmount();
        if (amount <= 0 || amount > MAX_RECHARGE_AMOUNT) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_RECHARGE_AMOUNT_INVALID);
        }
        // P1-C：先 CAS 占位 eq(payStatus,0).set(payStatus,1)——并发重复通知只有一个能成功，
        // 失败者直接幂等返回，杜绝"加钱在状态更新之前"导致的并发双倍入账。
        // 占位与加钱在同一事务内：加钱失败则整体回滚（含占位），保证钱、单同生共死
        int rows = TenantUtils.execute(recharge.getTenantId(), () -> memberRechargeMapper.update(null,
                new LambdaUpdateWrapper<MemberRechargeDO>()
                        .eq(MemberRechargeDO::getId, recharge.getId())
                        .eq(MemberRechargeDO::getPayStatus, 0)
                        .set(MemberRechargeDO::getPayStatus, 1)
                        .set(MemberRechargeDO::getStatus, 1)
                        .set(MemberRechargeDO::getPayOrderId, payOrderId)
                        .set(MemberRechargeDO::getPaidTime, LocalDateTime.now())));
        if (rows == 0) {
            // 已被并发通知处理，幂等返回
            return;
        }
        // 占位成功后并入会员钱包（本金 + 赠额）
        walletPayService.recharge(recharge.getUserId(), recharge.getUserType(),
                recharge.getOrderNo(), (int) amount);
    }

    @Override
    public PayWalletRespDTO getWallet(Long userId, Integer userType) {
        return walletPayService.getWallet(userId, userType);
    }

    @Override
    public PageResult<MemberRechargeDO> getRechargePage(Long userId, PageParam pageReqVO) {
        return memberRechargeMapper.selectPageByUser(userId, pageReqVO);
    }

    @Override
    public MemberRechargeDO getRecharge(Long id, Long userId) {
        // P2-J：充值单归属校验——非本人充值单抛 NOT_FOUND，
        // 不向调用方泄露充值单存在性（避免横向枚举攻击）
        MemberRechargeDO recharge = memberRechargeMapper.selectById(id);
        if (recharge == null || !java.util.Objects.equals(recharge.getUserId(), userId)) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_RECHARGE_NOT_FOUND);
        }
        return recharge;
    }

}
