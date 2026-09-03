package cn.iocoder.yudao.module.restaurant.controller.admin.packageconf;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.packageconf.vo.TenantSubscriptionVO;
import cn.iocoder.yudao.module.restaurant.service.packageconf.TenantSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 租户订阅")
@RestController
@RequestMapping("/platform/subscription")
@Validated
public class TenantSubscriptionController {

    @Resource
    private TenantSubscriptionService tenantSubscriptionService;

    @PostMapping("/open")
    @Operation(summary = "开通/续费租户订阅（自动初始化默认商户数据）")
    @PreAuthorize("hasAnyAuthority('restaurant:subscription:open')")
    public CommonResult<Long> openSubscription(@RequestBody @Valid TenantSubscriptionVO.OpenReqVO reqVO) {
        return success(tenantSubscriptionService.openSubscription(reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "租户订阅分页")
    @PreAuthorize("hasAnyAuthority('restaurant:subscription:query')")
    public CommonResult<PageResult<TenantSubscriptionVO.RespVO>> getSubscriptionPage(
            @Validated TenantSubscriptionVO.PageReqVO pageReqVO) {
        return success(tenantSubscriptionService.getSubscriptionPage(pageReqVO));
    }

}
