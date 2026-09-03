package cn.iocoder.yudao.module.restaurant.dal.mysql.dish;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishAddonDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品加料选项 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface DishAddonMapper extends BaseMapperX<DishAddonDO> {

}
