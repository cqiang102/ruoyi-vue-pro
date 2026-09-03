package cn.iocoder.yudao.module.restaurant.controller.admin.store.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 桌台 VO
 *
 * @author 餐饮 SaaS
 */
public class TableVO {

    @Schema(description = "桌台分页 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class PageReqVO extends PageParam {

        @Schema(description = "门店编号", example = "1")
        private Long storeId;

        @Schema(description = "桌台分类", example = "大厅")
        private String category;

        @Schema(description = "状态：0空闲 1占用 2待清理", example = "0")
        private Integer status;

    }

    @Schema(description = "桌台保存 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class SaveReqVO {

        @Schema(description = "编号（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "门店编号不能为空")
        private Long storeId;

        @Schema(description = "桌号", requiredMode = Schema.RequiredMode.REQUIRED, example = "A01")
        @NotEmpty(message = "桌号不能为空")
        private String tableNo;

        @Schema(description = "桌台分类", example = "大厅")
        private String category;

        @Schema(description = "座位数", example = "4")
        @NotNull(message = "座位数不能为空")
        @Min(value = 1, message = "座位数至少为1")
        private Integer seats;

    }

    @Schema(description = "桌台批量生成 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class BatchSaveReqVO {

        @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "门店编号不能为空")
        private Long storeId;

        @Schema(description = "桌台分类", example = "大厅")
        private String category;

        @Schema(description = "桌号前缀", example = "A")
        private String prefix;

        @Schema(description = "起始序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "起始序号不能为空")
        @Min(value = 1)
        private Integer startNo;

        @Schema(description = "结束序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
        @NotNull(message = "结束序号不能为空")
        @Min(value = 1)
        private Integer endNo;

        @Schema(description = "座位数", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
        @NotNull(message = "座位数不能为空")
        @Min(value = 1)
        private Integer seats;

    }

    @Schema(description = "桌台 Response VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class RespVO {

        @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long storeId;

        @Schema(description = "桌号", requiredMode = Schema.RequiredMode.REQUIRED, example = "A01")
        private String tableNo;

        @Schema(description = "桌台分类", example = "大厅")
        private String category;

        @Schema(description = "座位数", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
        private Integer seats;

        @Schema(description = "状态：0空闲 1占用 2待清理", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private Integer status;

        @Schema(description = "扫码点餐URL", example = "https://xxx/scan?storeId=1&tableId=2")
        private String qrcodeContent;

        @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createTime;

    }

}
