package cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品 VO（含规格/加料）
 *
 * @author 餐饮 SaaS
 */
public class DishVO {

    @Schema(description = "菜品分页 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class PageReqVO extends PageParam {

        @Schema(description = "菜品名称", example = "鱼香肉丝")
        private String name;

        @Schema(description = "分类编号", example = "1")
        private Long categoryId;

        @Schema(description = "状态：1上架 0下架", example = "1")
        private Integer status;

    }

    @Schema(description = "规格选项保存 VO")
    @Data
    public static class SpecSaveVO {

        @Schema(description = "编号（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "规格组名", requiredMode = Schema.RequiredMode.REQUIRED, example = "辣度")
        @NotEmpty(message = "规格组名不能为空")
        private String groupName;

        @Schema(description = "选项名", requiredMode = Schema.RequiredMode.REQUIRED, example = "微辣")
        @NotEmpty(message = "规格选项名不能为空")
        private String optionName;

        @Schema(description = "加价（分）", example = "0")
        @NotNull(message = "规格加价不能为空")
        private Long priceDelta;

        @Schema(description = "组内排序", example = "1")
        private Integer sort;

    }

    @Schema(description = "加料选项保存 VO")
    @Data
    public static class AddonSaveVO {

        @Schema(description = "编号（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "加料组名", requiredMode = Schema.RequiredMode.REQUIRED, example = "加料")
        @NotEmpty(message = "加料组名不能为空")
        private String groupName;

        @Schema(description = "选项名", requiredMode = Schema.RequiredMode.REQUIRED, example = "加蛋")
        @NotEmpty(message = "加料选项名不能为空")
        private String optionName;

        @Schema(description = "加价（分）", example = "200")
        @NotNull(message = "加料加价不能为空")
        private Long priceDelta;

        @Schema(description = "是否可多选：1是 0单选", example = "1")
        @NotNull(message = "是否可多选不能为空")
        private Integer multi;

        @Schema(description = "组内排序", example = "1")
        private Integer sort;

    }

    @Schema(description = "菜品保存 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class SaveReqVO {

        @Schema(description = "菜品编号（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "分类编号不能为空")
        private Long categoryId;

        @Schema(description = "菜品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "鱼香肉丝")
        @NotEmpty(message = "菜品名称不能为空")
        private String name;

        @Schema(description = "菜品图片", example = "https://xxx.png")
        private String image;

        @Schema(description = "描述", example = "经典川菜")
        private String description;

        @Schema(description = "基础价格（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3800")
        @NotNull(message = "基础价格不能为空")
        private Long price;

        @Schema(description = "状态：1上架 0下架", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "状态不能为空")
        private Integer status;

        @Schema(description = "排序", example = "1")
        private Integer sort;

        @Schema(description = "规格选项列表")
        private List<SpecSaveVO> specs;

        @Schema(description = "加料选项列表")
        private List<AddonSaveVO> addons;

    }

    @Schema(description = "菜品 Response VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class RespVO {

        @Schema(description = "菜品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long categoryId;

        @Schema(description = "菜品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "鱼香肉丝")
        private String name;

        @Schema(description = "菜品图片", example = "https://xxx.png")
        private String image;

        @Schema(description = "描述", example = "经典川菜")
        private String description;

        @Schema(description = "基础价格（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3800")
        private Long price;

        @Schema(description = "状态：1上架 0下架", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer status;

        @Schema(description = "沽清：1已售罄 0否", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private Integer soldOut;

        @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer sort;

        @Schema(description = "规格选项列表")
        private List<SpecRespVO> specs;

        @Schema(description = "加料选项列表")
        private List<AddonRespVO> addons;

        @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createTime;

    }

    @Schema(description = "规格选项 Response VO")
    @Data
    public static class SpecRespVO {

        @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "规格组名", requiredMode = Schema.RequiredMode.REQUIRED, example = "辣度")
        private String groupName;

        @Schema(description = "选项名", requiredMode = Schema.RequiredMode.REQUIRED, example = "微辣")
        private String optionName;

        @Schema(description = "加价（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private Long priceDelta;

        @Schema(description = "组内排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer sort;

    }

    @Schema(description = "加料选项 Response VO")
    @Data
    public static class AddonRespVO {

        @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "加料组名", requiredMode = Schema.RequiredMode.REQUIRED, example = "加料")
        private String groupName;

        @Schema(description = "选项名", requiredMode = Schema.RequiredMode.REQUIRED, example = "加蛋")
        private String optionName;

        @Schema(description = "加价（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
        private Long priceDelta;

        @Schema(description = "是否可多选：1是 0单选", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer multi;

        @Schema(description = "组内排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer sort;

    }

}
