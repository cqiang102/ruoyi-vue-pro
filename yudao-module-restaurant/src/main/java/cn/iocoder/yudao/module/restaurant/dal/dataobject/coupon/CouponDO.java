package cn.iocoder.yudao.module.restaurant.dal.dataobject.coupon;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户优惠券实例 DO（领取后生成一条）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_coupon")
@Data
@EqualsAndHashCode(callSuper = true)
public class CouponDO extends TenantBaseDO {

    /**
     * 券实例编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 模板编号
     */
    private Long templateId;
    /**
     * 券码
     */
    private String code;
    /**
     * 状态：0-未使用 1-已使用 2-已过期
     */
    private Integer status;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    /**
     * 使用时间
     */
    private LocalDateTime usedTime;
    /**
     * 核销使用的订单编号
     */
    private Long usedOrderId;

}
