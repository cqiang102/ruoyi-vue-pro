package cn.iocoder.yudao.module.restaurant.dal.dataobject.dish;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜品加料选项 DO（加蛋/加肠等，可多选，叠加到订单行金额）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_dish_addon")
@Data
@EqualsAndHashCode(callSuper = true)
public class DishAddonDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 菜品编号
     */
    private Long dishId;
    /**
     * 加料组名（如：加料）
     */
    private String groupName;
    /**
     * 选项名（如：加蛋）
     */
    private String optionName;
    /**
     * 加价（分）
     */
    private Long priceDelta;
    /**
     * 是否可多选：1是 0单选
     */
    private Integer multi;
    /**
     * 组内排序
     */
    private Integer sort;

}
