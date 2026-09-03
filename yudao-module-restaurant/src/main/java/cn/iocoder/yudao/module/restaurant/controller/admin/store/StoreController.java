package cn.iocoder.yudao.module.restaurant.controller.admin.store;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreVO;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 门店")
@RestController
@RequestMapping("/store/info")
@Validated
public class StoreController {

    @Resource
    private StoreService storeService;

    @PostMapping("/create")
    @Operation(summary = "创建门店")
    @PreAuthorize("hasAnyAuthority('restaurant:store:create')")
    public CommonResult<Long> createStore(@RequestBody @Valid StoreVO.SaveReqVO createReqVO) {
        return success(storeService.createStore(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新门店")
    @PreAuthorize("hasAnyAuthority('restaurant:store:update')")
    public CommonResult<Boolean> updateStore(@RequestBody @Valid StoreVO.SaveReqVO updateReqVO) {
        storeService.updateStore(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除门店")
    @PreAuthorize("hasAnyAuthority('restaurant:store:delete')")
    public CommonResult<Boolean> deleteStore(@RequestParam("id") Long id) {
        storeService.deleteStore(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得门店")
    @PreAuthorize("hasAnyAuthority('restaurant:store:query')")
    public CommonResult<StoreVO.RespVO> getStore(@RequestParam("id") Long id) {
        return success(storeService.getStore(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得门店分页")
    @PreAuthorize("hasAnyAuthority('restaurant:store:query')")
    public CommonResult<PageResult<StoreVO.RespVO>> getStorePage(@Valid StoreVO.PageReqVO pageReqVO) {
        return success(storeService.getStorePage(pageReqVO));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得门店精简列表（下拉用）")
    @PreAuthorize("hasAnyAuthority('restaurant:store:query')")
    public CommonResult<List<StoreVO.RespVO>> getStoreSimpleList() {
        return success(storeService.getStoreSimpleList());
    }

}
