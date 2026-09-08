package cn.iocoder.yudao.module.restaurant.dal.mysql.notice;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notice.NoticeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 公告 Mapper（P-07）
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface NoticeMapper extends BaseMapperX<NoticeDO> {

    /**
     * 会员端可见公告：本店公告 + 全平台公告（store_id=0），只取已发布
     */
    default List<NoticeDO> selectVisibleList(Long storeId) {
        return selectList(new LambdaQueryWrapperX<NoticeDO>()
                .eq(NoticeDO::getStatus, 0)
                .in(NoticeDO::getStoreId, 0L, storeId == null ? 0L : storeId)
                .orderByDesc(NoticeDO::getId));
    }

}
