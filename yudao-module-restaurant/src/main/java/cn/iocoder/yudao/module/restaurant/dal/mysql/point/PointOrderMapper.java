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
     * 兑换记录分页
     *
     * storeId 必须用 eqIfPresent：管理端（本店）会传值，会员端「我的兑换」传 null，
     * 用硬 eq 会导致 SQL 出现 store_id = NULL，任何记录都查不出来（2026-09-21 实测：
     * 会员有 2 条兑换记录，/member/point-shop/my-orders 却返回 total=0，
     * 用户因此看不到自己的核销码）。
     */
    default PageResult<PointOrderDO> selectPage(PageParam pageParam, Long storeId, Long userId, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<PointOrderDO>()
                .eqIfPresent(PointOrderDO::getStoreId, storeId)
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
