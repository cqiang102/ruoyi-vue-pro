package cn.iocoder.yudao.module.restaurant.dal.dataobject.packageconf;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 租户订阅记录 DO（每个租户一条有效订阅）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_tenant_subscription")
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantSubscriptionDO extends TenantBaseDO {

    /**
     * 订阅编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 租户编号
     */
    private Long tenantId;
    /**
     * 套餐编号
     */
    private Long packageId;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 到期时间
     */
    private LocalDateTime expireTime;
    /**
     * 状态：1-生效中 2-已过期
     */
    private Integer status;
    /**
     * 关联的支付订单号（pay_order.id），年费支付成功后回写
     */
    private Long payOrderId;
    /**
     * 实付金额（分）
     */
    private Integer amount;

}
