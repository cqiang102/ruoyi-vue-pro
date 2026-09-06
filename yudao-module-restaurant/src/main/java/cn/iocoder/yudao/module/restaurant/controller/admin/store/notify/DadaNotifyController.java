package cn.iocoder.yudao.module.restaurant.controller.admin.store.notify;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.restaurant.controller.admin.delivery.vo.DeliveryVO;
import cn.iocoder.yudao.module.restaurant.service.delivery.DeliveryService;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 达达快送 配送状态回调端点（M-11）
 * <p>
 * 达达服务器 POST JSON 推送运单状态变化（待接单/待取货/配送中/已送达/取消等）。
 * 端点无需登录（{@link PermitAll}），且不带租户头（{@link TenantIgnore} 免 Web 层租户校验），
 * 安全性由 <b>回调签名验签</b> 保证：md5(client_id / order_id / update_time 值升序拼接) 与 signature 比对
 * （见 {@code DeliveryServiceImpl#verifySign}），伪造回调直接拒绝。
 * <p>
 * 无租户上下文的 DB 操作由 Service 内部处理：
 * 先 {@code TenantUtils.executeIgnore} 按 origin_id 查运单取得 tenantId，
 * 再 {@code TenantUtils.execute(tenantId, ...)} 在正确租户上下文下更新（避免租户插件误伤）。
 * <p>
 * 返回约定：验签失败抛异常（非 0 code）；达达侧不依赖返回体，回调丢失靠「运单分页 + 状态补查」兜底。
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "回调 - 达达快送配送状态")
@RestController
@RequestMapping("/restaurant/delivery")
@Validated
public class DadaNotifyController {

    @Resource
    private DeliveryService deliveryService;

    @PostMapping("/notify")
    @Operation(summary = "达达配送状态回调（达达服务器推送）")
    @PermitAll
    @TenantIgnore
    public CommonResult<Boolean> dadaNotify(@Valid @RequestBody DeliveryVO.CallbackReqVO reqVO) {
        deliveryService.handleCallback(reqVO);
        return success(true);
    }

}
