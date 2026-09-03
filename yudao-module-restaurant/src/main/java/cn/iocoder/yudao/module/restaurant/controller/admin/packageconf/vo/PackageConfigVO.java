package cn.iocoder.yudao.module.restaurant.controller.admin.packageconf.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 租户套餐定义 VO
 *
 * @author 餐饮 SaaS
 */
@Schema(description = "租户套餐定义")
public class PackageConfigVO {

    @Schema(description = "创建/更新 Request VO")
    @Data
    public static class SaveReqVO {
        @Schema(description = "套餐编号（更新时必填）")
        private Long id;
        @Schema(description = "套餐名称")
        private String name;
        @Schema(description = "年费价格（分）")
        private Integer price;
        @Schema(description = "有效期（月），年费=12")
        private Integer durationMonths;
        @Schema(description = "门店数量上限")
        private Integer maxStores;
        @Schema(description = "功能开关 JSON，如 {\"member\":true,\"coupon\":true}")
        private String features;
        @Schema(description = "状态：0-停用 1-启用")
        private Integer status;
        @Schema(description = "备注")
        private String remark;
    }

    @Schema(description = "分页 Request VO")
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PageReqVO extends PageParam {
        @Schema(description = "套餐名称，模糊匹配")
        private String name;
        @Schema(description = "状态：0-停用 1-启用")
        private Integer status;
    }

    @Schema(description = "Response VO")
    @Data
    public static class RespVO {
        private Long id;
        private String name;
        private Integer price;
        private Integer durationMonths;
        private Integer maxStores;
        private String features;
        private Integer status;
        private String remark;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

}
