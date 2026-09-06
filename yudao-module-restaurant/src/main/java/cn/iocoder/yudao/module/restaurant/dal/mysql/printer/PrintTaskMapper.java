package cn.iocoder.yudao.module.restaurant.dal.mysql.printer;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrintTaskDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打印任务 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface PrintTaskMapper extends BaseMapperX<PrintTaskDO> {

    /**
     * 打印任务分页（storeId 必传：P1-A 本店任务隔离由调用方强制注入）
     */
    default PageResult<PrintTaskDO> selectPage(PageParam pageParam, Long storeId, Long orderId, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<PrintTaskDO>()
                .eq(PrintTaskDO::getStoreId, storeId)
                .eqIfPresent(PrintTaskDO::getOrderId, orderId)
                .eqIfPresent(PrintTaskDO::getStatus, status)
                .orderByDesc(PrintTaskDO::getId));
    }

}
