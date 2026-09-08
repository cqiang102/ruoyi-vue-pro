package cn.iocoder.yudao.module.restaurant.dal.dataobject.notice;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公告 DO（P-07 平台消息）
 *
 * <p>store_id = 0 表示全平台公告；>0 表示门店公告。
 * 正文纯文本（同 C-12 帮助文档策略，MVP 不引入富文本）。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_notice")
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店编号（0 = 全平台）
     */
    private Long storeId;

    private String title;

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
