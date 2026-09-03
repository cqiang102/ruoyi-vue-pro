package cn.iocoder.yudao.module.restaurant.dal.mysql.dish;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品 SPU Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface DishMapper extends BaseMapperX<DishDO> {

}
