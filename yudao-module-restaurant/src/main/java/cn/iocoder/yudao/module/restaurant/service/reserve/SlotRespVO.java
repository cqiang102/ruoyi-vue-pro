package cn.iocoder.yudao.module.restaurant.service.reserve;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 可预约时段 VO（M-09）
 *
 * @author 餐饮 SaaS
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotRespVO {

    @Schema(description = "时段开始时间 HH:mm", example = "11:30")
    private String time;

    @Schema(description = "剩余可预约人数", example = "6")
    private Integer remain;

    @Schema(description = "是否可选（false = 已约满或已过时）", example = "true")
    private Boolean available;

}
