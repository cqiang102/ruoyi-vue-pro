package cn.iocoder.yudao.module.restaurant.controller.app.help;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.service.help.HelpDocService;
import cn.iocoder.yudao.module.restaurant.service.help.HelpDocVO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberConsumeSummaryVO;
import cn.iocoder.yudao.module.restaurant.service.member.RestaurantMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 消费者小程序 - 用户中心（C-12）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "消费者小程序 - 用户中心")
@RestController
@RequestMapping("/member/user-center")
@Validated
public class AppUserCenterController {

    @Resource
    private RestaurantMemberService memberService;

    @Resource
    private HelpDocService helpDocService;

    @GetMapping("/consume-summary")
    @Operation(summary = "历史消费汇总（累计金额/订单数/最近下单）")
    public CommonResult<MemberConsumeSummaryVO> getConsumeSummary() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberService.getConsumeSummary(userId));
    }

    @GetMapping("/help-list")
    @Operation(summary = "帮助/关于列表（只返回启用项）")
    @Parameter(name = "type", description = "1-帮助 2-关于", required = true)
    public CommonResult<List<HelpDocVO>> getHelpList(@RequestParam("type") Integer type) {
        // 需登录：租户上下文
        SecurityFrameworkUtils.getLoginUserId();
        return success(helpDocService.getHelpDocListForMember(type));
    }

}
