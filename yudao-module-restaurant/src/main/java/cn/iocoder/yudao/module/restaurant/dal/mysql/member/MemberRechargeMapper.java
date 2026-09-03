package cn.iocoder.yudao.module.restaurant.dal.mysql.member;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberRechargeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员储值充值单 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface MemberRechargeMapper extends BaseMapperX<MemberRechargeDO> {

    default MemberRechargeDO selectByOrderNo(String orderNo) {
        return selectOne(MemberRechargeDO::getOrderNo, orderNo);
    }

    default PageResult<MemberRechargeDO> selectPageByUser(Long userId, PageParam pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MemberRechargeDO>()
                .eq(MemberRechargeDO::getUserId, userId)
                .orderByDesc(MemberRechargeDO::getId));
    }

    default List<MemberRechargeDO> selectListByUser(Long userId) {
        return selectList(new LambdaQueryWrapperX<MemberRechargeDO>()
                .eq(MemberRechargeDO::getUserId, userId)
                .orderByDesc(MemberRechargeDO::getId));
    }

}
