package cn.iocoder.yudao.module.restaurant.controller.app.coupon;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.app.coupon.vo.CouponVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.coupon.CouponDO;
import cn.iocoder.yudao.module.restaurant.service.coupon.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "消费者小程序 - 优惠券")
@RestController
@RequestMapping("/member/coupon")
@Validated
public class CouponController {

    @Resource
    private CouponService couponService;

    @PostMapping("/claim")
    @Operation(summary = "领取优惠券")
    // 越权修复（同 P0-3）：userId 由登录态取，不信任前端明文入参，杜绝为他人领券
    public CommonResult<CouponDO> claim(@RequestParam("templateId") Long templateId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(couponService.claimCoupon(userId, templateId));
    }

    @GetMapping("/my-list")
    @Operation(summary = "我的优惠券列表")
    public CommonResult<List<CouponVO.RespVO>> myList(@RequestParam(value = "status", required = false) Integer status) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(couponService.getMyCoupons(userId, status));
    }

}
