package cn.iocoder.yudao.module.restaurant.dal.mysql.delivery;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.delivery.DeliveryConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店配送配置 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface DeliveryConfigMapper extends BaseMapperX<DeliveryConfigDO> {

    /**
     * 按门店取配置（一店一条）
     */
    default DeliveryConfigDO selectByStoreId(Long storeId) {
        return selectOne(DeliveryConfigDO::getStoreId, storeId);
    }

}
