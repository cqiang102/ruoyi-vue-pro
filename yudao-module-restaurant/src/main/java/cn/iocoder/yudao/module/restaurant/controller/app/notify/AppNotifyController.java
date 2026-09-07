package cn.iocoder.yudao.module.restaurant.controller.app.notify;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.service.notify.NotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 消费者小程序 - 订阅消息（M-12）
 * <p>
 * 小程序下发订阅消息前，必须先由用户点击授权（wx.requestSubscribeMessage 需用户点击触发，
 * 不能放在 onLoad/onShow）。本接口供小程序在「提交订单按钮」点击时拉取需要订阅的模板 ID 列表。
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "消费者小程序 - 订阅消息")
@RestController
@RequestMapping("/member/notify")
@Validated
public class AppNotifyController {

    @Resource
    private NotifyService notifyService;

    @GetMapping("/template-ids")
    @Operation(summary = "获取需引导订阅的模板 ID 列表（供 wx.requestSubscribeMessage 的 tmplIds）")
    public CommonResult<List<String>> getSubscribeTemplateIds() {
        // 需登录：确保租户上下文正确（多租户下模板按租户隔离）
        SecurityFrameworkUtils.getLoginUserId();
        return success(notifyService.getSubscribeTemplateIds());
    }

}
