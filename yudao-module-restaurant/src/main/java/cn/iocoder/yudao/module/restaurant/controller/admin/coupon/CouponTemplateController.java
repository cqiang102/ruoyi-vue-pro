package cn.iocoder.yudao.module.restaurant.controller.admin.coupon;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.coupon.vo.CouponTemplateVO;
import cn.iocoder.yudao.module.restaurant.service.coupon.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 优惠券模板")
@RestController
@RequestMapping("/store/coupon-template")
@Validated
public class CouponTemplateController {

    @Resource
    private CouponService couponService;

    @PostMapping("/create")
    @Operation(summary = "创建优惠券模板")
    @PreAuthorize("hasAnyAuthority('restaurant:coupon-template:create')")
    public CommonResult<Long> createTemplate(@RequestBody @Valid CouponTemplateVO.SaveReqVO reqVO) {
        return success(couponService.createTemplate(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新优惠券模板")
    @PreAuthorize("hasAnyAuthority('restaurant:coupon-template:update')")
    public CommonResult<Boolean> updateTemplate(@RequestBody @Valid CouponTemplateVO.SaveReqVO reqVO) {
        couponService.updateTemplate(reqVO.getId(), reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除优惠券模板")
    @PreAuthorize("hasAnyAuthority('restaurant:coupon-template:delete')")
    public CommonResult<Boolean> deleteTemplate(@RequestParam("id") Long id) {
        couponService.deleteTemplate(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "优惠券模板分页")
    @PreAuthorize("hasAnyAuthority('restaurant:coupon-template:query')")
    public CommonResult<PageResult<CouponTemplateVO.RespVO>> getTemplatePage(
            @Validated CouponTemplateVO.PageReqVO pageReqVO) {
        return success(couponService.getTemplatePage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得优惠券模板")
    @PreAuthorize("hasAnyAuthority('restaurant:coupon-template:query')")
    public CommonResult<CouponTemplateVO.RespVO> getTemplate(@RequestParam("id") Long id) {
        return success(couponService.getTemplate(id));
    }

}
