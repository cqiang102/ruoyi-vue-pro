package cn.iocoder.yudao.module.restaurant.service.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 历史消费汇总 VO（C-12 用户中心）
 *
 * @author 餐饮 SaaS
 */
@Data
public class MemberConsumeSummaryVO {

    @Schema(description = "累计消费金额（分；口径：已支付订单）", example = "128800")
    private Long totalAmount;

    @Schema(description = "订单数", example = "23")
    private Long orderCount;

    @Schema(description = "最近下单时间", example = "2026-09-01 12:00:00")
    private LocalDateTime lastOrderTime;

}
