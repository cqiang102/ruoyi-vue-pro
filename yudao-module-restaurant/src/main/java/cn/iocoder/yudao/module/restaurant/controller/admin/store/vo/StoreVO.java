package cn.iocoder.yudao.module.restaurant.controller.admin.store.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 门店 VO
 *
 * @author 餐饮 SaaS
 */
public class StoreVO {

    @Schema(description = "门店分页 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class PageReqVO extends PageParam {

        @Schema(description = "门店名称", example = "总店")
        private String name;

        @Schema(description = "状态：1营业 0打烊", example = "1")
        private Integer status;

    }

    @Schema(description = "门店保存 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class SaveReqVO {

        @Schema(description = "门店编号（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "门店名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "总店")
        @NotEmpty(message = "门店名称不能为空")
        private String name;

        @Schema(description = "联系人", example = "张三")
        private String contact;

        @Schema(description = "联系电话", example = "13800138000")
        private String phone;

        @Schema(description = "地址", example = "北京市朝阳区...")
        private String address;

        @Schema(description = "营业开始（HH:mm）", example = "10:00")
        private String businessStart;

        @Schema(description = "营业结束（HH:mm）", example = "22:00")
        private String businessEnd;

        @Schema(description = "状态：1营业 0打烊", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "状态不能为空")
        private Integer status;

        @Schema(description = "配送费（分，外卖用）", example = "500")
        private Long deliveryFee;

        @Schema(description = "起送金额（分，外卖用）", example = "2000")
        private Long minOrderAmount;

    }

    @Schema(description = "门店 Response VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class RespVO {

        @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "门店名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "总店")
        private String name;

        @Schema(description = "联系人", example = "张三")
        private String contact;

        @Schema(description = "联系电话", example = "13800138000")
        private String phone;

        @Schema(description = "地址", example = "北京市朝阳区...")
        private String address;

        @Schema(description = "营业开始（HH:mm）", requiredMode = Schema.RequiredMode.REQUIRED, example = "10:00")
        private String businessStart;

        @Schema(description = "营业结束（HH:mm）", requiredMode = Schema.RequiredMode.REQUIRED, example = "22:00")
        private String businessEnd;

        @Schema(description = "状态：1营业 0打烊", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer status;

        @Schema(description = "配送费（分，外卖用）", example = "500")
        private Long deliveryFee;

        @Schema(description = "起送金额（分，外卖用）", example = "2000")
        private Long minOrderAmount;

        @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createTime;

    }

}
