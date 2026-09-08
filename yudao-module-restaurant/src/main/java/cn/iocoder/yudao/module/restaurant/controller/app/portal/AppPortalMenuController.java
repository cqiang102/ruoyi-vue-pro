package cn.iocoder.yudao.module.restaurant.controller.app.portal;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.portal.vo.PortalMenuVO;
import cn.iocoder.yudao.module.restaurant.service.portal.PortalMenuService;
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
 * 消费者小程序 - 我的服务菜单（M-24）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "消费者小程序 - 我的服务菜单")
@RestController
@RequestMapping("/member/portal-menu")
@Validated
public class AppPortalMenuController {

    @Resource
    private PortalMenuService portalMenuService;

    @GetMapping("/list")
    @Operation(summary = "会员可见菜单（本店优先，回退平台默认）")
    @Parameter(name = "storeId", description = "门店编号", required = true, example = "1")
    public CommonResult<List<PortalMenuVO>> getMemberMenuList(@RequestParam("storeId") Long storeId) {
        // 需登录：租户上下文
        SecurityFrameworkUtils.getLoginUserId();
        return success(portalMenuService.getMemberMenuList(storeId));
    }

}
