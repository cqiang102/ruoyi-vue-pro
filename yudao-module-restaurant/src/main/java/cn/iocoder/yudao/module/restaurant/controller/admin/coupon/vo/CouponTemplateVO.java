package cn.iocoder.yudao.module.restaurant.controller.admin.coupon.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 优惠券模板 VO
 *
 * @author 餐饮 SaaS
 */
@Schema(description = "优惠券模板")
public class CouponTemplateVO {

    @Schema(description = "创建/更新 Request VO")
    @Data
    public static class SaveReqVO {
        @Schema(description = "模板编号（更新时必填）")
        private Long id;
        @Schema(description = "券名称")
        private String name;
        @Schema(description = "券类型：1-满减 2-折扣")
        private Integer type;
        @Schema(description = "使用门槛金额（分，0=无门槛）")
        private Integer thresholdAmount;
        @Schema(description = "优惠值：满减=减免金额(分)；折扣=折扣率(95=95折)")
        private Integer discountValue;
        @Schema(description = "发放总量")
        private Integer total;
        @Schema(description = "每人限领数量")
        private Integer perLimit;
        @Schema(description = "领取后有效天数")
        private Integer validDays;
        @Schema(description = "状态：0-停用 1-启用")
        private Integer status;
    }

    @Schema(description = "分页 Request VO")
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PageReqVO extends PageParam {
        @Schema(description = "券名称，模糊匹配")
        private String name;
        @Schema(description = "券类型：1-满减 2-折扣")
        private Integer type;
        @Schema(description = "状态：0-停用 1-启用")
        private Integer status;
    }

    @Schema(description = "Response VO")
    @Data
    public static class RespVO {
        private Long id;
        private String name;
        private Integer type;
        private Integer thresholdAmount;
        private Integer discountValue;
        private Integer total;
        private Integer takenCount;
        private Integer perLimit;
        private Integer validDays;
        private Integer status;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

}
