package cn.iocoder.yudao.module.restaurant.dal.mysql.content;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.content.NewsDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 资讯 Mapper（M-10）
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface NewsMapper extends BaseMapperX<NewsDO> {

    /**
     * 会员端可见资讯：本店 + 全平台（store_id=0），只取已发布，sort 升序（同序取新）
     */
    default List<NewsDO> selectVisibleList(Long storeId) {
        return selectList(new LambdaQueryWrapperX<NewsDO>()
                .eq(NewsDO::getStatus, 0)
                .in(NewsDO::getStoreId, 0L, storeId == null ? 0L : storeId)
                .orderByAsc(NewsDO::getSort)
                .orderByDesc(NewsDO::getId));
    }

}
