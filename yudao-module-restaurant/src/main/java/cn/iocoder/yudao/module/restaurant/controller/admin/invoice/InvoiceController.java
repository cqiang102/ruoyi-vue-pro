package cn.iocoder.yudao.module.restaurant.controller.admin.invoice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.service.invoice.InvoiceService;
import cn.iocoder.yudao.module.restaurant.service.invoice.InvoiceVO;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 电子发票（M-35）
 *
 * <p>MVP 线下开具：审核通过 = 人工标记已开票。
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 电子发票")
@RestController
@RequestMapping("/store/invoice")
@Validated
public class InvoiceController {

    @Resource
    private InvoiceService invoiceService;

    @Resource
    private StoreAuthService storeAuthService;

    @GetMapping("/list")
    @Operation(summary = "开票申请列表（仅本店）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:invoice:query')")
    public CommonResult<List<InvoiceVO.RespVO>> getInvoiceList(
            @RequestParam(value = "storeId", required = false) Long storeId,
            @RequestParam(value = "status", required = false) Integer status) {
        // P1-A 同类修复（2026-09-21）：门店端一律以登录账号绑定的门店为准，
        // 忽略前端传入的 storeId —— 原先该参数可选且直接透传，
        // 不传时会把本租户所有门店的开票申请都返回（实测 store001 能看到门店2 的申请）
        return success(invoiceService.getInvoiceList(storeAuthService.getLoginUserStoreId(), status));
    }

    @PutMapping("/audit")
    @Operation(summary = "开票审核（true=标记已开票 false=驳回）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:invoice:audit')")
    public CommonResult<Boolean> auditInvoice(@Valid @RequestBody InvoiceVO.AuditReqVO reqVO) {
        // 原先不校验归属：实测 store001 可以直接审核门店2 的申请并改成已开票
        invoiceService.audit(reqVO, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除开票申请")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:invoice:delete')")
    public CommonResult<Boolean> deleteInvoice(@RequestParam("id") Long id) {
        // 同上：删除也必须限定本店
        invoiceService.deleteInvoice(id, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

}
