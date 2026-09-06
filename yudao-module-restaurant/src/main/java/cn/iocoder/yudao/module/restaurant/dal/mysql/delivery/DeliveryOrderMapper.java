package cn.iocoder.yudao.module.restaurant.dal.mysql.delivery;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.delivery.DeliveryOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 配送运单 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface DeliveryOrderMapper extends BaseMapperX<DeliveryOrderDO> {

    /**
     * 按餐饮订单取运单（一单一条）
     */
    default DeliveryOrderDO selectByOrderId(Long orderId) {
        return selectOne(DeliveryOrderDO::getOrderId, orderId);
    }

    /**
     * 按达达 origin_id 取运单（回调用，调用方需在忽略租户上下文中执行）
     */
    default DeliveryOrderDO selectByOriginId(String originId) {
        return selectOne(DeliveryOrderDO::getOriginId, originId);
    }

    /**
     * 运单分页（storeId 必传：P1-A 本店隔离）
     */
    default PageResult<DeliveryOrderDO> selectPage(PageParam pageParam, Long storeId, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<DeliveryOrderDO>()
                .eq(DeliveryOrderDO::getStoreId, storeId)
                .eqIfPresent(DeliveryOrderDO::getStatus, status)
                .orderByDesc(DeliveryOrderDO::getId));
    }

}
