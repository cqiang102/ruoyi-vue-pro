package cn.iocoder.yudao.module.restaurant.dal.mysql.point;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.point.PointProductDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 积分商品 Mapper（M-27）
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface PointProductMapper extends BaseMapperX<PointProductDO> {

    /**
     * 商品分页（本店）
     */
    default PageResult<PointProductDO> selectPage(PageParam pageParam, Long storeId, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<PointProductDO>()
                .eq(PointProductDO::getStoreId, storeId)
                .eqIfPresent(PointProductDO::getStatus, status)
                .orderByAsc(PointProductDO::getSort)
                .orderByDesc(PointProductDO::getId));
    }

    /**
     * CAS 扣库存：仅当剩余库存充足时扣减，返回影响行数（0 = 库存不足/并发失败）
     * stock = -1（不限）不扣减
     */
    @Update("UPDATE restaurant_point_product SET stock = stock - #{quantity} " +
            "WHERE id = #{id} AND deleted = 0 AND stock >= 0 AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

}
