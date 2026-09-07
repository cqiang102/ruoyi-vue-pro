package cn.iocoder.yudao.module.restaurant.dal.mysql.point;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.point.PointOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分兑换记录 Mapper（M-27）
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface PointOrderMapper extends BaseMapperX<PointOrderDO> {

    /**
     * 兑换记录分页（本店）
     */
    default PageResult<PointOrderDO> selectPage(PageParam pageParam, Long storeId, Long userId, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<PointOrderDO>()
                .eq(PointOrderDO::getStoreId, storeId)
                .eqIfPresent(PointOrderDO::getUserId, userId)
                .eqIfPresent(PointOrderDO::getStatus, status)
                .orderByDesc(PointOrderDO::getId));
    }

    /**
     * 按核销码取兑换记录（店员核销用）
     */
    default PointOrderDO selectByVerifyCode(String verifyCode) {
        return selectOne(PointOrderDO::getVerifyCode, verifyCode);
    }

}
