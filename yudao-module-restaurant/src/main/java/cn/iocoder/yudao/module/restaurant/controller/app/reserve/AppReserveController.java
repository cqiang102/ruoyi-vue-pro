package cn.iocoder.yudao.module.restaurant.controller.app.reserve;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.service.reserve.ReserveRuleService;
import cn.iocoder.yudao.module.restaurant.service.reserve.SlotRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 消费者小程序 - 预约（M-09 / C-15）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "消费者小程序 - 预约")
@RestController
@RequestMapping("/member/reserve")
@Validated
public class AppReserveController {

    @Resource
    private ReserveRuleService reserveRuleService;

    @GetMapping("/slots")
    @Operation(summary = "查询某天可预约时段（含剩余人数）")
    @Parameter(name = "storeId", description = "门店编号", required = true)
    @Parameter(name = "date", description = "日期 yyyy-MM-dd", required = true)
    public CommonResult<List<SlotRespVO>> getAvailableSlots(@RequestParam("storeId") Long storeId,
                                                            @RequestParam("date") String date) {
        // 需登录：租户上下文
        SecurityFrameworkUtils.getLoginUserId();
        return success(reserveRuleService.getAvailableSlots(storeId, LocalDate.parse(date)));
    }

}
