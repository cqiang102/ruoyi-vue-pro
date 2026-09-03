package cn.iocoder.yudao.module.restaurant.controller.admin.dish;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo.DishCategoryVO;
import cn.iocoder.yudao.module.restaurant.service.dish.DishCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 菜品分类
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 菜品分类")
@RestController
@RequestMapping("/store/dish-category")
@Validated
public class DishCategoryController {

    @Resource
    private DishCategoryService dishCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建菜品分类")
    @PreAuthorize("hasAnyAuthority('restaurant:dish-category:create')")
    public CommonResult<Long> createDishCategory(@RequestBody @Valid DishCategoryVO.SaveReqVO createReqVO) {
        return success(dishCategoryService.createDishCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新菜品分类")
    @PreAuthorize("hasAnyAuthority('restaurant:dish-category:update')")
    public CommonResult<Boolean> updateDishCategory(@RequestBody @Valid DishCategoryVO.SaveReqVO updateReqVO) {
        dishCategoryService.updateDishCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除菜品分类")
    @PreAuthorize("hasAnyAuthority('restaurant:dish-category:delete')")
    public CommonResult<Boolean> deleteDishCategory(@RequestParam("id") Long id) {
        dishCategoryService.deleteDishCategory(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得菜品分类")
    @PreAuthorize("hasAnyAuthority('restaurant:dish-category:query')")
    public CommonResult<DishCategoryVO.RespVO> getDishCategory(@RequestParam("id") Long id) {
        return success(dishCategoryService.getDishCategory(id));
    }

    @GetMapping("/page")
    @Operation(summary = "菜品分类分页")
    @PreAuthorize("hasAnyAuthority('restaurant:dish-category:query')")
    public CommonResult<PageResult<DishCategoryVO.RespVO>> getDishCategoryPage(
            @Validated DishCategoryVO.PageReqVO pageReqVO) {
        return success(dishCategoryService.getDishCategoryPage(pageReqVO));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得菜品分类精简列表（下拉用）")
    @PreAuthorize("hasAnyAuthority('restaurant:dish-category:query')")
    public CommonResult<List<DishCategoryVO.RespVO>> getSimpleList() {
        return success(dishCategoryService.getDishCategorySimpleList());
    }

}
