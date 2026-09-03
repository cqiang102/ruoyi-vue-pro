package cn.iocoder.yudao.module.restaurant.dal.mysql.store;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 门店 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface StoreMapper extends BaseMapperX<StoreDO> {

    /**
     * 对门店行加排他锁（SELECT ... FOR UPDATE）。
     * 用于下单事务内串行化「取餐号生成」（P1-2：selectCount+1 竞态修复），
     * 同一门店的并发下单在锁上排队，保证取餐号不重复。
     */
    @Select("SELECT id FROM restaurant_store WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    Long selectIdForUpdate(@Param("id") Long id);

}
