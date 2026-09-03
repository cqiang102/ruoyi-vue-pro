package cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 菜品分类 VO
 *
 * @author 餐饮 SaaS
 */
public class DishCategoryVO {

    @Schema(description = "菜品分类分页 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class PageReqVO extends PageParam {

        @Schema(description = "分类名称", example = "热菜")
        private String name;

        @Schema(description = "状态：1上架 0下架", example = "1")
        private Integer status;

    }

    @Schema(description = "菜品分类保存 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class SaveReqVO {

        @Schema(description = "分类编号（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "热菜")
        @NotEmpty(message = "分类名称不能为空")
        private String name;

        @Schema(description = "排序", example = "1")
        @NotNull(message = "排序不能为空")
        private Integer sort;

        @Schema(description = "状态：1上架 0下架", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "状态不能为空")
        private Integer status;

        @Schema(description = "备注", example = "川菜")
        private String remark;

    }

    @Schema(description = "菜品分类 Response VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class RespVO {

        @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "热菜")
        private String name;

        @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer sort;

        @Schema(description = "状态：1上架 0下架", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer status;

        @Schema(description = "备注", example = "川菜")
        private String remark;

        @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createTime;

    }

}
