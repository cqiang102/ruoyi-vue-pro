package cn.iocoder.yudao.module.restaurant.controller.admin.delivery;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.delivery.vo.DeliveryVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.delivery.DeliveryOrderDO;
import cn.iocoder.yudao.module.restaurant.service.delivery.DeliveryService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 门店工作台 - 达达配送（M-11）
 * <p>
 * 配置（一店一条）+ 发单/取消 + 运单分页。P1-A：storeId 全部由登录店员强制注入。
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台/门店 APP - 达达配送")
@RestController
@RequestMapping("/store/delivery")
@Validated
public class DeliveryController {

    @Resource
    private DeliveryService deliveryService;
    @Resource
    private StoreAuthService storeAuthService;

    @GetMapping("/config")
    @Operation(summary = "获取门店配送配置（无配置返回空）")
    @PreAuthorize("hasAnyAuthority('restaurant:delivery:query')")
    public CommonResult<DeliveryVO.ConfigRespVO> getConfig() {
        return success(deliveryService.getConfig(storeAuthService.getLoginUserStoreId()));
    }

    @PutMapping("/config")
    @Operation(summary = "保存门店配送配置（一店一条 upsert）")
    @PreAuthorize("hasAnyAuthority('restaurant:delivery:config')")
    public CommonResult<Boolean> saveConfig(@Valid @RequestBody DeliveryVO.ConfigSaveReqVO reqVO) {
        deliveryService.saveConfig(reqVO, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "运单分页（本店隔离）")
    @PreAuthorize("hasAnyAuthority('restaurant:delivery:query')")
    public CommonResult<PageResult<DeliveryVO.RespVO>> getDeliveryPage(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        PageResult<DeliveryOrderDO> pageResult = deliveryService.getDeliveryPage(
                pageParam, storeAuthService.getLoginUserStoreId(), status);
        PageResult<DeliveryVO.RespVO> result = new PageResult<>(
                pageResult.getList().stream().map(this::convert).collect(Collectors.toList()),
                pageResult.getTotal());
        return success(result);
    }

    @PostMapping("/send")
    @Operation(summary = "发单（外卖订单 → 达达快送）")
    @PreAuthorize("hasAnyAuthority('restaurant:delivery:send')")
    public CommonResult<Long> sendDelivery(@RequestParam("orderId") Long orderId) {
        return success(deliveryService.sendDelivery(orderId, storeAuthService.getLoginUserStoreId()));
    }

    @PutMapping("/cancel")
    @Operation(summary = "商家取消运单")
    @PreAuthorize("hasAnyAuthority('restaurant:delivery:cancel')")
    public CommonResult<Boolean> cancelDelivery(@RequestParam("orderId") Long orderId) {
        deliveryService.cancelDelivery(orderId, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

    private DeliveryVO.RespVO convert(DeliveryOrderDO d) {
        DeliveryVO.RespVO vo = new DeliveryVO.RespVO();
        vo.setId(d.getId());
        vo.setOrderId(d.getOrderId());
        vo.setOriginId(d.getOriginId());
        vo.setDadaOrderId(d.getDadaOrderId());
        vo.setStatus(d.getStatus());
        vo.setFee(d.getFee());
        vo.setErrorMsg(d.getErrorMsg());
        vo.setDmName(d.getDmName());
        vo.setDmMobile(d.getDmMobile());
        vo.setCallbackTime(d.getCallbackTime());
        vo.setCreateTime(d.getCreateTime());
        return vo;
    }

}
