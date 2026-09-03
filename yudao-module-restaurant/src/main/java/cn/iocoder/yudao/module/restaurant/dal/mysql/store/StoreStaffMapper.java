package cn.iocoder.yudao.module.restaurant.dal.mysql.store;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreStaffDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店店员映射 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface StoreStaffMapper extends BaseMapperX<StoreStaffDO> {

    /**
     * 按后台账号编号查询店员映射（uk_admin_user 保证最多一条）
     */
    default StoreStaffDO selectByAdminUserId(Long adminUserId) {
        return selectOne(StoreStaffDO::getAdminUserId, adminUserId);
    }

}
