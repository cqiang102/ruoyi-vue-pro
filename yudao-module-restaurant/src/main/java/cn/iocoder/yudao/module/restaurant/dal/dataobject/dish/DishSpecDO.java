package cn.iocoder.yudao.module.restaurant.dal.dataobject.dish;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜品规格选项 DO（份量/辣度等，单选项，叠加到菜品基础价）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_dish_spec")
@Data
@EqualsAndHashCode(callSuper = true)
public class DishSpecDO extends TenantBaseDO {

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
     * 规格组名（如：辣度）
     */
    private String groupName;
    /**
     * 选项名（如：微辣）
     */
    private String optionName;
    /**
     * 加价（分，可为负）
     */
    private Long priceDelta;
    /**
     * 组内排序
     */
    private Integer sort;

}
