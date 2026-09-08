package cn.iocoder.yudao.module.restaurant.dal.dataobject.decor;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 首页装修条目 DO（M-02）
 *
 * <p>MVP 采用「配置式装修」（条目 CRUD + 排序），不做画布拖拽：
 * type：1-轮播 banner（image 全宽图，link 跳转）；2-金刚区入口（image 小图 + title + link）；
 * 3-推荐菜品位（title 标题，渲染时自动取该店菜品销量 TOP，image/link 不用）。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_home_decor")
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeDecorDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 类型：1-轮播 banner 2-金刚区入口 3-推荐菜品位
     */
    private Integer type;

    /**
     * 标题（金刚区名称 / 推荐位标题）
     */
    private String title;

    /**
     * 图片 URL
     */
    private String image;

    /**
     * 跳转路径（页面路径或 https 外链）
     */
    private String link;

    private Integer sort;

    /**
     * 状态：0-启用 1-停用
     */
    private Integer status;

}
