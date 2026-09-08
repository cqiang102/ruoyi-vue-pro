package cn.iocoder.yudao.module.restaurant.service.reserve;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 预约规则保存 VO（M-09）
 *
 * @author 餐饮 SaaS
 */
@Data
public class ReserveRuleSaveReqVO {

    @Schema(description = "规则编号（更新必填）", example = "1")
    private Long id;

    @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "门店编号不能为空")
    private Long storeId;

    @Schema(description = "星期：0-周日 1-周一 … 6-周六；-1 = 每天", requiredMode = Schema.RequiredMode.REQUIRED, example = "-1")
    @NotNull(message = "星期不能为空")
    private Integer weekday;

    @Schema(description = "开始时间 HH:mm", requiredMode = Schema.RequiredMode.REQUIRED, example = "10:00")
    @NotBlank(message = "开始时间不能为空")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "开始时间格式须为 HH:mm")
    private String startTime;

    @Schema(description = "结束时间 HH:mm", requiredMode = Schema.RequiredMode.REQUIRED, example = "21:00")
    @NotBlank(message = "结束时间不能为空")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "结束时间格式须为 HH:mm")
    private String endTime;

    @Schema(description = "时段间隔（分钟）", example = "30")
    @NotNull(message = "时段间隔不能为空")
    private Integer slotInterval;

    @Schema(description = "单时段可预约人数", example = "10")
    @NotNull(message = "单时段可预约人数不能为空")
    private Integer maxPeople;

    @Schema(description = "最多可提前预约天数", example = "7")
    private Integer advanceDays;

    @Schema(description = "状态：0-启用 1-停用", example = "0")
    private Integer status;

}
