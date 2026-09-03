package cn.iocoder.yudao.module.restaurant.controller.app.dish;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo.DishCategoryVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo.DishVO;
import cn.iocoder.yudao.module.restaurant.service.dish.DishCategoryService;
import cn.iocoder.yudao.module.restaurant.service.dish.DishService;
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

@Tag(name = "消费者小程序 - 菜品")
@RestController
@RequestMapping("/member/dish")
@Validated
public class AppDishController {

    @Resource
    private DishService dishService;
    @Resource
    private DishCategoryService dishCategoryService;

    @GetMapping("/category-list")
    @Operation(summary = "获得在售分类列表（菜单 tab 用）")
    public CommonResult<List<DishCategoryVO.RespVO>> getCategoryList() {
        return success(dishCategoryService.getEnabledCategoryList());
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得在售菜品列表（含规格/加料，点餐用）")
    public CommonResult<List<DishVO.RespVO>> getDishSimpleList(
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        return success(dishService.getDishMenu(categoryId));
    }

}
