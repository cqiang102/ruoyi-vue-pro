package cn.iocoder.yudao.module.restaurant.dal.mysql.reserve;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.reserve.ReserveRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 预约规则 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface ReserveRuleMapper extends BaseMapperX<ReserveRuleDO> {

    /**
     * 查询某店启用的规则（含「每天」规则）
     */
    default List<ReserveRuleDO> selectEnabledByStore(Long storeId) {
        return selectList(new LambdaQueryWrapperX<ReserveRuleDO>()
                .eq(ReserveRuleDO::getStoreId, storeId)
                .eq(ReserveRuleDO::getStatus, 0)
                .orderByAsc(ReserveRuleDO::getWeekday, ReserveRuleDO::getStartTime));
    }

}
