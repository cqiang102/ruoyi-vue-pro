package cn.iocoder.yudao.module.restaurant.dal.dataobject.dish;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜品分类 DO
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_dish_category")
@Data
@EqualsAndHashCode(callSuper = true)
public class DishCategoryDO extends TenantBaseDO {

    /**
     * 分类编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态：1上架 0下架
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
