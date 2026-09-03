package cn.iocoder.yudao.module.restaurant.controller.app.order;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.order.vo.OrderVO;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.restaurant.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 消费者小程序 - 订单
 * <p>
 * 安全约定（P0 修复）：
 * 1. 用户身份一律取自登录态（token），绝不接受前端传入的 userId / memberId；
 * 2. 所有订单读写操作前强制归属校验（validateOrderOwner），防水平越权。
 */
@Tag(name = "消费者小程序 - 订单")
@RestController
@RequestMapping("/member/order")
@Validated
public class ConsumerOrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/create")
    @Operation(summary = "创建订单（消费者点餐）")
    public CommonResult<Long> createOrder(@RequestBody @Valid OrderVO.CreateReqVO createReqVO) {
        // 身份以登录态为准：无条件覆盖请求体，防止伪造他人 userId
        createReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        return success(orderService.createOrder(createReqVO));
    }

    @PostMapping("/pay-weixin")
    @Operation(summary = "发起微信支付，返回 pay_order 编号")
    public CommonResult<Long> payByWeixin(@RequestParam("orderId") Long orderId,
                                          @RequestParam("appKey") String appKey,
                                          @RequestParam(value = "userIp", defaultValue = "127.0.0.1") String userIp) {
        // P0-3：userId 从登录态取，前端不可传
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_OWNER);
        }
        // 归属校验：只能支付自己的订单
        orderService.validateOrderOwner(orderId, userId);
        return success(orderService.payByWeixin(orderId, appKey, userIp, userId,
                UserTypeEnum.MEMBER.getValue()));
    }

    @PostMapping("/pay-balance")
    @Operation(summary = "余额支付（会员储值卡）")
    public CommonResult<Boolean> payByBalance(@RequestParam("orderId") Long orderId) {
        // P0-3：memberId 不再由前端传入，钱包按登录用户 userId 定位，且先做归属校验
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_OWNER);
        }
        orderService.validateOrderOwner(orderId, userId);
        orderService.payByBalance(orderId, userId);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得订单（含明细）")
    public CommonResult<OrderVO.RespVO> getOrder(@RequestParam("id") Long id) {
        // P0-4：归属校验，防止遍历 id 拖库（他人订单一律按"不存在"处理）
        orderService.validateOrderOwner(id, SecurityFrameworkUtils.getLoginUserId());
        return success(orderService.getOrder(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得我的订单分页")
    public CommonResult<PageResult<OrderVO.RespVO>> getOrderPage(@Valid OrderVO.PageReqVO pageReqVO) {
        // P0-4：强制注入登录用户作为过滤条件，"我的订单"不再返回全租户订单
        pageReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        return success(orderService.getOrderPage(pageReqVO));
    }

    @PostMapping("/apply-refund")
    @Operation(summary = "申请退款（消费者发起）")
    public CommonResult<Boolean> applyRefund(@RequestParam("orderId") Long orderId,
                                             @RequestParam(value = "reason", required = false) String reason) {
        // P0-4：归属校验，只能退自己的订单
        orderService.validateOrderOwner(orderId, SecurityFrameworkUtils.getLoginUserId());
        orderService.refundOrder(orderId, reason);
        return success(true);
    }

}
