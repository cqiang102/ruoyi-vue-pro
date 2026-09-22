package cn.iocoder.yudao.module.restaurant.controller.admin.point.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 积分商城 VO（M-27）
 *
 * @author 餐饮 SaaS
 */
@Data
public class PointShopVO {

    @Data
    public static class ProductSaveReqVO {
        private Long id;
        @NotBlank(message = "商品名称不能为空")
        private String name;
        private String image;
        /**
         * 单件所需积分
         */
        @NotNull(message = "所需积分不能为空")
        @Min(value = 1, message = "所需积分至少为 1")
        private Integer points;
        /**
         * 库存（-1 不限）
         */
        @NotNull(message = "库存不能为空")
        @Min(value = -1, message = "库存不能小于 -1（不限）")
        private Integer stock;
        private String description;
        /**
         * 状态：1上架 0下架
         */
        private Integer status;
        private Integer sort;
    }

    @Data
    public static class ProductRespVO {
        private Long id;
        private Long storeId;
        private String name;
        private String image;
        private Integer points;
        private Integer stock;
        private String description;
        private Integer status;
        private Integer sort;
    }

    @Data
    public static class OrderRespVO {
        private Long id;
        private Long storeId;
        private Long userId;
        private Long memberId;
        private Long productId;
        private String productName;
        private Integer points;
        private Integer quantity;
        private Integer totalPoints;
        /**
         * 状态：0待核销 1已核销 2已取消
         */
        private Integer status;
        private String verifyCode;
        private String verifyTime;
        private String createTime;
    }

    /**
     * 会员端兑换请求
     */
    @Data
    public static class ExchangeReqVO {
        /**
         * 门店编号：积分商品是「门店级」的（兑换单也落在该门店、由该店核销），
         * 必须与商品的 storeId 一致，避免会员兑换到其他门店的商品（2026-09-22 补校验）
         */
        @NotNull(message = "门店编号不能为空")
        private Long storeId;
        @NotNull(message = "商品编号不能为空")
        private Long productId;
        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量至少为 1")
        private Integer quantity;
    }

    /**
     * 会员端「我的兑换」列表
     */
    @Data
    public static class MyOrderRespVO {
        private Long id;
        private String productName;
        private String image;
        private Integer quantity;
        private Integer totalPoints;
        private Integer status;
        private String verifyCode;
        private String createTime;
    }

    /**
     * 兑换结果
     */
    @Data
    public static class ExchangeRespVO {
        private Long orderId;
        private String verifyCode;
    }

    /**
     * 核销结果
     */
    @Data
    public static class VerifyRespVO {
        private Long orderId;
        private String productName;
        private Integer quantity;
        private Integer status;
    }

    /**
     * 兑换记录分页响应（列表场景字段裁剪，防 cyclic）
     */
    @Data
    public static class OrderPageWrapper {
        private List<OrderRespVO> list;
        private Long total;
    }

}
