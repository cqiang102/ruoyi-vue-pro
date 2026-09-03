package cn.iocoder.yudao.module.restaurant.service.dish;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo.DishVO;

import java.util.List;

/**
 * 菜品 Service 接口（含规格/加料）
 *
 * @author 餐饮 SaaS
 */
public interface DishService {

    /**
     * 创建菜品
     */
    Long createDish(DishVO.SaveReqVO createReqVO);

    /**
     * 更新菜品
     */
    void updateDish(DishVO.SaveReqVO updateReqVO);

    /**
     * 删除菜品
     */
    void deleteDish(Long id);

    /**
     * 获得菜品（含规格/加料）
     */
    DishVO.RespVO getDish(Long id);

    /**
     * 分页查询菜品（含规格/加料）
     */
    PageResult<DishVO.RespVO> getDishPage(DishVO.PageReqVO pageReqVO);

    /**
     * 获得菜品精简列表（含规格/加料，点餐/下单用）
     */
    List<DishVO.RespVO> getDishSimpleList(Long categoryId);

    /**
     * 获得在售菜品菜单（消费者端，status=1 且未沽清），可按分类过滤
     */
    List<DishVO.RespVO> getDishMenu(Long categoryId);

}
