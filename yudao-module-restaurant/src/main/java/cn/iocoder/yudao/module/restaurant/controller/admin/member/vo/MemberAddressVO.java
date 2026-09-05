package cn.iocoder.yudao.module.restaurant.controller.admin.member.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 会员收货地址 VO（M-23）
 *
 * @author 餐饮 SaaS
 */
public class MemberAddressVO {

    @Schema(description = "管理后台 - 会员地址分页 Request VO")
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class PageReqVO extends PageParam {

        @Schema(description = "用户编号（精确匹配）", example = "1")
        private Long userId;

    }

    @Schema(description = "消费者端 - 地址保存 Request VO")
    @Data
    public static class SaveReqVO {

        @Schema(description = "地址编号（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "收货人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
        @NotBlank(message = "收货人姓名不能为空")
        @Size(max = 30, message = "收货人姓名不能超过 30 字")
        private String name;

        @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
        @NotBlank(message = "联系电话不能为空")
        @Mobile
        private String phone;

        @Schema(description = "省市区（省 市 区，空格分隔）", requiredMode = Schema.RequiredMode.REQUIRED, example = "广东省 深圳市 南山区")
        @NotBlank(message = "省市区不能为空")
        @Size(max = 100, message = "省市区不能超过 100 字")
        private String region;

        @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "科技园 1 栋 501")
        @NotBlank(message = "详细地址不能为空")
        @Size(max = 200, message = "详细地址不能超过 200 字")
        private String detail;

        @Schema(description = "是否默认：1是 0否", example = "0")
        @NotNull(message = "是否默认不能为空")
        private Integer defaultStatus;

    }

    @Schema(description = "地址 Response VO")
    @Data
    public static class RespVO {

        @Schema(description = "地址编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long userId;

        @Schema(description = "收货人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
        private String name;

        @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
        private String phone;

        @Schema(description = "省市区", requiredMode = Schema.RequiredMode.REQUIRED, example = "广东省 深圳市 南山区")
        private String region;

        @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "科技园 1 栋 501")
        private String detail;

        @Schema(description = "是否默认：1是 0否", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private Integer defaultStatus;

        @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createTime;

    }

}
