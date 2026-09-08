package cn.iocoder.yudao.module.restaurant.controller.app.invoice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.service.invoice.InvoiceService;
import cn.iocoder.yudao.module.restaurant.service.invoice.InvoiceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 消费者小程序 - 电子发票（M-35 MVP）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "消费者小程序 - 电子发票")
@RestController
@RequestMapping("/member/invoice")
@Validated
public class AppInvoiceController {

    @Resource
    private InvoiceService invoiceService;

    // ========== 抬头 ==========

    @GetMapping("/title-list")
    @Operation(summary = "我的发票抬头列表")
    public CommonResult<List<InvoiceVO.TitleRespVO>> getTitleList() {
        return success(invoiceService.getTitleList(getLoginUserId()));
    }

    @PostMapping("/title-save")
    @Operation(summary = "保存抬头（有 id 为更新，无 id 为新增）")
    public CommonResult<Long> saveTitle(@Valid @RequestBody InvoiceVO.TitleSaveReqVO reqVO) {
        Long userId = getLoginUserId();
        if (reqVO.getId() == null) {
            return success(invoiceService.createTitle(userId, reqVO));
        }
        invoiceService.updateTitle(userId, reqVO);
        return success(reqVO.getId());
    }

    @DeleteMapping("/title-delete")
    @Operation(summary = "删除抬头")
    @Parameter(name = "id", required = true)
    public CommonResult<Boolean> deleteTitle(@RequestParam("id") Long id) {
        invoiceService.deleteTitle(getLoginUserId(), id);
        return success(true);
    }

    // ========== 开票申请 ==========

    @PostMapping("/apply")
    @Operation(summary = "申请开票（订单需已支付且未申请过）")
    public CommonResult<Long> apply(@Valid @RequestBody InvoiceVO.ApplyReqVO reqVO) {
        return success(invoiceService.apply(getLoginUserId(), reqVO));
    }

    @GetMapping("/my-list")
    @Operation(summary = "我的开票记录")
    public CommonResult<List<InvoiceVO.RespVO>> getMyInvoices() {
        return success(invoiceService.getInvoiceListByUser(getLoginUserId()));
    }

    private static Long getLoginUserId() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw new IllegalStateException("登录用户不能为空");
        }
        return userId;
    }

}
