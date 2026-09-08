package cn.iocoder.yudao.module.restaurant.service.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 公告 VO（P-07）
 *
 * @author 餐饮 SaaS
 */
@Data
public class NoticeVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "门店编号（0 = 全平台）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "门店编号不能为空")
    private Long storeId;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "国庆营业时间调整")
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题不能超过 100 字")
    private String title;

    @Schema(description = "正文（纯文本）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "正文不能为空")
    private String content;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "状态：0-发布 1-下线", example = "0")
    private Integer status;

}
