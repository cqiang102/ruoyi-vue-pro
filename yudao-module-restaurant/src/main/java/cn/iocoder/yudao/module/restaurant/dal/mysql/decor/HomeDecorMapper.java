package cn.iocoder.yudao.module.restaurant.dal.mysql.decor;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.decor.HomeDecorDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 首页装修 Mapper（M-02）
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface HomeDecorMapper extends BaseMapperX<HomeDecorDO> {

    default List<HomeDecorDO> selectEnabledByStore(Long storeId) {
        return selectList(new LambdaQueryWrapperX<HomeDecorDO>()
                .eq(HomeDecorDO::getStoreId, storeId)
                .eq(HomeDecorDO::getStatus, 0)
                .orderByAsc(HomeDecorDO::getSort));
    }

}
