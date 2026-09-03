package cn.iocoder.yudao.module.restaurant.controller.app.coupon.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户优惠券实例 VO（消费者端）
 *
 * @author 餐饮 SaaS
 */
@Schema(description = "我的优惠券")
public class CouponVO {

    @Schema(description = "Response VO")
    @Data
    public static class RespVO {
        private Long id;
        /**
         * 模板编号
         */
        private Long templateId;
        /**
         * 券名称
         */
        private String name;
        /**
         * 券类型：1-满减 2-折扣
         */
        private Integer type;
        /**
         * 使用门槛金额（分）
         */
        private Integer thresholdAmount;
        /**
         * 优惠值：满减=减免金额(分)；折扣=折扣率
         */
        private Integer discountValue;
        /**
         * 券码
         */
        private String code;
        /**
         * 状态：0-未使用 1-已使用 2-已过期
         */
        private Integer status;
        /**
         * 过期时间
         */
        private LocalDateTime expireTime;
        private LocalDateTime usedTime;
    }

}
