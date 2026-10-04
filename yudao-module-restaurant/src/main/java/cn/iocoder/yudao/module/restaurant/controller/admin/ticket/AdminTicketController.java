package cn.iocoder.yudao.module.restaurant.controller.admin.ticket;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.ticket.RestaurantTicketDO;
import cn.iocoder.yudao.module.restaurant.service.ticket.RestaurantTicketService;
import cn.iocoder.yudao.module.restaurant.service.ticket.TicketVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 平台端 - 工单管理（P-07 平台消息·工单）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "平台端 - 工单管理")
@RestController
@RequestMapping("/restaurant/ticket")
@Validated
public class AdminTicketController {

    @Resource
    private RestaurantTicketService ticketService;

    @GetMapping("/page")
    @Operation(summary = "工单分页（全部门店）")
    @PreAuthorize("@ss.hasPermission('restaurant:ticket:query')")
    public CommonResult<PageResult<RestaurantTicketDO>> page(@Valid TicketVO.PageReqVO reqVO) {
        return success(ticketService.getTicketPage(reqVO));
    }

    @PutMapping("/reply")
    @Operation(summary = "回复工单")
    @PreAuthorize("@ss.hasPermission('restaurant:ticket:reply')")
    public CommonResult<Boolean> reply(@Valid @RequestBody TicketVO.ReplyReqVO reqVO) {
        ticketService.reply(reqVO);
        return success(true);
    }

    @PutMapping("/close")
    @Operation(summary = "关闭工单")
    @PreAuthorize("@ss.hasPermission('restaurant:ticket:close')")
    public CommonResult<Boolean> close(@RequestParam("id") Long id) {
        ticketService.close(id);
        return success(true);
    }

}
