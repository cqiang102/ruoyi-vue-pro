package cn.iocoder.yudao.module.restaurant.controller.admin.notify;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.notify.vo.NotifyVO;
import cn.iocoder.yudao.module.restaurant.convert.notify.NotifyConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.NotifyRecordDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.RestaurantNotifyTemplateDO;
import cn.iocoder.yudao.module.restaurant.service.notify.NotifyService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台/门店 APP - 订阅消息模板与发送记录（M-12）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台/门店 APP - 微信订阅消息")
@RestController
@RequestMapping("/store/notify")
@Validated
public class NotifyController {

    @Resource
    private NotifyService notifyService;
    @Resource
    private StoreAuthService storeAuthService;

    @GetMapping("/template-page")
    @Operation(summary = "订阅消息模板分页（本店）")
    @PreAuthorize("hasAnyAuthority('restaurant:notify:query')")
    public CommonResult<PageResult<NotifyVO.TemplateRespVO>> getTemplatePage(
            @Valid PageParam pageParam) {
        PageResult<RestaurantNotifyTemplateDO> pageResult =
                notifyService.getTemplatePage(pageParam, storeAuthService.getLoginUserStoreId());
        return success(NotifyConvert.INSTANCE.convertTemplatePage(pageResult));
    }

    @PostMapping("/template/create")
    @Operation(summary = "创建订阅消息模板")
    @PreAuthorize("hasAnyAuthority('restaurant:notify:create')")
    public CommonResult<Long> createTemplate(@Valid @RequestBody NotifyVO.TemplateSaveReqVO reqVO) {
        return success(notifyService.createTemplate(reqVO, storeAuthService.getLoginUserStoreId()));
    }

    @PutMapping("/template/update")
    @Operation(summary = "更新订阅消息模板")
    @PreAuthorize("hasAnyAuthority('restaurant:notify:update')")
    public CommonResult<Boolean> updateTemplate(@Valid @RequestBody NotifyVO.TemplateSaveReqVO reqVO) {
        notifyService.updateTemplate(reqVO, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

    @DeleteMapping("/template/delete")
    @Operation(summary = "删除订阅消息模板")
    @PreAuthorize("hasAnyAuthority('restaurant:notify:delete')")
    public CommonResult<Boolean> deleteTemplate(@RequestParam("id") Long id) {
        notifyService.deleteTemplate(id, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

    @GetMapping("/record-page")
    @Operation(summary = "订阅消息发送记录分页（本店，排查用户未收到的凭据）")
    @PreAuthorize("hasAnyAuthority('restaurant:notify:query')")
    public CommonResult<PageResult<NotifyVO.RecordRespVO>> getRecordPage(
            @Valid PageParam pageParam,
            @RequestParam(value = "scene", required = false) String scene,
            @RequestParam(value = "status", required = false) Integer status) {
        PageResult<NotifyRecordDO> pageResult = notifyService.getRecordPage(
                pageParam, storeAuthService.getLoginUserStoreId(), scene, status);
        return success(NotifyConvert.INSTANCE.convertRecordPage(pageResult));
    }

}
