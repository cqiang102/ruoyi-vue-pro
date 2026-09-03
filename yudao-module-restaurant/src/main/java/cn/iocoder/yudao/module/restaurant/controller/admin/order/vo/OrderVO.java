package cn.iocoder.yudao.module.restaurant.controller.admin.order.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 VO
 *
 * @author 餐饮 SaaS
 */
public class OrderVO {

    @Schema(description = "订单分页 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class PageReqVO extends PageParam {

        @Schema(description = "门店编号", example = "1")
        private Long storeId;

        @Schema(description = "订单号", example = "RO20260821120000123")
        private String orderNo;

        @Schema(description = "订单类型：1堂食 2自取 3外卖", example = "1")
        private Integer type;

        @Schema(description = "订单状态：1待支付 2已支付 3制作中 4已完成 5已取消 6退款中", example = "1")
        private Integer status;

        @Schema(description = "下单用户编号（服务端注入：消费者端强制为登录用户，管理端不传查全部）", example = "10086")
        private Long userId;

    }

    @Schema(description = "订单明细创建 Request VO")
    @Data
    public static class ItemCreateVO {

        @Schema(description = "菜品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "菜品编号不能为空")
        private Long dishId;

        @Schema(description = "选中的规格选项编号", example = "10")
        private Long specId;

        @Schema(description = "选中的加料选项编号列表", example = "[20, 21]")
        private List<Long> addonIds;

        @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量至少为1")
        @Max(value = 999, message = "数量不能超过999")
        private Integer quantity;

    }

    @Schema(description = "订单创建 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class CreateReqVO {

        @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "门店编号不能为空")
        private Long storeId;

        @Schema(description = "桌台编号（堂食必填）", example = "2")
        private Long tableId;

        @Schema(description = "订单类型：1堂食 2自取 3外卖", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "订单类型不能为空")
        private Integer type;

        @Schema(description = "会员编号（余额支付/会员价用）", example = "100")
        private Long memberId;

        @Schema(description = "使用的优惠券编号（选填，下单抵扣用）", example = "10")
        private Long couponId;

        @Schema(description = "微信用户编号（优惠券归属校验用，选填）", example = "10086")
        private Long userId;

        @Schema(description = "就餐人数", example = "2")
        private Integer peopleCount;

        @Schema(description = "备注", example = "不要香菜")
        private String remark;

        @Schema(description = "收货人姓名（外卖必填）", example = "张三")
        private String receiverName;

        @Schema(description = "收货人电话（外卖必填）", example = "13800138000")
        private String receiverPhone;

        @Schema(description = "收货地址（外卖必填）", example = "北京市朝阳区xx路1号")
        private String receiverAddress;

        @Schema(description = "菜品明细列表", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(message = "订单至少需要一个菜品")
        private List<ItemCreateVO> items;

    }

    @Schema(description = "订单明细 Response VO")
    @Data
    public static class ItemRespVO {

        @Schema(description = "明细编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "菜品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long dishId;

        @Schema(description = "菜品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "鱼香肉丝")
        private String dishName;

        @Schema(description = "菜品图片", example = "https://xxx.png")
        private String image;

        @Schema(description = "规格描述", example = "辣度:微辣")
        private String specDesc;

        @Schema(description = "加料描述", example = "加料:加蛋、加料:加肠")
        private String addonDesc;

        @Schema(description = "单价（分，含规格加价）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3800")
        private Long unitPrice;

        @Schema(description = "加料合计（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
        private Long addonPrice;

        @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer quantity;

        @Schema(description = "行总价（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "4000")
        private Long totalPrice;

    }

    @Schema(description = "订单 Response VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class RespVO {

        @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "门店编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long storeId;

        @Schema(description = "桌台编号", example = "2")
        private Long tableId;

        @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "RO20260821120000123")
        private String orderNo;

        @Schema(description = "订单类型：1堂食 2自取 3外卖", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer type;

        @Schema(description = "订单状态：1待支付 2已支付 3制作中 4已完成 5已取消 6退款中", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer status;

        @Schema(description = "订单总金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "4000")
        private Long totalPrice;

        @Schema(description = "实付金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "4000")
        private Long payPrice;

        @Schema(description = "优惠金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private Long discountPrice;

        @Schema(description = "支付方式：0未付 1微信 2余额 3组合", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer payType;

        @Schema(description = "支付状态：0未付 1已付 2退款中 3已退", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        private Integer payStatus;

        @Schema(description = "会员编号", example = "100")
        private Long memberId;

        @Schema(description = "下单用户编号（app 端为登录用户）", example = "10086")
        private Long userId;

        @Schema(description = "就餐人数", example = "2")
        private Integer peopleCount;

        @Schema(description = "备注", example = "不要香菜")
        private String remark;

        @Schema(description = "配送费（分，外卖计入总价）", example = "500")
        private Long deliveryFee;

        @Schema(description = "收货人姓名（外卖）", example = "张三")
        private String receiverName;

        @Schema(description = "收货人电话（外卖）", example = "13800138000")
        private String receiverPhone;

        @Schema(description = "收货地址（外卖）", example = "北京市朝阳区xx路1号")
        private String receiverAddress;

        @Schema(description = "关联的支付单编号", example = "50")
        private Long payOrderId;

        @Schema(description = "退款金额（分）", example = "4000")
        private Long refundPrice;

        @Schema(description = "取餐号（叫号展示，自取/外卖用）", example = "12")
        private Integer pickupNo;

        @Schema(description = "核销码（门店扫码核销匹配用，6 位）", example = "A1B2C3")
        private String verifyCode;

        @Schema(description = "叫号时间")
        private LocalDateTime calledTime;

        @Schema(description = "支付时间")
        private LocalDateTime paidTime;

        @Schema(description = "完成时间")
        private LocalDateTime finishTime;

        @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
        private LocalDateTime createTime;

        @Schema(description = "菜品明细列表")
        private List<ItemRespVO> items;

    }

}
