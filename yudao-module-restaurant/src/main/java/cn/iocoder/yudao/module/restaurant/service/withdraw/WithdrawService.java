package cn.iocoder.yudao.module.restaurant.service.withdraw;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.withdraw.WithdrawAccountDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.withdraw.WithdrawDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.withdraw.WithdrawAccountMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.withdraw.WithdrawMapper;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.STORE_STAFF_STORE_MISMATCH;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.WITHDRAW_ACCOUNT_NOT_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.WITHDRAW_AMOUNT_INVALID;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.WITHDRAW_STATUS_INVALID;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.WITHDRAW_NOT_EXISTS;

/**
 * 提现 Service（M-30）
 *
 * @author 餐饮 SaaS
 */
@Service
public class WithdrawService {

    private static final DateTimeFormatter SNAPSHOT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Resource
    private WithdrawAccountMapper withdrawAccountMapper;

    @Resource
    private WithdrawMapper withdrawMapper;

    @Resource
    private StoreAuthService storeAuthService;

    // ===================== 提现账户 =====================

    public Long createAccount(WithdrawVO.AccountSaveReqVO reqVO) {
        WithdrawAccountDO account = BeanUtils.toBean(reqVO, WithdrawAccountDO.class);
        if (account.getStatus() == null) {
            account.setStatus(0);
        }
        // 门店端接口：门店归属取登录账号绑定的门店，不采信入参
        account.setStoreId(storeAuthService.getLoginUserStoreId());
        withdrawAccountMapper.insert(account);
        return account.getId();
    }

    public void updateAccount(WithdrawVO.AccountSaveReqVO reqVO) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateAccountInStore(reqVO.getId(), storeId);
        WithdrawAccountDO updateObj = BeanUtils.toBean(reqVO, WithdrawAccountDO.class);
        // 不允许通过更新把提现账户挂到其他门店
        updateObj.setStoreId(storeId);
        withdrawAccountMapper.updateById(updateObj);
    }

    public void deleteAccount(Long id) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateAccountInStore(id, storeId);
        withdrawAccountMapper.deleteById(id);
    }

    // ===================== 提现单 =====================

    /**
     * 门店申请提现：校验金额与账户，账户信息做快照（防事后改账户）
     */
    public Long apply(WithdrawVO.ApplyReqVO reqVO) {
        if (reqVO.getAmount() == null || reqVO.getAmount() <= 0) {
            throw exception(WITHDRAW_AMOUNT_INVALID);
        }
        WithdrawAccountDO account = withdrawAccountMapper.selectById(reqVO.getAccountId());
        if (account == null || !account.getStoreId().equals(reqVO.getStoreId())) {
            throw exception(WITHDRAW_ACCOUNT_NOT_EXISTS);
        }
        WithdrawDO withdraw = new WithdrawDO();
        withdraw.setStoreId(reqVO.getStoreId());
        withdraw.setAmount(reqVO.getAmount());
        withdraw.setStatus(0);
        withdraw.setAccountSnapshot(account.getAccountType() + " | " + account.getAccountName()
                + " | " + account.getAccountNo() + "（快照 " + LocalDateTime.now().format(SNAPSHOT_FMT) + "）");
        withdraw.setApplyRemark(reqVO.getApplyRemark());
        withdrawMapper.insert(withdraw);
        return withdraw.getId();
    }

    /**
     * 审核：通过 → 已打款（线下打款后人工标记）；驳回 → 必填原因
     */
    public void audit(WithdrawVO.AuditReqVO reqVO, String auditUser) {
        WithdrawDO withdraw = withdrawMapper.selectById(reqVO.getId());
        if (withdraw == null) {
            throw exception(WITHDRAW_NOT_EXISTS);
        }
        if (withdraw.getStatus() != 0) {
            throw exception(WITHDRAW_STATUS_INVALID);
        }
        if (Boolean.TRUE.equals(reqVO.getApproved())) {
            withdraw.setStatus(1);
        } else {
            if (reqVO.getRejectReason() == null || reqVO.getRejectReason().isEmpty()) {
                throw exception(WITHDRAW_STATUS_INVALID);
            }
            withdraw.setStatus(2);
            withdraw.setRejectReason(reqVO.getRejectReason());
        }
        withdraw.setAuditTime(LocalDateTime.now());
        withdraw.setAuditUser(auditUser);
        withdrawMapper.updateById(withdraw);
    }

    /**
     * 收支概览：门店累计已支付 GMV − 已打款/在途提现 = 可提现余额（口径说明：真实流水减提现记账）
     */
    public Map<String, Object> getIncomeSummary(Long storeId,
                                                cn.iocoder.yudao.module.restaurant.dal.mysql.statistics.StatisticsMapper statisticsMapper) {
        LocalDateTime epoch = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime far = LocalDateTime.of(2099, 1, 1, 0, 0);
        Map<String, Object> income = statisticsMapper.selectOverview(storeId, epoch, far);
        java.util.List<WithdrawDO> withdraws = withdrawMapper.selectList(
                new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<WithdrawDO>()
                        .eq(WithdrawDO::getStoreId, storeId)
                        .in(WithdrawDO::getStatus, 0, 1));
        long withdrawn = withdraws.stream().mapToLong(w -> w.getAmount() == null ? 0 : w.getAmount()).sum();
        long totalIncome = income == null || income.get("payTotal") == null ? 0L : Long.parseLong(income.get("payTotal").toString());
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("totalIncome", totalIncome);
        result.put("withdrawn", withdrawn);
        result.put("available", Math.max(0, totalIncome - withdrawn));
        return result;
    }

    public PageResult<WithdrawVO.RespVO> getWithdrawPage(PageParam pageParam, Long storeId, Integer status) {
        cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<WithdrawDO> wrapper =
                new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<WithdrawDO>()
                        .eqIfPresent(WithdrawDO::getStoreId, storeId)
                        .eqIfPresent(WithdrawDO::getStatus, status)
                        .orderByDesc(WithdrawDO::getId);
        PageResult<WithdrawDO> page = withdrawMapper.selectPage(pageParam, wrapper);
        return BeanUtils.toBean(page, WithdrawVO.RespVO.class);
    }

    private void validateAccountExists(Long id) {
        if (id == null || withdrawAccountMapper.selectById(id) == null) {
            throw exception(WITHDRAW_ACCOUNT_NOT_EXISTS);
        }
    }

    /**
     * 门店归属校验（2026-09-28 横向越权排查补充）：
     * 原先只校验"存在性"，A 店店员可删/改 B 店的提现账户（提现账户是打款凭据，风险高）。
     */
    private void validateAccountInStore(Long id, Long storeId) {
        WithdrawAccountDO account = id == null ? null : withdrawAccountMapper.selectById(id);
        if (account == null) {
            throw exception(WITHDRAW_ACCOUNT_NOT_EXISTS);
        }
        if (!Objects.equals(account.getStoreId(), storeId)) {
            throw exception(STORE_STAFF_STORE_MISMATCH);
        }
    }

}
