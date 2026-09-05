package cn.iocoder.yudao.module.restaurant.controller.app.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberAddressVO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "消费者小程序 - 会员收货地址")
@RestController
@RequestMapping("/member/address")
@Validated
public class MemberAddressController {

    @Resource
    private MemberAddressService memberAddressService;

    @GetMapping("/my-list")
    @Operation(summary = "获得我的地址列表（默认地址在前）")
    public CommonResult<List<MemberAddressVO.RespVO>> getMyAddressList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberAddressService.getAddressList(userId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建我的地址")
    // 越权修复（同 P0-3）：userId 由登录态取，不信任前端明文入参
    public CommonResult<Long> createAddress(@RequestBody @Valid MemberAddressVO.SaveReqVO createReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberAddressService.createAddress(userId, createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新我的地址（校验归属）")
    public CommonResult<Boolean> updateAddress(@RequestBody @Valid MemberAddressVO.SaveReqVO updateReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        memberAddressService.updateAddress(userId, updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除我的地址（校验归属）")
    public CommonResult<Boolean> deleteAddress(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        memberAddressService.deleteAddress(userId, id);
        return success(true);
    }

    @PutMapping("/set-default")
    @Operation(summary = "设为默认地址（校验归属）")
    public CommonResult<Boolean> setDefaultAddress(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        memberAddressService.setDefaultAddress(userId, id);
        return success(true);
    }

}
