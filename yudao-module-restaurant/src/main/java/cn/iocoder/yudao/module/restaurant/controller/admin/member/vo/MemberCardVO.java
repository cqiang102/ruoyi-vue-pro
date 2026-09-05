package cn.iocoder.yudao.module.restaurant.controller.admin.member.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * 会员卡 VO（M-26）
 *
 * @author 餐饮 SaaS
 */
public class MemberCardVO {

    // ========== 卡商品 ==========

    @Schema(description = "管理后台 - 会员卡分页 Request VO")
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class PageReqVO extends PageParam {

        @Schema(description = "卡名称（模糊）", example = "年卡")
        private String name;

        @Schema(description = "状态：1在售 0下架", example = "1")
        private Integer status;

    }

    @Schema(description = "会员卡保存 Request VO")
    @Data
    public static class SaveReqVO {

        @Schema(description = "卡编号（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "卡名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "金卡年卡")
        @NotBlank(message = "卡名称不能为空")
        private String name;

        @Schema(description = "售价（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "9900")
        @NotNull(message = "售价不能为空")
        @Positive(message = "售价必须大于 0")
        private Long price;

        @Schema(description = "卡描述", example = "全年 9 折")
        private String description;

        @Schema(description = "权益说明（逐行一条）", example = "全场 9 折\n生日赠券")
        private String rights;

        @Schema(description = "状态：1在售 0下架", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "状态不能为空")
        private Integer status;

        @Schema(description = "排序（越大越靠前）", example = "1")
        private Integer sort;

    }

    @Schema(description = "会员卡 Response VO")
    @Data
    public static class RespVO {

        @Schema(description = "卡编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "卡名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "金卡年卡")
        private String name;

        @Schema(description = "售价（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "9900")
        private Long price;

        @Schema(description = "卡描述", example = "全年 9 折")
        private String description;

        @Schema(description = "权益说明（逐行一条）", example = "全场 9 折\n生日赠券")
        private String rights;

        @Schema(description = "状态：1在售 0下架", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer status;

        @Schema(description = "已售数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private Integer soldCount;

        @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer sort;

        @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createTime;

    }

    // ========== 购买记录 ==========

    @Schema(description = "管理后台 - 购卡记录分页 Request VO")
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class OrderPageReqVO extends PageParam {

        @Schema(description = "用户编号（精确）", example = "1")
        private Long userId;

        @Schema(description = "卡编号（精确）", example = "1")
        private Long cardId;

    }

    @Schema(description = "购卡记录 Response VO")
    @Data
    public static class OrderRespVO {

        @Schema(description = "记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "购卡单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "MCD-xxx")
        private String orderNo;

        @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long userId;

        @Schema(description = "卡编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long cardId;

        @Schema(description = "卡名称快照", requiredMode = Schema.RequiredMode.REQUIRED, example = "金卡年卡")
        private String cardName;

        @Schema(description = "实付金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "9900")
        private Long price;

        @Schema(description = "支付方式：2余额", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
        private Integer payType;

        @Schema(description = "状态：0待支付 1已支付", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer status;

        @Schema(description = "支付时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime paidTime;

        @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createTime;

    }

    @Schema(description = "消费者端 - 余额购卡 Request VO")
    @Data
    public static class BuyReqVO {

        @Schema(description = "卡编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "卡编号不能为空")
        private Long cardId;

    }

}
