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

    @Resource
    private cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService storeAuthService;

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

    @GetMapping("/workbench-summary")
    @Operation(summary = "工作台收支概览（S-04：storeId 由登录店员绑定门店强制注入）")
    // 2026-10-05 修复：原用 restaurant:withdraw:query，与「平台端财务管理」同权限串。
    // m49 为把财务管理收归平台端而剔除了门店角色的该权限 → 门店端工作台的收支卡片
    // 悄悄不显示了（前端 if(res.code===0) 静默失败）。但直接把权限还回去**不安全**：
    // 同 Controller 的 /income-summary、/page 都是**前端传 storeId、无归属校验**，
    // 门店账号拿到该权限可跨店查账。故本接口改用**独立权限串**（内部走
    // storeAuthService.getLoginUserStoreId()，无越权面），只放行这一个接口。
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:withdraw:workbench')")
    public CommonResult<Map<String, Object>> getWorkbenchSummary() {
        Long storeId = storeAuthService.getLoginUserStoreId();
        return success(withdrawService.getIncomeSummary(storeId, statisticsMapper));
    }

}
