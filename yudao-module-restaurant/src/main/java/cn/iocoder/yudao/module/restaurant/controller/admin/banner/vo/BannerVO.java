package cn.iocoder.yudao.module.restaurant.controller.admin.banner.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 轮播图 VO
 *
 * @author 餐饮 SaaS
 */
public class BannerVO {

    @Schema(description = "轮播图分页 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class PageReqVO extends PageParam {

        @Schema(description = "标题", example = "招牌菜推荐")
        private String title;

        @Schema(description = "状态：1启用 0禁用", example = "1")
        private Integer status;

    }

    @Schema(description = "轮播图保存 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class SaveReqVO {

        @Schema(description = "轮播图编号（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "招牌菜推荐")
        @NotEmpty(message = "轮播图标题不能为空")
        private String title;

        @Schema(description = "图片地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://xxx.png")
        @NotEmpty(message = "轮播图图片不能为空")
        private String image;

        @Schema(description = "跳转类型：0无跳转 1菜品 2门店 3外链", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        @NotNull(message = "跳转类型不能为空")
        private Integer linkType;

        @Schema(description = "跳转目标（菜品编号 / 门店编号 / 外链 URL）", example = "")
        private String linkValue;

        @Schema(description = "状态：1启用 0禁用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "状态不能为空")
        private Integer status;

        @Schema(description = "排序（越大越靠前）", example = "1")
        private Integer sort;

    }

    @Schema(description = "轮播图 Response VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class RespVO {

        @Schema(description = "轮播图编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "招牌菜推荐")
        private String title;

        @Schema(description = "图片地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://xxx.png")
        private String image;

        @Schema(description = "跳转类型：0无跳转 1菜品 2门店 3外链", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private Integer linkType;

        @Schema(description = "跳转目标", example = "")
        private String linkValue;

        @Schema(description = "状态：1启用 0禁用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer status;

        @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer sort;

        @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createTime;

    }

}
