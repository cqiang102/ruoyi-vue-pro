package cn.iocoder.yudao.module.restaurant.controller.admin.order;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.order.vo.OrderVO;
import cn.iocoder.yudao.module.restaurant.service.order.OrderService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台/门店 APP - 订单")
@RestController
@RequestMapping("/store/order")
@Validated
public class AdminOrderController {

    @Resource
    private OrderService orderService;
    @Resource
    private StoreAuthService storeAuthService;

    @PostMapping("/create")
    @Operation(summary = "创建订单（门店/收银协助点餐）")
    @PreAuthorize("hasAnyAuthority('restaurant:order:create')")
    public CommonResult<Long> createOrder(@RequestBody @Valid OrderVO.CreateReqVO createReqVO) {
        // P1-A：门店端创建订单时强制以登录账号绑定的门店为准，
        // 忽略前端传入的 storeId，杜绝跨门店代客下单
        Long loginStoreId = storeAuthService.getLoginUserStoreId();
        createReqVO.setStoreId(loginStoreId);
        return success(orderService.createOrder(createReqVO));
    }

    @PostMapping("/add-items")
    @Operation(summary = "加菜（往已存在订单追加明细）")
    @PreAuthorize("hasAnyAuthority('restaurant:order:add-items')")
    public CommonResult<Boolean> addOrderItems(@RequestParam("orderId") Long orderId,
                                               @RequestBody @Valid List<OrderVO.ItemCreateVO> items) {
        // P1-A：校验订单归属当前门店，杜绝跨门店加菜
        storeAuthService.validateOrderOwnership(orderId);
        orderService.addOrderItems(orderId, items);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消订单（仅待支付）")
    @PreAuthorize("hasAnyAuthority('restaurant:order:cancel')")
    public CommonResult<Boolean> cancelOrder(@RequestParam("id") Long id) {
        // P1-A：校验订单归属
        storeAuthService.validateOrderOwnership(id);
        orderService.cancelOrder(id);
        return success(true);
    }

    @PutMapping("/accept")
    @Operation(summary = "接单（已支付 → 制作中）")
    @PreAuthorize("hasAnyAuthority('restaurant:order:accept')")
    public CommonResult<Boolean> acceptOrder(@RequestParam("id") Long id) {
        // P1-A：校验订单归属
        storeAuthService.validateOrderOwnership(id);
        orderService.acceptOrder(id);
        return success(true);
    }

    @PutMapping("/complete")
    @Operation(summary = "完成订单（制作中 → 已完成）")
    @PreAuthorize("hasAnyAuthority('restaurant:order:complete')")
    public CommonResult<Boolean> completeOrder(@RequestParam("id") Long id) {
        // P1-A：校验订单归属
        storeAuthService.validateOrderOwnership(id);
        orderService.completeOrder(id);
        return success(true);
    }

    @PostMapping("/verify")
    @Operation(summary = "扫码核销（门店端凭核销码完成订单，自动释放堂食桌台）")
    @PreAuthorize("hasAnyAuthority('restaurant:order:verify')")
    public CommonResult<Boolean> verifyOrder(@RequestParam("verifyCode") String verifyCode,
                                             @RequestParam(value = "storeId", required = false) Long storeId) {
        // P1-A：核销场景 verifyCode 是全局唯一键，storeId 仅辅助校验；
        // 强制以登录账号绑定的门店为准，忽略前端 storeId 入参，
        // 服务层 verifyOrder 内部已按 verifyCode 反查订单，再与本店比对
        Long loginStoreId = storeAuthService.getLoginUserStoreId();
        orderService.verifyOrder(verifyCode, loginStoreId);
        return success(true);
    }

    @PostMapping("/call")
    @Operation(summary = "叫号（记录叫号时间，便于展示已叫状态）")
    @PreAuthorize("hasAnyAuthority('restaurant:order:call')")
    public CommonResult<Boolean> callOrder(@RequestParam("id") Long id) {
        // P1-A：校验订单归属
        storeAuthService.validateOrderOwnership(id);
        orderService.callOrder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得订单（含明细）")
    @PreAuthorize("hasAnyAuthority('restaurant:order:query')")
    public CommonResult<OrderVO.RespVO> getOrder(@RequestParam("id") Long id) {
        // P1-A：校验订单归属——门店端查看订单明细不能跨店
        storeAuthService.validateOrderOwnership(id);
        return success(orderService.getOrder(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得订单分页")
    @PreAuthorize("hasAnyAuthority('restaurant:order:query')")
    public CommonResult<PageResult<OrderVO.RespVO>> getOrderPage(@Valid OrderVO.PageReqVO pageReqVO) {
        // P1-A：门店端订单分页强制注入登录账号绑定的门店编号，
        // 忽略前端传入的 storeId，杜绝跨门店订单列表越权查看
        Long loginStoreId = storeAuthService.getLoginUserStoreId();
        pageReqVO.setStoreId(loginStoreId);
        return success(orderService.getOrderPage(pageReqVO));
    }

    @PutMapping("/refund")
    @Operation(summary = "退款（门店/商家发起，原路退回）")
    @PreAuthorize("hasAnyAuthority('restaurant:order:refund')")
    public CommonResult<Boolean> refundOrder(@RequestParam("id") Long id,
                                             @RequestParam(value = "reason", required = false) String reason) {
        // P1-A：校验订单归属——门店端只能退本店订单，杜绝跨店退款
        storeAuthService.validateOrderOwnership(id);
        orderService.refundOrder(id, reason);
        return success(true);
    }

}
