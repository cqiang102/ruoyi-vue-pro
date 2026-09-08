package cn.iocoder.yudao.module.restaurant.controller.admin.withdraw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 提现账户 RespVO（M-30）
 *
 * @author 餐饮 SaaS
 */
@Data
public class WithdrawAccountRespVO {

    @Schema(description = "账户编号", example = "1")
    private Long id;

    @Schema(description = "门店编号", example = "1")
    private Long storeId;

    @Schema(description = "账户类型：1-对公银行 2-微信 3-支付宝", example = "1")
    private Integer accountType;

    @Schema(description = "账户名称", example = "XX餐饮有限公司")
    private String accountName;

    @Schema(description = "账号", example = "6222020200112233445")
    private String accountNo;

    @Schema(description = "状态：0-启用 1-停用", example = "0")
    private Integer status;

}
