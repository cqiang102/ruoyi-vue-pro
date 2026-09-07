package cn.iocoder.yudao.module.restaurant.dal.mysql.notify;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.NotifyRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订阅消息发送记录 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface NotifyRecordMapper extends BaseMapperX<NotifyRecordDO> {

    /**
     * 记录分页（storeId 隔离）
     */
    default PageResult<NotifyRecordDO> selectPage(PageParam pageParam, Long storeId, String scene, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<NotifyRecordDO>()
                .eqIfPresent(NotifyRecordDO::getStoreId, storeId)
                .eqIfPresent(NotifyRecordDO::getScene, scene)
                .eqIfPresent(NotifyRecordDO::getStatus, status)
                .orderByDesc(NotifyRecordDO::getId));
    }

}
