package cn.iocoder.yudao.module.restaurant.dal.dataobject.order;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 餐饮订单明细 DO（菜品行，含规格/加料快照）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_order_item")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderItemDO extends TenantBaseDO {

    /**
     * 明细编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单编号
     */
    private Long orderId;
    /**
     * 菜品编号
     */
    private Long dishId;
    /**
     * 菜品名称（下单时快照）
     */
    private String dishName;
    /**
     * 菜品图片（下单时快照）
     */
    private String image;
    /**
     * 规格描述（如：辣度:微辣）
     */
    private String specDesc;
    /**
     * 加料描述（如：加料:加蛋、加料:加肠）
     */
    private String addonDesc;
    /**
     * 单价（单位：分，含规格加价，未含加料）
     */
    private Long unitPrice;
    /**
     * 加料合计（单位：分）
     */
    private Long addonPrice;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 行总价（单位：分 = unitPrice*quantity + addonPrice）
     */
    private Long totalPrice;

}
