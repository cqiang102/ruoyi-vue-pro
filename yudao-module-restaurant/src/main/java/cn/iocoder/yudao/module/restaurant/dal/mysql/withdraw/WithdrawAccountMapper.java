package cn.iocoder.yudao.module.restaurant.dal.mysql.withdraw;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.withdraw.WithdrawAccountDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现账户 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface WithdrawAccountMapper extends BaseMapperX<WithdrawAccountDO> {

}
