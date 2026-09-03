package cn.iocoder.yudao.module.restaurant.dal.mysql.dish;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishCategoryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品分类 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface DishCategoryMapper extends BaseMapperX<DishCategoryDO> {

}
