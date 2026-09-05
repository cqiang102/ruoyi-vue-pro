package cn.iocoder.yudao.module.restaurant.controller.admin.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberAddressVO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 会员收货地址")
@RestController
@RequestMapping("/store/member-address")
@Validated
public class MemberAddressController {

    @Resource
    private MemberAddressService memberAddressService;

    @GetMapping("/page")
    @Operation(summary = "获得会员地址分页（按 userId 过滤，客服排查用）")
    @PreAuthorize("hasAnyAuthority('restaurant:member-address:query')")
    public CommonResult<PageResult<MemberAddressVO.RespVO>> getAddressPage(
            @Valid MemberAddressVO.PageReqVO pageReqVO) {
        return success(memberAddressService.getAddressPage(pageReqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会员地址")
    @PreAuthorize("hasAnyAuthority('restaurant:member-address:delete')")
    public CommonResult<Boolean> deleteAddress(@RequestParam("id") Long id) {
        memberAddressService.deleteAddressByAdmin(id);
        return success(true);
    }

}
