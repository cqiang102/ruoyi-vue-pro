package cn.iocoder.yudao.module.restaurant.dal.mysql.dish;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishSpecDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品规格选项 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface DishSpecMapper extends BaseMapperX<DishSpecDO> {

}
