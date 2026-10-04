package cn.iocoder.yudao.module.restaurant.controller.admin.ticket;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
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
 * 门店端 - 工单（P-07 平台消息·工单）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "门店端 - 工单")
@RestController
@RequestMapping("/store/ticket")
@Validated
public class StoreTicketController {

    @Resource
    private RestaurantTicketService ticketService;

    @PostMapping("/create")
    @Operation(summary = "提交工单")
    @PreAuthorize("@ss.hasPermission('restaurant:ticket:create')")
    public CommonResult<Long> create(@Valid @RequestBody TicketVO.CreateReqVO reqVO) {
        // 门店归属由登录账号绑定的门店决定（Service 内部注入），不采信前端入参
        return success(ticketService.createTicket(reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "我的工单分页（仅本店）")
    @PreAuthorize("@ss.hasPermission('restaurant:ticket:my')")
    public CommonResult<PageResult<RestaurantTicketDO>> page(@Valid PageParam pageParam,
                                                             @RequestParam(value = "status", required = false) Integer status) {
        return success(ticketService.getMyTicketPage(pageParam, status));
    }

}
