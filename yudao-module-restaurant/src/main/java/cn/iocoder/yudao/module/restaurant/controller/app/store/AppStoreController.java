package cn.iocoder.yudao.module.restaurant.controller.app.store;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreVO;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import javax.annotation.security.PermitAll;

@Tag(name = "消费者小程序 - 门店")
@RestController
@RequestMapping("/member/store")
@Validated
public class AppStoreController {

    @Resource
    private StoreService storeService;

    @PermitAll // C 端免登录：H5 登录前首屏需要

    @GetMapping("/get")
    @Operation(summary = "获得门店公开信息（含配送费/起送价，下单外卖用）")
    public CommonResult<StoreVO.RespVO> getStore(@RequestParam("id") Long id) {
        return success(storeService.getStore(id));
    }

    @PermitAll // C 端免登录：H5 登录前首屏需要

    @GetMapping("/list")
    @Operation(summary = "门店列表（C-14：传入定位时按距离升序并返回距离公里数）")
    public CommonResult<List<StoreVO.RespVO>> getStoreList(
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {
        return success(storeService.getStoreListForMember(latitude, longitude));
    }

}
