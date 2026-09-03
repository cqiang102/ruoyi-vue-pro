package cn.iocoder.yudao.module.restaurant.dal.dataobject.dish;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜品 SPU DO
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_dish")
@Data
@EqualsAndHashCode(callSuper = true)
public class DishDO extends TenantBaseDO {

    /**
     * 菜品编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 分类编号
     */
    private Long categoryId;
    /**
     * 菜品名称
     */
    private String name;
    /**
     * 菜品图片
     */
    private String image;
    /**
     * 描述
     */
    private String description;
    /**
     * 基础价格（单位：分）
     */
    private Long price;
    /**
     * 状态：1上架 0下架
     */
    private Integer status;
    /**
     * 沽清：1已售罄 0否
     */
    private Integer soldOut;
    /**
     * 排序
     */
    private Integer sort;

}
