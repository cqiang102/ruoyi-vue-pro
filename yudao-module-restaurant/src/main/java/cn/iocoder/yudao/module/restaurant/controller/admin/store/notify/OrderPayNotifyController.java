package cn.iocoder.yudao.module.restaurant.controller.admin.store.notify;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayRefundNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.api.refund.dto.PayRefundRespDTO;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.restaurant.service.member.MemberRechargeService;
import cn.iocoder.yudao.module.restaurant.service.order.OrderService;
import cn.iocoder.yudao.module.restaurant.service.pay.OrderPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 餐饮订单 支付/退款 回调端点
 * <p>
 * 由芋道 pay 模块的 {@code PayNotifyJob} 在支付/退款成功后，通过 HTTP POST 调起（见 {@code PayApp.orderNotifyUrl}）。
 * 端点无需登录（{@link PermitAll}），安全性由以下两道防线保证（P0-1 修复）：
 * <p>
 * 1. <b>不信任请求体结论</b>：以回调携带的 payOrderId 反查芋道 pay_order 真实状态，
 *    确认「支付成功 + 商户单号一致」后才推进业务；伪造的回调（无对应支付单/未支付/串单）一律抛异常；
 * 2. <b>失败即重试</b>：校验不通过或业务异常时抛出异常返回非 0 code，让 pay 模块标记通知失败并重试，
 *    杜绝"付款成功但订单未更新"的静默资损（P0-2 修复）。
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "回调 - 餐饮订单支付/退款")
@RestController
@RequestMapping("/restaurant/order")
@Validated
public class OrderPayNotifyController {

    @Resource
    private OrderService orderService;
    @Resource
    private MemberRechargeService memberRechargeService;
    @Resource
    private OrderPayService orderPayService;

    @PostMapping("/pay/notify")
    @Operation(summary = "微信支付成功回调（芋道 pay · PayNotifyJob 调起）")
    @PermitAll
    public CommonResult<Boolean> payNotify(@Valid @RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
        String merchantOrderId = notifyReqDTO.getMerchantOrderId();
        // P0-1：反查真实支付单，验证支付确已成功且商户单号一致（防伪造回调 0 元支付）
        PayOrderRespDTO payOrder = orderPayService.getOrder(notifyReqDTO.getPayOrderId());
        if (payOrder == null || !PayOrderStatusEnum.isSuccess(payOrder.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_PAY_CALLBACK_INVALID);
        }
        if (!Objects.equals(payOrder.getMerchantOrderId(), merchantOrderId)) {
            throw new ServiceException(ErrorCodeConstants.ORDER_PAY_ORDER_MISMATCH);
        }
        // 按商户单号前缀路由：RCG- 为会员储值充值，其余为餐饮订单
        if (merchantOrderId != null && merchantOrderId.startsWith("RCG-")) {
            memberRechargeService.onPaySuccess(merchantOrderId, notifyReqDTO.getPayOrderId());
        } else {
            orderService.onPaySuccess(merchantOrderId, notifyReqDTO.getPayOrderId());
        }
        return success(true);
    }

    @PostMapping("/refund/notify")
    @Operation(summary = "退款成功回调（芋道 pay · PayNotifyJob 调起）")
    @PermitAll
    public CommonResult<Boolean> refundNotify(@Valid @RequestBody PayRefundNotifyReqDTO notifyReqDTO) {
        // 与 payNotify 同款防线（P0-1 同族漏洞）：本端点 @PermitAll 且公网可达，
        // 若不反查真实退款单，伪造一条 POST 即可把任意订单标记为"已退款"并触发券归还/积分冲正。
        PayRefundRespDTO refund = orderPayService.getRefund(notifyReqDTO.getPayRefundId());
        if (refund == null || !PayRefundStatusEnum.isSuccess(refund.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_REFUND_CALLBACK_INVALID);
        }
        if (!Objects.equals(refund.getMerchantOrderId(), notifyReqDTO.getMerchantOrderId())
                || !Objects.equals(refund.getMerchantRefundId(), notifyReqDTO.getMerchantRefundId())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_REFUND_ORDER_MISMATCH);
        }
        orderService.onRefundSuccess(notifyReqDTO.getMerchantOrderId(),
                notifyReqDTO.getMerchantRefundId(), notifyReqDTO.getPayRefundId());
        return success(true);
    }

}
