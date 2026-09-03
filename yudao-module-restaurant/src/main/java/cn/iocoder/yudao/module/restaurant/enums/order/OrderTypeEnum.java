package cn.iocoder.yudao.module.restaurant.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单类型
 *
 * @author 餐饮 SaaS
 */
@Getter
@AllArgsConstructor
public enum OrderTypeEnum {

    DINE_IN(1, "堂食"),
    TAKE_OUT(2, "自取"),
    DELIVERY(3, "外卖");

    private final Integer type;
    private final String desc;

}
