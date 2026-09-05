package cn.iocoder.yudao.module.restaurant.dal.dataobject.member;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会员卡购买记录 DO（M-26）
 *
 * MVP 用余额支付：事务内「扣余额 → CAS 累加 sold_count → 插入已支付记录」。
 * 微信支付购卡后续接入 OrderPayService 回调链路时，status 复用 0待支付/1已支付。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_member_card_order")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberCardOrderDO extends TenantBaseDO {

    /**
     * 记录编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 购卡单号（MCD- 前缀，后续接支付回调路由用）
     */
    private String orderNo;
    /**
     * 用户编号（system_users.id，登录态取）
     */
    private Long userId;
    /**
     * 卡编号
     */
    private Long cardId;
    /**
     * 卡名称快照（卡改名不影响历史记录展示）
     */
    private String cardName;
    /**
     * 实付金额快照（单位：分）
     */
    private Long price;
    /**
     * 支付方式：2余额（后续扩展 1微信）
     */
    private Integer payType;
    /**
     * 状态：0待支付 1已支付
     */
    private Integer status;
    /**
     * 支付时间
     */
    private LocalDateTime paidTime;

}
