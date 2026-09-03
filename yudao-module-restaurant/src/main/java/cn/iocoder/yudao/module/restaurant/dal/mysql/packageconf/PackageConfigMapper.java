package cn.iocoder.yudao.module.restaurant.dal.mysql.packageconf;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.packageconf.PackageConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户套餐定义 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface PackageConfigMapper extends BaseMapperX<PackageConfigDO> {

}
