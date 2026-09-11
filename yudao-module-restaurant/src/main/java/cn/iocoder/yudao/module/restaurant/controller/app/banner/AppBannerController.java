package cn.iocoder.yudao.module.restaurant.controller.app.banner;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.banner.vo.BannerVO;
import cn.iocoder.yudao.module.restaurant.service.banner.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import javax.annotation.security.PermitAll;

@Tag(name = "消费者小程序 - 轮播图")
@RestController
@RequestMapping("/member/banner")
@Validated
public class AppBannerController {

    @Resource
    private BannerService bannerService;

    @PermitAll // C 端免登录：H5 登录前首屏需要

    @GetMapping("/list")
    @Operation(summary = "获得启用中的轮播图列表（首页轮播用）")
    public CommonResult<List<BannerVO.RespVO>> getBannerList() {
        return success(bannerService.getBannerList());
    }

}
