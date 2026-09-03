package cn.iocoder.yudao.module.restaurant.dal.mysql.store;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.TableDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 桌台 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface TableMapper extends BaseMapperX<TableDO> {

}
