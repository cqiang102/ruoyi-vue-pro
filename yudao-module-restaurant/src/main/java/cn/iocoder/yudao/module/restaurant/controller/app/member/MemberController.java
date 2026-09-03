package cn.iocoder.yudao.module.restaurant.controller.app.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberDO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "消费者小程序 - 会员中心")
@RestController
@RequestMapping("/member/me")
@Validated
public class MemberController {

    @Resource
    private MemberService memberService;

    @PostMapping("/bind")
    @Operation(summary = "获取或创建我的会员档案")
    // 越权修复（同 P0-3）：userId 由登录态取，不信任前端明文入参，杜绝盗用他人档案
    public CommonResult<MemberDO> bind() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberService.getOrCreateMember(userId));
    }

    @GetMapping("/get")
    @Operation(summary = "获取我的会员档案")
    public CommonResult<MemberVO.RespVO> get() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        MemberDO member = memberService.getMemberByUserId(userId);
        if (member == null) {
            return success(null);
        }
        return success(memberService.getMemberPage(
                new MemberVO.PageReqVO() {{
                    setUserId(userId);
                }}).getList().stream().findFirst().orElse(null));
    }

}
