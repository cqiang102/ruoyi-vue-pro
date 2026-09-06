package cn.iocoder.yudao.module.restaurant.dal.mysql.printer;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrinterDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 云打印机 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface PrinterMapper extends BaseMapperX<PrinterDO> {

    /**
     * 查询门店下启用的打印机列表（按 sort 升序）
     */
    default List<PrinterDO> selectEnabledListByStore(Long storeId) {
        return selectList(new LambdaQueryWrapperX<PrinterDO>()
                .eq(PrinterDO::getStoreId, storeId)
                .eq(PrinterDO::getStatus, 1)
                .orderByAsc(PrinterDO::getSort));
    }

}
