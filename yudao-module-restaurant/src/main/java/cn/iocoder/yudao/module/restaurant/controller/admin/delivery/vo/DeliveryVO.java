package cn.iocoder.yudao.module.restaurant.controller.admin.delivery.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 达达配送 VO（M-11）
 *
 * @author 餐饮 SaaS
 */
@Data
public class DeliveryVO {

    /**
     * ========== 门店配送配置 ==========
     */

    @Data
    public static class ConfigSaveReqVO {
        /**
         * 达达门店编号（商户后台创建门店后获得；测试环境 11047059）
         */
        @NotBlank(message = "达达门店编号不能为空")
        private String dadaShopNo;
        /**
         * 城市 code（达达城市编码）
         */
        @NotBlank(message = "城市编码不能为空")
        private String cityCode;
        /**
         * 门店纬度（高德坐标系）
         */
        @NotNull(message = "门店纬度不能为空")
        private BigDecimal storeLat;
        /**
         * 门店经度（高德坐标系）
         */
        @NotNull(message = "门店经度不能为空")
        private BigDecimal storeLng;
        /**
         * 是否启用达达：1启用 0停用
         */
        private Integer enabled;
    }

    @Data
    public static class ConfigRespVO {
        private Long storeId;
        private String dadaShopNo;
        private String cityCode;
        private BigDecimal storeLat;
        private BigDecimal storeLng;
        private Integer enabled;
    }

    /**
     * ========== 运单 ==========
     */

    @Data
    public static class RespVO {
        private Long id;
        private Long orderId;
        private String originId;
        private String dadaOrderId;
        /**
         * 状态：0待发单 1待接单 2待取货 3配送中 4已送达 5已取消 9妥投异常 10发单失败
         */
        private Integer status;
        /**
         * 运费（分）
         */
        private BigDecimal fee;
        private String errorMsg;
        private String dmName;
        private String dmMobile;
        private LocalDateTime callbackTime;
        private LocalDateTime createTime;
    }

    /**
     * ========== 达达回调（JSON 推送） ==========
     */

    @Data
    public static class CallbackReqVO {
        /**
         * 达达运单号（JSON: client_id）
         */
        @JsonProperty("client_id")
        private String clientId;
        /**
         * 第三方订单 ID（= origin_id，JSON: order_id）
         */
        @JsonProperty("order_id")
        private String orderId;
        /**
         * 订单状态（1待接单 2待取货 100骑士到店 3配送中 4已完成 5已取消 8追加待接单 9返回中 10返回完成 1000创建失败，JSON: order_status）
         */
        @JsonProperty("order_status")
        private Integer orderStatus;
        @JsonProperty("cancel_reason")
        private String cancelReason;
        @JsonProperty("cancel_from")
        private Integer cancelFrom;
        @JsonProperty("dm_id")
        private Long dmId;
        @JsonProperty("dm_name")
        private String dmName;
        @JsonProperty("dm_mobile")
        private String dmMobile;
        @JsonProperty("update_time")
        private Long updateTime;
        private String signature;
    }

}
