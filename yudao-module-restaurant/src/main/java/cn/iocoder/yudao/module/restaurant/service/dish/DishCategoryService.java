package cn.iocoder.yudao.module.restaurant.service.dish;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo.DishCategoryVO;

import java.util.List;

/**
 * 菜品分类 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface DishCategoryService {

    /**
     * 创建菜品分类
     */
    Long createDishCategory(DishCategoryVO.SaveReqVO createReqVO);

    /**
     * 更新菜品分类
     */
    void updateDishCategory(DishCategoryVO.SaveReqVO updateReqVO);

    /**
     * 删除菜品分类
     */
    void deleteDishCategory(Long id);

    /**
     * 获得菜品分类
     */
    DishCategoryVO.RespVO getDishCategory(Long id);

    /**
     * 分页查询菜品分类
     */
    PageResult<DishCategoryVO.RespVO> getDishCategoryPage(DishCategoryVO.PageReqVO pageReqVO);

    /**
     * 获得全部分类（下拉用）
     */
    List<DishCategoryVO.RespVO> getDishCategorySimpleList();

    /**
     * 获得在售分类列表（消费者端菜单 tab 用）
     */
    List<DishCategoryVO.RespVO> getEnabledCategoryList();

}
