package cn.iocoder.yudao.module.restaurant.controller.admin.statistics;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.statistics.vo.StatisticsVO;
import cn.iocoder.yudao.module.restaurant.service.statistics.StatisticsService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台/门店 APP - 数据看板（M-28）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台/门店 APP - 数据看板")
@RestController
@RequestMapping("/store/statistics")
@Validated
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;
    @Resource
    private StoreAuthService storeAuthService;

    @GetMapping("/dashboard")
    @Operation(summary = "看板聚合数据（概览 + 趋势 + 类型分布 + 菜品 TOP）")
    @PreAuthorize("hasAnyAuthority('restaurant:statistics:query')")
    public CommonResult<StatisticsVO.DashboardRespVO> getDashboard() {
        return success(statisticsService.getDashboard(storeAuthService.getLoginUserStoreId()));
    }

}
