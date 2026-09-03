package cn.iocoder.yudao.module.restaurant.dal.dataobject.member;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会员储值充值单 DO
 * <p>
 * 储值走与订单相同的微信支付通道（芋道 PayApp），支付成功后由回调路由到此单，
 * 调用 {@code WalletPayService.recharge} 将本金+赠额并入会员钱包（芋道 PayWallet）。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_member_recharge")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberRechargeDO extends TenantBaseDO {

    /**
     * 充值单编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户编号（餐饮会员对应用户）
     */
    private Long userId;

    /**
     * 用户类型（芋道 UserTypeEnum.MEMBER=2）
     */
    private Integer userType;

    /**
     * 充值本金（单位：分）
     */
    private Long payAmount;

    /**
     * 赠送金额（单位：分），默认 0
     */
    private Long giftAmount;

    /**
     * 实际到账总额 = payAmount + giftAmount（单位：分）
     */
    private Long totalAmount;

    /**
     * 关联的 pay_order.id（微信支付单）
     */
    private Long payOrderId;

    /**
     * 支付应用标识（定位 PayApp）
     */
    private String appKey;

    /**
     * 充值单号（商户内唯一，RCG- 前缀，作为支付幂等键与回调路由键）
     */
    private String orderNo;

    /**
     * 状态：0待支付 1已充值
     */
    private Integer status;

    /**
     * 支付状态：0未付 1已付
     */
    private Integer payStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 支付时间
     */
    private LocalDateTime paidTime;

}
