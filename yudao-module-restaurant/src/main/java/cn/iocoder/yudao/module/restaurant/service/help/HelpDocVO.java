package cn.iocoder.yudao.module.restaurant.service.help;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 帮助/关于文档 VO（C-12）
 *
 * @author 餐饮 SaaS
 */
@Data
public class HelpDocVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "类型：1-帮助 2-关于", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "如何开发票")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "正文（纯文本）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "正文不能为空")
    private String content;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态：0-启用 1-停用", example = "0")
    private Integer status;

}
