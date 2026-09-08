package cn.iocoder.yudao.module.restaurant.dal.dataobject.content;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资讯 DO（M-10 内容管理）
 *
 * <p>广告图已由 banner 模块（M-17）覆盖，本表只承载「新闻/资讯」。
 * store_id = 0 表示全平台资讯；>0 表示门店资讯。正文纯文本（同公告策略）。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_news")
@Data
@EqualsAndHashCode(callSuper = true)
public class NewsDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店编号（0 = 全平台）
     */
    private Long storeId;

    private String title;

    /**
     * 摘要（列表展示，可空）
     */
    private String summary;

    /**
     * 正文（纯文本）
     */
    private String content;

    private Integer sort;

    /**
     * 状态：0-发布 1-下线
     */
    private Integer status;

}
