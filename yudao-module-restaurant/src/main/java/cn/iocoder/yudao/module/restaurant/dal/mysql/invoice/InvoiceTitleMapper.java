package cn.iocoder.yudao.module.restaurant.dal.mysql.invoice;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.invoice.InvoiceTitleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 发票抬头 Mapper（M-35）
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface InvoiceTitleMapper extends BaseMapperX<InvoiceTitleDO> {

    default List<InvoiceTitleDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<InvoiceTitleDO>()
                .eq(InvoiceTitleDO::getUserId, userId)
                .orderByDesc(InvoiceTitleDO::getIsDefault)
                .orderByDesc(InvoiceTitleDO::getId));
    }

    default InvoiceTitleDO selectDefaultByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<InvoiceTitleDO>()
                .eq(InvoiceTitleDO::getUserId, userId)
                .eq(InvoiceTitleDO::getIsDefault, 1)
                .last("LIMIT 1"));
    }

    /**
     * 校验抬头归属（防水平越权：他人抬头视为不存在）
     */
    default InvoiceTitleDO selectOwned(Long id, Long userId) {
        return selectOne(new LambdaQueryWrapperX<InvoiceTitleDO>()
                .eq(InvoiceTitleDO::getId, id)
                .eq(InvoiceTitleDO::getUserId, userId));
    }

}
