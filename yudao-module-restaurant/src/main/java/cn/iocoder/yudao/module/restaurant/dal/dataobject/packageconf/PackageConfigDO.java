package cn.iocoder.yudao.module.restaurant.dal.dataobject.packageconf;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户套餐定义 DO（SaaS 平台级，跨租户共享，不做 tenant_id 隔离）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_package")
@Data
@EqualsAndHashCode(callSuper = true)
public class PackageConfigDO extends BaseDO {

    /**
     * 套餐编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 套餐名称
     */
    private String name;
    /**
     * 年费价格（单位：分）
     */
    private Integer price;
    /**
     * 有效期（月），年费通常为 12
     */
    private Integer durationMonths;
    /**
     * 门店数量上限
     */
    private Integer maxStores;
    /**
     * 功能开关（JSON 字符串，如 {"member":true,"coupon":true}）
     */
    private String features;
    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
