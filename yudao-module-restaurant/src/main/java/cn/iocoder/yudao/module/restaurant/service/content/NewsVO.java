package cn.iocoder.yudao.module.restaurant.service.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 资讯 VO（M-10）
 *
 * @author 餐饮 SaaS
 */
@Schema(description = "管理后台 - 餐饮资讯 Request/Response VO")
@Data
public class NewsVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "门店编号（0 = 全平台）", example = "0")
    private Long storeId;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "摘要", example = "周年庆活动开始啦")
    private String summary;

    @Schema(description = "正文（纯文本）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "正文不能为空")
    private String content;

    @Schema(description = "排序（越小越靠前）", example = "0")
    private Integer sort;

    @Schema(description = "状态：0-发布 1-下线", example = "0")
    private Integer status;

}
