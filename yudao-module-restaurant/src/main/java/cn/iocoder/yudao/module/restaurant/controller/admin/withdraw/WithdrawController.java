package cn.iocoder.yudao.module.restaurant.controller.admin.withdraw;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.withdraw.vo.WithdrawAccountRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.withdraw.WithdrawAccountDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.statistics.StatisticsMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.withdraw.WithdrawAccountMapper;
import cn.iocoder.yudao.module.restaurant.service.withdraw.WithdrawService;
import cn.iocoder.yudao.module.restaurant.service.withdraw.WithdrawVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 财务提现（M-30）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 财务提现")
@RestController
@RequestMapping("/store/withdraw")
@Validated
public class WithdrawController {

    @Resource
    private WithdrawService withdrawService;

    @Resource
    private WithdrawAccountMapper withdrawAccountMapper;

    @Resource
    private StatisticsMapper statisticsMapper;

    // ===================== 提现账户 =====================

    @PostMapping("/account/create")
    @Operation(summary = "新增提现账户")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:withdraw:create')")
    public CommonResult<Long> createAccount(@Valid @RequestBody WithdrawVO.AccountSaveReqVO reqVO) {
        return success(withdrawService.createAccount(reqVO));
    }

    @PutMapping("/account/update")
    @Operation(summary = "更新提现账户")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:withdraw:update')")
    public CommonResult<Boolean> updateAccount(@Valid @RequestBody WithdrawVO.AccountSaveReqVO reqVO) {
        withdrawService.updateAccount(reqVO);
        return success(true);
    }

    @DeleteMapping("/account/delete")
    @Operation(summary = "删除提现账户")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:withdraw:delete')")
    public CommonResult<Boolean> deleteAccount(@RequestParam("id") Long id) {
        withdrawService.deleteAccount(id);
        return success(true);
    }

    @GetMapping("/account/list")
    @Operation(summary = "提现账户列表（按店）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:withdraw:query')")
    public CommonResult<List<WithdrawAccountRespVO>> getAccountList(@RequestParam("storeId") Long storeId) {
        List<WithdrawAccountDO> list = withdrawAccountMapper.selectList(
                new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<WithdrawAccountDO>()
                        .eq(WithdrawAccountDO::getStoreId, storeId)
                        .orderByDesc(WithdrawAccountDO::getId));
        return success(BeanUtils.toBean(list, WithdrawAccountRespVO.class));
    }

    // ===================== 提现单 =====================

    @PostMapping("/apply")
    @Operation(summary = "申请提现（商户发起）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:withdraw:apply')")
    public CommonResult<Long> apply(@Valid @RequestBody WithdrawVO.ApplyReqVO reqVO) {
        return success(withdrawService.apply(reqVO));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核提现（平台：通过=已打款标记，驳回=填原因）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:withdraw:audit')")
    public CommonResult<Boolean> audit(@Valid @RequestBody WithdrawVO.AuditReqVO reqVO) {
        String auditUser = String.valueOf(SecurityFrameworkUtils.getLoginUserId());
        withdrawService.audit(reqVO, auditUser);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "提现单分页")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:withdraw:query')")
    public CommonResult<PageResult<WithdrawVO.RespVO>> getWithdrawPage(@Valid PageParam pageParam,
                                                                       @RequestParam(value = "storeId", required = false) Long storeId,
                                                                       @RequestParam(value = "status", required = false) Integer status) {
        return success(withdrawService.getWithdrawPage(pageParam, storeId, status));
    }

    @GetMapping("/income-summary")
    @Operation(summary = "门店收支概览（累计收入/已提现/可提现）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:withdraw:query')")
    public CommonResult<Map<String, Object>> getIncomeSummary(@RequestParam("storeId") Long storeId) {
        return success(withdrawService.getIncomeSummary(storeId, statisticsMapper));
    }

}
