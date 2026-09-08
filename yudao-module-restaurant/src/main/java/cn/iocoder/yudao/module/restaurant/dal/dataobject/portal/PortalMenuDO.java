package cn.iocoder.yudao.module.restaurant.dal.dataobject.portal;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 我的服务菜单项 DO（M-24：个人中心菜单配置，商户可增删排序）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_portal_menu")
@Data
@EqualsAndHashCode(callSuper = true)
public class PortalMenuDO extends TenantBaseDO {

    /**
     * 菜单项编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店编号（0 = 平台默认，全部门店可见）
     */
    private Long storeId;
    /**
     * 菜单名称（如「会员储值」）
     */
    private String name;
    /**
     * 图标（可选，emoji 或图标名）
     */
    private String icon;
    /**
     * 跳转路径（uniapp 页面路径如 /pages/restaurant/recharge，或 https:// 外链）
     */
    private String path;
    /**
     * 排序（越小越靠前）
     */
    private Integer sort;
    /**
     * 状态：0-启用 1-停用
     */
    private Integer status;

}
