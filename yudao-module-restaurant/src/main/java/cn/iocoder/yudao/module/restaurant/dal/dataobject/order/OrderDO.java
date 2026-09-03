package cn.iocoder.yudao.module.restaurant.dal.dataobject.order;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 餐饮订单 DO（主单）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_order")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDO extends TenantBaseDO {

    /**
     * 订单编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店编号
     */
    private Long storeId;
    /**
     * 桌台编号（堂食必填）
     */
    private Long tableId;
    /**
     * 订单号（业务唯一，作支付幂等键）
     */
    private String orderNo;
    /**
     * 订单类型：1堂食 2自取 3外卖
     */
    private Integer type;
    /**
     * 订单状态：1待支付 2已支付 3制作中 4已完成 5已取消 6退款中
     */
    private Integer status;
    /**
     * 订单总金额（单位：分）
     */
    private Long totalPrice;
    /**
     * 实付金额（单位：分）
     */
    private Long payPrice;
    /**
     * 优惠金额（单位：分）
     */
    private Long discountPrice;
    /**
     * 配送费（单位：分，外卖订单计入总价）
     */
    private Long deliveryFee;
    /**
     * 收货人姓名（外卖必填）
     */
    private String receiverName;
    /**
     * 收货人电话（外卖必填）
     */
    private String receiverPhone;
    /**
     * 收货地址（外卖必填）
     */
    private String receiverAddress;
    /**
     * 支付方式：0未付 1微信 2余额 3组合
     */
    private Integer payType;
    /**
     * 支付状态：0未付 1已付 2退款中 3已退
     */
    private Integer payStatus;
    /**
     * 下单用户编号（system member 用户 id）
     * <p>
     * app 端下单时由服务端从登录态强制写入（防止伪造）；
     * admin 端代客下单时为空（散客）。归属校验以此字段为准。
     */
    private Long userId;
    /**
     * 会员编号（余额/会员价用）
     */
    private Long memberId;
    /**
     * 使用的优惠券编号（restaurant_coupon.id）
     */
    private Long couponId;
    /**
     * 就餐人数
     */
    private Integer peopleCount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 关联的 pay_order.id（微信支付单）
     */
    private Long payOrderId;
    /**
     * 微信支付使用的 PayApp 标识（退款定位 PayApp 用，余额支付为空）
     */
    private String appKey;
    /**
     * 支付时间
     */
    private LocalDateTime paidTime;
    /**
     * 完成时间
     */
    private LocalDateTime finishTime;
    /**
     * 退款时间
     */
    private LocalDateTime refundTime;
    /**
     * 退款金额（单位：分）
     */
    private Long refundPrice;
    /**
     * 取餐号（叫号用，自取/外卖订单展示，堂食可空）
     */
    private Integer pickupNo;
    /**
     * 核销码（6 位，门店扫码核销时匹配，全局唯一）
     */
    private String verifyCode;
    /**
     * 叫号时间（门店点击"叫号"时记录，便于展示已叫号状态）
     */
    private LocalDateTime calledTime;

}
