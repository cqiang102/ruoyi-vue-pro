package cn.iocoder.yudao.module.restaurant.dal.dataobject.point;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分商品 DO（M-27 积分商城）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_point_product")
@Data
@EqualsAndHashCode(callSuper = true)
public class PointProductDO extends TenantBaseDO {

    /**
     * 商品编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店编号（本店隔离）
     */
    private Long storeId;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品图片
     */
    private String image;
    /**
     * 所需积分（单件）
     */
    private Integer points;
    /**
     * 库存（-1 = 不限）
     */
    private Integer stock;
    /**
     * 商品描述
     */
    private String description;
    /**
     * 状态：1上架 0下架
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;

}
