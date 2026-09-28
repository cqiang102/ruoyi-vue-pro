package cn.iocoder.yudao.module.restaurant.controller.admin.decor;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.decor.HomeDecorDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.decor.HomeDecorMapper;
import cn.iocoder.yudao.module.restaurant.framework.storeownership.StoreOwnership;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 首页装修（M-02）
 *
 * <p>MVP 配置式装修：条目 CRUD + 排序（不做画布拖拽，条目类型三选一）。
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 首页装修")
@RestController
@RequestMapping("/store/home-decor")
@Validated
public class HomeDecorController {

    @Resource
    private HomeDecorMapper homeDecorMapper;

    @Data
    public static class SaveReqVO {

        @Schema(description = "条目编号（更新必填）", example = "1")
        private Long id;

        // 门店编号由服务端按登录账号绑定的门店注入（@StoreOwnership），前端可不传
        @Schema(description = "门店编号（服务端按登录门店注入，可不传）", example = "1")
        private Long storeId;

        @Schema(description = "类型：1-轮播 banner 2-金刚区入口 3-推荐菜品位", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "类型不能为空")
        private Integer type;

        @Schema(description = "标题（金刚区名称/推荐位标题）", example = "招牌推荐")
        @NotBlank(message = "标题不能为空")
        private String title;

        @Schema(description = "图片 URL", example = "https://cdn.example.com/banner.jpg")
        private String image;

        @Schema(description = "跳转路径", example = "/pages/restaurant/point-shop")
        private String link;

        @Schema(description = "排序", example = "1")
        private Integer sort;

        @Schema(description = "状态：0-启用 1-停用", example = "0")
        private Integer status;

    }

    @Data
    public static class RespVO extends SaveReqVO {
    }

    @PostMapping("/create")
    @Operation(summary = "新增装修条目")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:home-decor:create')")
    @StoreOwnership(entity = HomeDecorDO.class, idParam = "", injectStoreId = true)
    public CommonResult<Long> createDecor(@Valid @RequestBody SaveReqVO reqVO) {
        HomeDecorDO decor = BeanUtils.toBean(reqVO, HomeDecorDO.class);
        if (decor.getStatus() == null) {
            decor.setStatus(0);
        }
        if (decor.getSort() == null) {
            decor.setSort(0);
        }
        // storeId 由 @StoreOwnership 统一注入（不采信入参）
        homeDecorMapper.insert(decor);
        return success(decor.getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新装修条目")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:home-decor:update')")
    @StoreOwnership(entity = HomeDecorDO.class, idParam = "id", injectStoreId = true)
    public CommonResult<Boolean> updateDecor(@Valid @RequestBody SaveReqVO reqVO) {
        // 归属校验 + storeId 覆写由 @StoreOwnership 统一处理
        homeDecorMapper.updateById(BeanUtils.toBean(reqVO, HomeDecorDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除装修条目")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:home-decor:delete')")
    @StoreOwnership(entity = HomeDecorDO.class, idParam = "id")
    public CommonResult<Boolean> deleteDecor(@RequestParam("id") Long id) {
        homeDecorMapper.deleteById(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "装修条目列表（按店）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:home-decor:query')")
    public CommonResult<List<RespVO>> getDecorList(@RequestParam("storeId") Long storeId) {
        List<HomeDecorDO> list = homeDecorMapper.selectList(
                new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<HomeDecorDO>()
                        .eq(HomeDecorDO::getStoreId, storeId)
                        .orderByAsc(HomeDecorDO::getSort));
        return success(BeanUtils.toBean(list, RespVO.class));
    }

    /**
     * 门店归属校验已迁移到 {@link StoreOwnership} 注解 + {@code StoreOwnershipAspect} 切面统一处理
     * （2026-09-28：原先逐域手写，容易漏；本域作为切面托管的样板，其余域逐步迁移）。
     */

}
