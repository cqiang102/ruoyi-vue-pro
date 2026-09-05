package cn.iocoder.yudao.module.restaurant.controller.admin.banner;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.banner.vo.BannerVO;
import cn.iocoder.yudao.module.restaurant.service.banner.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 轮播图")
@RestController
@RequestMapping("/store/banner")
@Validated
public class BannerController {

    @Resource
    private BannerService bannerService;

    @PostMapping("/create")
    @Operation(summary = "创建轮播图")
    @PreAuthorize("hasAnyAuthority('restaurant:banner:create')")
    public CommonResult<Long> createBanner(@RequestBody @Valid BannerVO.SaveReqVO createReqVO) {
        return success(bannerService.createBanner(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新轮播图")
    @PreAuthorize("hasAnyAuthority('restaurant:banner:update')")
    public CommonResult<Boolean> updateBanner(@RequestBody @Valid BannerVO.SaveReqVO updateReqVO) {
        bannerService.updateBanner(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除轮播图")
    @PreAuthorize("hasAnyAuthority('restaurant:banner:delete')")
    public CommonResult<Boolean> deleteBanner(@RequestParam("id") Long id) {
        bannerService.deleteBanner(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得轮播图")
    @PreAuthorize("hasAnyAuthority('restaurant:banner:query')")
    public CommonResult<BannerVO.RespVO> getBanner(@RequestParam("id") Long id) {
        return success(bannerService.getBanner(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得轮播图分页")
    @PreAuthorize("hasAnyAuthority('restaurant:banner:query')")
    public CommonResult<PageResult<BannerVO.RespVO>> getBannerPage(@Valid BannerVO.PageReqVO pageReqVO) {
        return success(bannerService.getBannerPage(pageReqVO));
    }

}
