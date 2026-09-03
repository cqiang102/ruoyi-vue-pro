package cn.iocoder.yudao.module.restaurant.controller.admin.store;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreStaffVO;
import cn.iocoder.yudao.module.restaurant.service.store.StoreStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 门店店员映射
 * <p>
 * P1-A 闭环：维护 admin 账号 ↔ 门店 的归属映射，让 m14 跑完后
 * StoreAuthService.getLoginUserStoreId() 能正确取到门店编号。
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 门店店员")
@RestController
@RequestMapping("/store-staff")
@Validated
public class StoreStaffController {

    @Resource
    private StoreStaffService storeStaffService;

    @PostMapping("/create")
    @Operation(summary = "绑定店员账号到门店")
    @PreAuthorize("hasAnyAuthority('restaurant:store-staff:create')")
    public CommonResult<Long> createStoreStaff(@RequestBody @Valid StoreStaffVO.SaveReqVO createReqVO) {
        return success(storeStaffService.createStoreStaff(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新店员绑定")
    @PreAuthorize("hasAnyAuthority('restaurant:store-staff:update')")
    public CommonResult<Boolean> updateStoreStaff(@RequestBody @Valid StoreStaffVO.SaveReqVO updateReqVO) {
        storeStaffService.updateStoreStaff(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除店员绑定")
    @PreAuthorize("hasAnyAuthority('restaurant:store-staff:delete')")
    public CommonResult<Boolean> deleteStoreStaff(@RequestParam("id") Long id) {
        storeStaffService.deleteStoreStaff(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得店员绑定详情")
    @PreAuthorize("hasAnyAuthority('restaurant:store-staff:query')")
    public CommonResult<StoreStaffVO.RespVO> getStoreStaff(@RequestParam("id") Long id) {
        return success(storeStaffService.getStoreStaff(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得店员绑定分页")
    @PreAuthorize("hasAnyAuthority('restaurant:store-staff:query')")
    public CommonResult<PageResult<StoreStaffVO.RespVO>> getStoreStaffPage(@Valid StoreStaffVO.PageReqVO pageReqVO) {
        return success(storeStaffService.getStoreStaffPage(pageReqVO));
    }

}
