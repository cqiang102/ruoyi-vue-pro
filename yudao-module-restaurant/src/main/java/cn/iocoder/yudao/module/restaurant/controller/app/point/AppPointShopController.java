package cn.iocoder.yudao.module.restaurant.controller.app.point;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.point.vo.PointShopVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.point.PointProductDO;
import cn.iocoder.yudao.module.restaurant.service.point.PointShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 消费者小程序 - 积分商城（M-27）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "消费者小程序 - 积分商城")
@RestController
@RequestMapping("/member/point-shop")
@Validated
public class AppPointShopController {

    @Resource
    private PointShopService pointShopService;

    @GetMapping("/product-page")
    @Operation(summary = "上架积分商品分页（本店，会员端只看上架）")
    public CommonResult<PageResult<PointShopVO.ProductRespVO>> getProductPage(
            @Valid PageParam pageParam,
            @RequestParam("storeId") Long storeId) {
        // 需登录：租户上下文
        SecurityFrameworkUtils.getLoginUserId();
        PageResult<PointProductDO> pageResult = pointShopService.getProductPage(pageParam, storeId, 1);
        List<PointShopVO.ProductRespVO> list = new ArrayList<>();
        for (PointProductDO p : pageResult.getList()) {
            PointShopVO.ProductRespVO vo = new PointShopVO.ProductRespVO();
            vo.setId(p.getId());
            vo.setStoreId(p.getStoreId());
            vo.setName(p.getName());
            vo.setImage(p.getImage());
            vo.setPoints(p.getPoints());
            vo.setStock(p.getStock());
            vo.setDescription(p.getDescription());
            vo.setStatus(p.getStatus());
            vo.setSort(p.getSort());
            list.add(vo);
        }
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @PostMapping("/exchange")
    @Operation(summary = "积分兑换（扣积分 + 扣库存，返回核销码）")
    public CommonResult<PointShopVO.ExchangeRespVO> exchange(@Valid @RequestBody PointShopVO.ExchangeReqVO reqVO) {
        return success(pointShopService.exchange(SecurityFrameworkUtils.getLoginUserId(), reqVO));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "我的兑换列表")
    public CommonResult<PageResult<PointShopVO.MyOrderRespVO>> getMyOrders(
            @Valid PageParam pageParam,
            @RequestParam(value = "status", required = false) Integer status) {
        return success(pointShopService.getMyOrders(SecurityFrameworkUtils.getLoginUserId(), pageParam, status));
    }

    @PutMapping("/my-orders/cancel")
    @Operation(summary = "取消我的兑换（仅待核销；退积分退库存）")
    public CommonResult<Boolean> cancelMyOrder(@RequestParam("orderId") Long orderId) {
        pointShopService.cancelMyOrder(SecurityFrameworkUtils.getLoginUserId(), orderId);
        return success(true);
    }

}
