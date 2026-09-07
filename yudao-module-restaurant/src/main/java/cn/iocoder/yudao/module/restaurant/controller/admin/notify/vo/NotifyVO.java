package cn.iocoder.yudao.module.restaurant.controller.admin.notify.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 订阅消息 VO（M-12）
 *
 * @author 餐饮 SaaS
 */
@Data
public class NotifyVO {

    @Data
    public static class TemplateSaveReqVO {
        /**
         * 模板编号（更新时必传）
         */
        private Long id;
        /**
         * 场景码：pay_success 支付成功 / meal_ready 出餐完成 / refund_success 退款成功
         */
        @NotBlank(message = "场景不能为空")
        private String scene;
        /**
         * 微信订阅消息模板标题（须与微信公众平台「我的模板」标题完全一致）
         */
        @NotBlank(message = "模板标题不能为空")
        private String templateTitle;
        /**
         * 关键词映射 JSON（备注用）：{"character_string1":"订单号","thing2":"门店","amount3":"金额","time4":"时间"}
         */
        private String keywordConfig;
        /**
         * 小程序跳转页面（如 pages/order/detail?id=xxx）
         */
        private String page;
        /**
         * 状态：1启用 0停用
         */
        private Integer status;
    }

    @Data
    public static class TemplateRespVO {
        private Long id;
        private Long storeId;
        private String scene;
        private String templateTitle;
        private String keywordConfig;
        private String page;
        private Integer status;
    }

    @Data
    public static class RecordRespVO {
        private Long id;
        private Long storeId;
        private Long userId;
        private String openid;
        private Long orderId;
        private String scene;
        private String templateTitle;
        private String page;
        private String content;
        /**
         * 状态：0待发送 1成功 2失败
         */
        private Integer status;
        private String errorMsg;
        private String sendTime;
    }

}
