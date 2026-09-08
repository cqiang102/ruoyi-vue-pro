package cn.iocoder.yudao.module.restaurant.dal.dataobject.help;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帮助/关于文档 DO（C-12 用户中心）
 *
 * <p>type：1-帮助中心 2-关于我们。内容为纯文本（换行保留），小程序端直接展示，
 * 不引入富文本编辑器（MVP 阶段避免存储 HTML 带来的 XSS 与排版成本）。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_help_doc")
@Data
@EqualsAndHashCode(callSuper = true)
public class HelpDocDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 类型：1-帮助 2-关于
     */
    private Integer type;

    /**
     * 标题
     */
    private String title;

    /**
     * 正文（纯文本）
     */
    private String content;

    private Integer sort;

    /**
     * 状态：0-启用 1-停用
     */
    private Integer status;

}
