package cn.iocoder.yudao.module.restaurant.controller.admin.point;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.point.vo.PointShopVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.point.PointOrderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.point.PointProductDO;
import cn.iocoder.yudao.module.restaurant.service.point.PointShopService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台/门店 APP - 积分商城（M-27）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台/门店 APP - 积分商城")
@RestController
@RequestMapping("/store/point-shop")
@Validated
public class PointShopController {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private PointShopService pointShopService;
    @Resource
    private StoreAuthService storeAuthService;

    @GetMapping("/product-page")
    @Operation(summary = "积分商品分页（本店）")
    @PreAuthorize("hasAnyAuthority('restaurant:point-shop:query')")
    public CommonResult<PageResult<PointShopVO.ProductRespVO>> getProductPage(
            @Valid PageParam pageParam,
            @RequestParam(value = "status", required = false) Integer status) {
        PageResult<PointProductDO> pageResult =
                pointShopService.getProductPage(pageParam, storeAuthService.getLoginUserStoreId(), status);
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

    @PostMapping("/product/create")
    @Operation(summary = "创建积分商品")
    @PreAuthorize("hasAnyAuthority('restaurant:point-shop:create')")
    public CommonResult<Long> createProduct(@Valid @RequestBody PointShopVO.ProductSaveReqVO reqVO) {
        return success(pointShopService.createProduct(reqVO, storeAuthService.getLoginUserStoreId()));
    }

    @PutMapping("/product/update")
    @Operation(summary = "更新积分商品")
    @PreAuthorize("hasAnyAuthority('restaurant:point-shop:update')")
    public CommonResult<Boolean> updateProduct(@Valid @RequestBody PointShopVO.ProductSaveReqVO reqVO) {
        pointShopService.updateProduct(reqVO, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

    @DeleteMapping("/product/delete")
    @Operation(summary = "删除积分商品（仅下架状态）")
    @PreAuthorize("hasAnyAuthority('restaurant:point-shop:delete')")
    public CommonResult<Boolean> deleteProduct(@RequestParam("id") Long id) {
        pointShopService.deleteProduct(id, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

    @GetMapping("/order-page")
    @Operation(summary = "兑换记录分页（本店）")
    @PreAuthorize("hasAnyAuthority('restaurant:point-shop:query')")
    public CommonResult<PageResult<PointShopVO.OrderRespVO>> getOrderPage(
            @Valid PageParam pageParam,
            @RequestParam(value = "status", required = false) Integer status) {
        PageResult<PointOrderDO> pageResult =
                pointShopService.getOrderPage(pageParam, storeAuthService.getLoginUserStoreId(), status);
        List<PointShopVO.OrderRespVO> list = new ArrayList<>();
        for (PointOrderDO o : pageResult.getList()) {
            PointShopVO.OrderRespVO vo = new PointShopVO.OrderRespVO();
            vo.setId(o.getId());
            vo.setStoreId(o.getStoreId());
            vo.setUserId(o.getUserId());
            vo.setMemberId(o.getMemberId());
            vo.setProductId(o.getProductId());
            vo.setProductName(o.getProductName());
            vo.setPoints(o.getPoints());
            vo.setQuantity(o.getQuantity());
            vo.setTotalPoints(o.getTotalPoints());
            vo.setStatus(o.getStatus());
            vo.setVerifyCode(o.getVerifyCode());
            vo.setCreateTime(o.getCreateTime() == null ? null : o.getCreateTime().format(TS));
            list.add(vo);
        }
        return success(new PageResult<>(list, pageResult.getTotal()));
    }

    @PostMapping("/verify")
    @Operation(summary = "店员核销（按会员出示的核销码）")
    @PreAuthorize("hasAnyAuthority('restaurant:point-shop:verify')")
    public CommonResult<PointShopVO.VerifyRespVO> verify(@RequestParam("verifyCode") String verifyCode) {
        return success(pointShopService.verify(verifyCode, storeAuthService.getLoginUserStoreId()));
    }

}
