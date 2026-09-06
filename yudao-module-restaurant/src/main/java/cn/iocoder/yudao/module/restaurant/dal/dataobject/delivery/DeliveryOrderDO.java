package cn.iocoder.yudao.module.restaurant.dal.dataobject.delivery;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送运单 DO（M-11 达达快送）：一单一条（uk order_id），发单失败可重发（同 origin_id 复用）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_delivery_order")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryOrderDO extends TenantBaseDO {

    /**
     * 运单编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 餐饮订单编号（唯一：一单最多一条进行中运单，取消后可重发，重发更新本条）
     */
    private Long orderId;
    /**
     * 门店编号（冗余，本店隔离）
     */
    private Long storeId;
    /**
     * 达达运单号 client_id（接单后回调/查询返回）
     */
    private String dadaOrderId;
    /**
     * 第三方幂等单号 origin_id（= 餐饮订单号字符串）
     */
    private String originId;
    /**
     * 状态：0待发单 1待接单 2待取货 3配送中 4已送达 5已取消 9妥投异常 10发单失败
     * （达达状态映射：1待接单→1、2待取货/100骑士到店→2、3配送中→3、4已完成→4、5已取消→5、
     *   9/10妥投异常→9、1000创建失败→10）
     */
    private Integer status;
    /**
     * 运费（分，达达返回 fee）
     */
    private BigDecimal fee;
    /**
     * 失败/取消原因
     */
    private String errorMsg;
    /**
     * 骑手姓名（接单后回调返回）
     */
    private String dmName;
    /**
     * 骑手手机号
     */
    private String dmMobile;
    /**
     * 最后回调时间
     */
    private LocalDateTime callbackTime;

}
