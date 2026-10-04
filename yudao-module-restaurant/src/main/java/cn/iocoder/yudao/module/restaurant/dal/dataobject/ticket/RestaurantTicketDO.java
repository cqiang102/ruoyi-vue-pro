package cn.iocoder.yudao.module.restaurant.dal.dataobject.ticket;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 工单 DO（P-07 平台消息·工单）
 *
 * <p>商户/门店向平台提交问题反馈，平台回复并关闭。
 * 正文与回复均为纯文本（同 C-12/P-07 其他内容表的策略，MVP 不引入富文本）。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_ticket")
@Data
@EqualsAndHashCode(callSuper = true)
public class RestaurantTicketDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店编号（提交方所在门店；由登录账号绑定的门店注入）
     */
    private Long storeId;

    /**
     * 标题
     */
    private String title;

    /**
     * 正文（纯文本）
     */
    private String content;

    /**
     * 类型：1-功能建议 2-故障报修 3-结算咨询 4-其他
     */
    private Integer type;

    /**
     * 状态：0-待处理 1-已回复 2-已关闭
     */
    private Integer status;

    /**
     * 平台回复（纯文本）
     */
    private String reply;

    /**
     * 回复人（后台用户编号）
     */
    private Long replyUserId;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

}
