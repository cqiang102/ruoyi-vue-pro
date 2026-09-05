package cn.iocoder.yudao.module.restaurant.dal.mysql.member;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberCardOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员卡购买记录 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface MemberCardOrderMapper extends BaseMapperX<MemberCardOrderDO> {

    default PageResult<MemberCardOrderDO> selectPageByUser(Long userId, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<MemberCardOrderDO>()
                .eq(MemberCardOrderDO::getUserId, userId)
                .orderByDesc(MemberCardOrderDO::getId));
    }

}
