package cn.iocoder.yudao.module.restaurant.service.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 电子发票 VO（M-35）
 *
 * @author 餐饮 SaaS
 */
public class InvoiceVO {

    // ========== 抬头 ==========

    @Data
    public static class TitleSaveReqVO {

        @Schema(description = "抬头编号（更新必填）", example = "1")
        private Long id;

        @Schema(description = "抬头类型：1-企业单位 2-个人", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "抬头类型不能为空")
        private Integer type;

        @Schema(description = "抬头名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "XX科技有限公司")
        @NotBlank(message = "抬头名称不能为空")
        private String title;

        @Schema(description = "税号（企业必填）", example = "91330106MA2XXXXX0X")
        private String taxNo;

        @Schema(description = "是否默认：0-否 1-是", example = "0")
        private Integer isDefault;

    }

    @Data
    public static class TitleRespVO {

        private Long id;
        private Integer type;
        private String title;
        private String taxNo;
        private Integer isDefault;

    }

    // ========== 开票申请 ==========

    @Data
    public static class ApplyReqVO {

        @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "订单编号不能为空")
        private Long orderId;

        @Schema(description = "抬头编号（传了则快照抬头）", example = "1")
        private Long titleId;

        @Schema(description = "抬头类型：1-企业单位 2-个人（未传抬头编号时必填）", example = "1")
        private Integer type;

        @Schema(description = "抬头名称（未传抬头编号时必填）", example = "XX科技有限公司")
        private String title;

        @Schema(description = "税号", example = "91330106MA2XXXXX0X")
        private String taxNo;

    }

    @Data
    public static class AuditReqVO {

        @Schema(description = "开票申请编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "开票申请编号不能为空")
        private Long id;

        @Schema(description = "结果：true-标记已开票（线下开具） false-驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
        @NotNull(message = "结果不能为空")
        private Boolean approved;

        @Schema(description = "驳回原因（驳回时必填）", example = "税号有误请重新提交")
        private String rejectReason;

    }

    @Data
    public static class RespVO {

        private Long id;
        private Long userId;
        private Long storeId;
        private Long orderId;
        private String orderNo;

        @Schema(description = "开票金额（分）", example = "8800")
        private Long amount;

        private Integer type;
        private String title;
        private String taxNo;

        @Schema(description = "状态：0-申请中 1-已开票 2-已驳回", example = "0")
        private Integer status;

        private String rejectReason;
        private LocalDateTime invoiceTime;
        private LocalDateTime createTime;

    }

}
