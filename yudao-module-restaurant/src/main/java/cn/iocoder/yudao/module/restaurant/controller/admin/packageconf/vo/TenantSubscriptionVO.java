package cn.iocoder.yudao.module.restaurant.controller.admin.packageconf.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 租户订阅记录 VO
 *
 * @author 餐饮 SaaS
 */
@Schema(description = "租户订阅记录")
public class TenantSubscriptionVO {

    @Schema(description = "开通 Request VO")
    @Data
    public static class OpenReqVO {
        @Schema(description = "租户编号")
        private Long tenantId;
        @Schema(description = "套餐编号")
        private Long packageId;
        @Schema(description = "年费支付订单号（pay_order.id），支付成功后回写")
        private Long payOrderId;
        @Schema(description = "实付金额（分）")
        private Integer amount;
    }

    @Schema(description = "分页 Request VO")
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PageReqVO extends PageParam {
        @Schema(description = "租户编号")
        private Long tenantId;
        @Schema(description = "套餐编号")
        private Long packageId;
        @Schema(description = "状态：1-生效中 2-已过期")
        private Integer status;
    }

    @Schema(description = "Response VO")
    @Data
    public static class RespVO {
        private Long id;
        private Long tenantId;
        private Long packageId;
        private String packageName;
        private LocalDateTime startTime;
        private LocalDateTime expireTime;
        private Integer status;
        private Long payOrderId;
        private Integer amount;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

}
