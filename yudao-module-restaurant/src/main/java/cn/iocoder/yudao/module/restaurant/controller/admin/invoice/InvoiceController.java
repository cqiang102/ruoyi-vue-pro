package cn.iocoder.yudao.module.restaurant.controller.admin.invoice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.service.invoice.InvoiceService;
import cn.iocoder.yudao.module.restaurant.service.invoice.InvoiceVO;
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

    @GetMapping("/list")
    @Operation(summary = "开票申请列表（storeId/status 可选）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:invoice:query')")
    public CommonResult<List<InvoiceVO.RespVO>> getInvoiceList(
            @RequestParam(value = "storeId", required = false) Long storeId,
            @RequestParam(value = "status", required = false) Integer status) {
        return success(invoiceService.getInvoiceList(storeId, status));
    }

    @PutMapping("/audit")
    @Operation(summary = "开票审核（true=标记已开票 false=驳回）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:invoice:audit')")
    public CommonResult<Boolean> auditInvoice(@Valid @RequestBody InvoiceVO.AuditReqVO reqVO) {
        invoiceService.audit(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除开票申请")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:invoice:delete')")
    public CommonResult<Boolean> deleteInvoice(@RequestParam("id") Long id) {
        invoiceService.deleteInvoice(id);
        return success(true);
    }

}
