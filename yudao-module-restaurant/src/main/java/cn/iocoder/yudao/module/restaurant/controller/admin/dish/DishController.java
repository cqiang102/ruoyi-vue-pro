package cn.iocoder.yudao.module.restaurant.controller.admin.dish;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo.DishVO;
import cn.iocoder.yudao.module.restaurant.service.dish.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 菜品")
@RestController
@RequestMapping("/store/dish")
@Validated
public class DishController {

    @Resource
    private DishService dishService;

    @PostMapping("/create")
    @Operation(summary = "创建菜品（含规格/加料）")
    @PreAuthorize("hasAnyAuthority('restaurant:dish:create')")
    public CommonResult<Long> createDish(@RequestBody @Valid DishVO.SaveReqVO createReqVO) {
        return success(dishService.createDish(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新菜品（含规格/加料）")
    @PreAuthorize("hasAnyAuthority('restaurant:dish:update')")
    public CommonResult<Boolean> updateDish(@RequestBody @Valid DishVO.SaveReqVO updateReqVO) {
        dishService.updateDish(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除菜品")
    @PreAuthorize("hasAnyAuthority('restaurant:dish:delete')")
    public CommonResult<Boolean> deleteDish(@RequestParam("id") Long id) {
        dishService.deleteDish(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得菜品（含规格/加料）")
    @PreAuthorize("hasAnyAuthority('restaurant:dish:query')")
    public CommonResult<DishVO.RespVO> getDish(@RequestParam("id") Long id) {
        return success(dishService.getDish(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得菜品分页")
    @PreAuthorize("hasAnyAuthority('restaurant:dish:query')")
    public CommonResult<PageResult<DishVO.RespVO>> getDishPage(@Valid DishVO.PageReqVO pageReqVO) {
        return success(dishService.getDishPage(pageReqVO));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得菜品精简列表（含规格/加料，点餐/下单用）")
    @PreAuthorize("hasAnyAuthority('restaurant:dish:query')")
    public CommonResult<List<DishVO.RespVO>> getDishSimpleList(
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        return success(dishService.getDishSimpleList(categoryId));
    }

}
