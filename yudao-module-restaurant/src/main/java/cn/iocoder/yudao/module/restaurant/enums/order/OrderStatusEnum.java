package cn.iocoder.yudao.module.restaurant.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 * <p>
 * 状态机：UNPAID(待支付) → PAID(已支付) → COOKING(制作中) → COMPLETED(已完成)
 *                       ↘ CANCELED(已取消)
 *
 * @author 餐饮 SaaS
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    UNPAID(1, "待支付"),
    PAID(2, "已支付"),
    COOKING(3, "制作中"),
    COMPLETED(4, "已完成"),
    CANCELED(5, "已取消"),
    REFUNDING(6, "退款中"),
    REFUNDED(7, "已退款");

    private final Integer status;
    private final String desc;

}
