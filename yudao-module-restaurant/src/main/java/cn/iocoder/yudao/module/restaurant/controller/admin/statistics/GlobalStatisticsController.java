package cn.iocoder.yudao.module.restaurant.controller.admin.statistics;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.service.statistics.GlobalStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 平台运营 - 全局统计（P-06）
 *
 * <p>跨租户聚合，仅平台管理员可见。注意 Controller 用 @ss.hasPermission（芋道惯例）。
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "平台运营 - 全局统计")
@RestController
@RequestMapping("/platform/statistics")
@Validated
public class GlobalStatisticsController {

    @Resource
    private GlobalStatisticsService globalStatisticsService;

    @GetMapping("/overview")
    @Operation(summary = "总览（商户数/会员数/累计与今日 GMV、订单数）")
    @PreAuthorize("@ss.hasPermission('restaurant:global-stat:query')")
    public CommonResult<Map<String, Object>> getOverview() {
        return success(globalStatisticsService.getOverview());
    }

    @GetMapping("/trend")
    @Operation(summary = "近 30 天 GMV/订单趋势")
    @PreAuthorize("@ss.hasPermission('restaurant:global-stat:query')")
    public CommonResult<List<Map<String, Object>>> getTrend() {
        return success(globalStatisticsService.getTrend());
    }

    @GetMapping("/store-top")
    @Operation(summary = "门店 GMV TOP10（近 30 天）")
    @PreAuthorize("@ss.hasPermission('restaurant:global-stat:query')")
    public CommonResult<List<Map<String, Object>>> getStoreTop() {
        return success(globalStatisticsService.getStoreTop());
    }

}
