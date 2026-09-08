package cn.iocoder.yudao.module.restaurant.service.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 提现 VO（M-30）
 *
 * @author 餐饮 SaaS
 */
public class WithdrawVO {

    @Data
    public static class AccountSaveReqVO {

        @Schema(description = "账户编号（更新必填）", example = "1")
        private Long id;

        @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "门店编号不能为空")
        private Long storeId;

        @Schema(description = "账户类型：1-对公银行 2-微信 3-支付宝", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "账户类型不能为空")
        private Integer accountType;

        @Schema(description = "账户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "XX餐饮有限公司")
        @NotBlank(message = "账户名称不能为空")
        private String accountName;

        @Schema(description = "账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6222020200112233445")
        @NotBlank(message = "账号不能为空")
        private String accountNo;

        @Schema(description = "状态：0-启用 1-停用", example = "0")
        private Integer status;

    }

    @Data
    public static class ApplyReqVO {

        @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "门店编号不能为空")
        private Long storeId;

        @Schema(description = "提现账户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "请选择提现账户")
        private Long accountId;

        @Schema(description = "提现金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "100000")
        @NotNull(message = "提现金额不能为空")
        private Long amount;

        @Schema(description = "申请备注", example = "9 月上旬提现")
        private String applyRemark;

    }

    @Data
    public static class AuditReqVO {

        @Schema(description = "提现单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "提现单编号不能为空")
        private Long id;

        @Schema(description = "审核结果：true-通过并标记已打款 false-驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
        @NotNull(message = "审核结果不能为空")
        private Boolean approved;

        @Schema(description = "驳回原因（驳回时必填）", example = "账户信息核对不一致")
        private String rejectReason;

    }

    @Data
    public static class RespVO {

        @Schema(description = "提现单编号", example = "1")
        private Long id;

        private Long storeId;

        @Schema(description = "提现金额（分）", example = "100000")
        private Long amount;

        @Schema(description = "状态：0-待审核 1-已打款 2-已驳回", example = "0")
        private Integer status;

        @Schema(description = "账户快照", example = "对公银行 | XX餐饮有限公司 | 6222020200112233445")
        private String accountSnapshot;

        private String applyRemark;

        private String rejectReason;

        private LocalDateTime auditTime;

        private String auditUser;

        private LocalDateTime createTime;

    }

}
