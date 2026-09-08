package cn.iocoder.yudao.module.restaurant.controller.admin.portal;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.portal.vo.PortalMenuVO;
import cn.iocoder.yudao.module.restaurant.service.portal.PortalMenuService;
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
 * 管理后台 - 我的服务菜单（M-24）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 我的服务菜单")
@RestController
@RequestMapping("/store/portal-menu")
@Validated
public class PortalMenuController {

    @Resource
    private PortalMenuService portalMenuService;

    @PostMapping("/create")
    @Operation(summary = "新增菜单项")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:portal-menu:create')")
    public CommonResult<Long> createPortalMenu(@Valid @RequestBody PortalMenuVO reqVO) {
        return success(portalMenuService.createPortalMenu(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新菜单项")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:portal-menu:update')")
    public CommonResult<Boolean> updatePortalMenu(@Valid @RequestBody PortalMenuVO reqVO) {
        portalMenuService.updatePortalMenu(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除菜单项")
    @Parameter(name = "id", description = "菜单项编号", required = true, example = "1")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:portal-menu:delete')")
    public CommonResult<Boolean> deletePortalMenu(@RequestParam("id") Long id) {
        portalMenuService.deletePortalMenu(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "菜单项列表（含停用，按 sort 升序）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:portal-menu:query')")
    public CommonResult<List<PortalMenuVO>> getPortalMenuList() {
        return success(portalMenuService.getPortalMenuList());
    }

}
