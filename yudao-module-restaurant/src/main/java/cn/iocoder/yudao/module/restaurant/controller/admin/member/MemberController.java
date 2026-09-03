package cn.iocoder.yudao.module.restaurant.controller.admin.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberVO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 会员档案")
@RestController
@RequestMapping("/store/member")
@Validated
public class MemberController {

    @Resource
    private MemberService memberService;

    @GetMapping("/page")
    @Operation(summary = "会员档案分页")
    @PreAuthorize("hasAnyAuthority('restaurant:member:query')")
    public CommonResult<PageResult<MemberVO.RespVO>> getMemberPage(@Validated MemberVO.PageReqVO pageReqVO) {
        return success(memberService.getMemberPage(pageReqVO));
    }

    @GetMapping("/get-by-user")
    @Operation(summary = "按用户编号查询会员档案")
    @PreAuthorize("hasAnyAuthority('restaurant:member:query')")
    public CommonResult<MemberVO.RespVO> getMemberByUser(@RequestParam("userId") Long userId) {
        return success(memberService.getMemberPage(
                new MemberVO.PageReqVO() {{
                    setUserId(userId);
                }}).getList().stream().findFirst().orElse(null));
    }

}
