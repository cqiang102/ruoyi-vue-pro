package cn.iocoder.yudao.module.restaurant.dal.mysql.ticket;

import cn.iocoder.yudao.module.restaurant.dal.dataobject.ticket.RestaurantTicketDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单 Mapper（P-07）
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface RestaurantTicketMapper extends BaseMapperX<RestaurantTicketDO> {

    /**
     * 门店端：只看本门店的工单
     */
    default Long countByStore(Long storeId) {
        return selectCount(new LambdaQueryWrapperX<RestaurantTicketDO>()
                .eq(RestaurantTicketDO::getStoreId, storeId));
    }

}
