package cn.iocoder.yudao.module.restaurant.controller.admin.reserve;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.service.reserve.ReserveRuleSaveReqVO;
import cn.iocoder.yudao.module.restaurant.service.reserve.ReserveRuleService;
import cn.iocoder.yudao.module.restaurant.service.reserve.SlotRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 预约规则（M-09）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 预约规则")
@RestController
@RequestMapping("/store/reserve-rule")
@Validated
public class ReserveRuleController {

    @Resource
    private ReserveRuleService reserveRuleService;

    @PostMapping("/create")
    @Operation(summary = "新增预约规则")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:reserve-rule:create')")
    public CommonResult<Long> createRule(@Valid @RequestBody ReserveRuleSaveReqVO reqVO) {
        return success(reserveRuleService.createRule(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新预约规则")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:reserve-rule:update')")
    public CommonResult<Boolean> updateRule(@Valid @RequestBody ReserveRuleSaveReqVO reqVO) {
        reserveRuleService.updateRule(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除预约规则")
    @Parameter(name = "id", description = "规则编号", required = true, example = "1")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:reserve-rule:delete')")
    public CommonResult<Boolean> deleteRule(@RequestParam("id") Long id) {
        reserveRuleService.deleteRule(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "预约规则列表")
    @Parameter(name = "storeId", description = "门店编号（本店隔离由 storeId 保证）", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:reserve-rule:query')")
    public CommonResult<List<ReserveRuleSaveReqVO>> getRuleList(@RequestParam("storeId") Long storeId) {
        return success(reserveRuleService.getRuleList(storeId));
    }

    @GetMapping("/slots")
    @Operation(summary = "预览某天可用时段（含余量）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:reserve-rule:query')")
    public CommonResult<List<SlotRespVO>> getAvailableSlots(@RequestParam("storeId") Long storeId,
                                                            @RequestParam("date") String date) {
        return success(reserveRuleService.getAvailableSlots(storeId, LocalDate.parse(date)));
    }

}
