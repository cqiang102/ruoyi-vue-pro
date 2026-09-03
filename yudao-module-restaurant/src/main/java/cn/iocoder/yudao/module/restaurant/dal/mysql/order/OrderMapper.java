package cn.iocoder.yudao.module.restaurant.dal.mysql.order;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface OrderMapper extends BaseMapperX<OrderDO> {

    /**
     * 按核销码查询订单（核销码全局唯一）
     */
    default OrderDO selectByVerifyCode(String verifyCode) {
        return selectOne(OrderDO::getVerifyCode, verifyCode);
    }

}
