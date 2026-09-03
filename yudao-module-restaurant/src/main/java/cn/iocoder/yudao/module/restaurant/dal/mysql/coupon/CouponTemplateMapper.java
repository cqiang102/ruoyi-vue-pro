package cn.iocoder.yudao.module.restaurant.dal.mysql.coupon;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.coupon.CouponTemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券模板 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface CouponTemplateMapper extends BaseMapperX<CouponTemplateDO> {

}
