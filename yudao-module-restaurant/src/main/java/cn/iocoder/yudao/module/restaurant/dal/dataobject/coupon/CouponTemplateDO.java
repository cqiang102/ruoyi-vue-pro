package cn.iocoder.yudao.module.restaurant.dal.dataobject.coupon;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 优惠券模板 DO（商户可配，定义一种券的发放规则）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_coupon_template")
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponTemplateDO extends TenantBaseDO {

    /**
     * 模板编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 券名称
     */
    private String name;
    /**
     * 券类型：1-满减 2-折扣
     */
    private Integer type;
    /**
     * 使用门槛金额（单位：分，0 表示无门槛）
     */
    private Integer thresholdAmount;
    /**
     * 优惠值：满减时为减免金额（分）；折扣时为折扣率（95 表示 95 折）
     */
    private Integer discountValue;
    /**
     * 发放总量
     */
    private Integer total;
    /**
     * 已领取数量
     */
    private Integer takenCount;
    /**
     * 每人限领数量（默认 1）
     */
    private Integer perLimit;
    /**
     * 领取后有效天数
     */
    private Integer validDays;
    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;

}
