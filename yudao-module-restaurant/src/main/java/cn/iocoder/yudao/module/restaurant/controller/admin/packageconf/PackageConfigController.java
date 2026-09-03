package cn.iocoder.yudao.module.restaurant.controller.admin.packageconf;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.packageconf.vo.PackageConfigVO;
import cn.iocoder.yudao.module.restaurant.service.packageconf.PackageConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 租户套餐定义")
@RestController
@RequestMapping("/platform/package")
@Validated
public class PackageConfigController {

    @Resource
    private PackageConfigService packageConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建套餐")
    @PreAuthorize("hasAnyAuthority('restaurant:package:create')")
    public CommonResult<Long> createPackage(@RequestBody @Valid PackageConfigVO.SaveReqVO reqVO) {
        return success(packageConfigService.createPackage(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新套餐")
    @PreAuthorize("hasAnyAuthority('restaurant:package:update')")
    public CommonResult<Boolean> updatePackage(@RequestBody @Valid PackageConfigVO.SaveReqVO reqVO) {
        packageConfigService.updatePackage(reqVO.getId(), reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除套餐")
    @PreAuthorize("hasAnyAuthority('restaurant:package:delete')")
    public CommonResult<Boolean> deletePackage(@RequestParam("id") Long id) {
        packageConfigService.deletePackage(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "套餐分页")
    @PreAuthorize("hasAnyAuthority('restaurant:package:query')")
    public CommonResult<PageResult<PackageConfigVO.RespVO>> getPackagePage(
            @Validated PackageConfigVO.PageReqVO pageReqVO) {
        return success(packageConfigService.getPackagePage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得套餐")
    @PreAuthorize("hasAnyAuthority('restaurant:package:query')")
    public CommonResult<PackageConfigVO.RespVO> getPackage(@RequestParam("id") Long id) {
        return success(packageConfigService.getPackage(id));
    }

}
