package cn.iocoder.yudao.module.restaurant.dal.mysql.packageconf;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.packageconf.TenantSubscriptionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户订阅记录 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface TenantSubscriptionMapper extends BaseMapperX<TenantSubscriptionDO> {

    /**
     * 统计引用该套餐的订阅记录数（含已过期/已取消的历史记录）。
     *
     * 用途：删除套餐前做占用校验。restaurant_tenant_subscription.package_id 为 NOT NULL，
     * 若直接物理删除套餐，历史订阅会留下悬空引用（订阅页展示套餐名/时长时取不到数据）。
     * 故删除前必须先确认无任何订阅引用该套餐。
     *
     * @param packageId 套餐编号
     * @return 引用该套餐的订阅记录数
     */
    default Long selectCountByPackageId(Long packageId) {
        return selectCount(TenantSubscriptionDO::getPackageId, packageId);
    }

}
