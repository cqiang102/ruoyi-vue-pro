package cn.iocoder.yudao.module.restaurant.dal.dataobject.banner;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 轮播图 DO
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_banner")
@Data
@EqualsAndHashCode(callSuper = true)
public class BannerDO extends TenantBaseDO {

    /**
     * 轮播图编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 图片地址
     */
    private String image;
    /**
     * 跳转类型：0无跳转 1菜品 2门店 3外链
     */
    private Integer linkType;
    /**
     * 跳转目标（菜品编号 / 门店编号 / 外链 URL）
     */
    private String linkValue;
    /**
     * 状态：1启用 0禁用
     */
    private Integer status;
    /**
     * 排序（越大越靠前）
     */
    private Integer sort;

}
