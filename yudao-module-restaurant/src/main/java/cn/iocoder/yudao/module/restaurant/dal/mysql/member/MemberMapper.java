package cn.iocoder.yudao.module.restaurant.dal.mysql.member;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员档案 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface MemberMapper extends BaseMapperX<MemberDO> {

}
