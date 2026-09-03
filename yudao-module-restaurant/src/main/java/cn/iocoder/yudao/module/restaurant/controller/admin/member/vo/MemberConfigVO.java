package cn.iocoder.yudao.module.restaurant.controller.admin.member.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 会员营销配置 VO
 *
 * @author 餐饮 SaaS
 */
@Schema(description = "会员营销配置")
public class MemberConfigVO {

    @Schema(description = "配置编号", example = "1")
    @Data
    public static class RespVO {
        private Long id;
        /**
         * 每消费 1 元获得的积分
         */
        private Integer earnPerYuan;
        /**
         * 每积分抵扣金额（单位：分）
         */
        private Integer deductPerPoint;
        /**
         * 使用积分抵现的最低订单金额（单位：分）
         */
        private Integer minDeductAmount;
        /**
         * 积分抵现占订单金额上限比例（单位：%，如 50 表示最多抵 50%）
         */
        private Integer maxDeductRate;
        /**
         * 升级方式：0-按成长值 1-按累计消费（单位：分）
         */
        private Integer levelUpMode;
        /**
         * 状态：0-停用 1-启用
         */
        private Integer status;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

    @Schema(description = "保存/更新 Request VO")
    @Data
    public static class SaveReqVO {
        /**
         * 每消费 1 元获得的积分
         */
        @Schema(description = "每消费1元得积分", example = "1")
        private Integer earnPerYuan;
        /**
         * 每积分抵扣金额（单位：分）
         */
        @Schema(description = "每积分抵现(分)", example = "10")
        private Integer deductPerPoint;
        /**
         * 使用积分抵现的最低订单金额（单位：分）
         */
        @Schema(description = "最低抵现订单金额(分)", example = "0")
        private Integer minDeductAmount;
        /**
         * 积分抵现占订单金额上限比例（%）
         */
        @Schema(description = "抵现上限比例(%)", example = "50")
        private Integer maxDeductRate;
        /**
         * 升级方式：0-按成长值 1-按累计消费（分）
         */
        @Schema(description = "升级方式 0成长值1累计消费", example = "0")
        private Integer levelUpMode;
        /**
         * 状态：0-停用 1-启用
         */
        @Schema(description = "状态 0停用1启用", example = "1")
        private Integer status;
    }

}
