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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "消费者小程序 - 门店")
@RestController
@RequestMapping("/member/store")
@Validated
public class AppStoreController {

    @Resource
    private StoreService storeService;

    @GetMapping("/get")
    @Operation(summary = "获得门店公开信息（含配送费/起送价，下单外卖用）")
    public CommonResult<StoreVO.RespVO> getStore(@RequestParam("id") Long id) {
        return success(storeService.getStore(id));
    }

}
