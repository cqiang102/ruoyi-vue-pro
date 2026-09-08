package cn.iocoder.yudao.module.restaurant.dal.mysql.invoice;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.invoice.InvoiceDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 开票申请 Mapper（M-35）
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface InvoiceMapper extends BaseMapperX<InvoiceDO> {

    default InvoiceDO selectByOrderId(Long orderId) {
        return selectOne(new LambdaQueryWrapperX<InvoiceDO>()
                .eq(InvoiceDO::getOrderId, orderId)
                .last("LIMIT 1"));
    }

}
