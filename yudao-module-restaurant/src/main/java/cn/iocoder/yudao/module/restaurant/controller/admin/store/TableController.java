package cn.iocoder.yudao.module.restaurant.controller.admin.store;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.TableVO;
import cn.iocoder.yudao.module.restaurant.service.store.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 桌台")
@RestController
@RequestMapping("/store/table")
@Validated
public class TableController {

    @Resource
    private TableService tableService;

    @PostMapping("/create")
    @Operation(summary = "创建桌台")
    @PreAuthorize("hasAnyAuthority('restaurant:table:create')")
    public CommonResult<Long> createTable(@RequestBody @Valid TableVO.SaveReqVO createReqVO) {
        return success(tableService.createTable(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新桌台")
    @PreAuthorize("hasAnyAuthority('restaurant:table:update')")
    public CommonResult<Boolean> updateTable(@RequestBody @Valid TableVO.SaveReqVO updateReqVO) {
        tableService.updateTable(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除桌台")
    @PreAuthorize("hasAnyAuthority('restaurant:table:delete')")
    public CommonResult<Boolean> deleteTable(@RequestParam("id") Long id) {
        tableService.deleteTable(id);
        return success(true);
    }

    @PostMapping("/generate")
    @Operation(summary = "批量生成桌台")
    @PreAuthorize("hasAnyAuthority('restaurant:table:create')")
    public CommonResult<Boolean> generateTables(@RequestBody @Valid TableVO.BatchSaveReqVO batchReqVO) {
        tableService.generateTables(batchReqVO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得桌台")
    @PreAuthorize("hasAnyAuthority('restaurant:table:query')")
    public CommonResult<TableVO.RespVO> getTable(@RequestParam("id") Long id) {
        return success(tableService.getTable(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得桌台分页")
    @PreAuthorize("hasAnyAuthority('restaurant:table:query')")
    public CommonResult<PageResult<TableVO.RespVO>> getTablePage(@Valid TableVO.PageReqVO pageReqVO) {
        return success(tableService.getTablePage(pageReqVO));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得门店下全部桌台（点餐/下单用）")
    @PreAuthorize("hasAnyAuthority('restaurant:table:query')")
    public CommonResult<List<TableVO.RespVO>> getTableSimpleList(
            @RequestParam(value = "storeId", required = false) Long storeId) {
        return success(tableService.getTableSimpleList(storeId));
    }

    @PostMapping("/regenerate-qrcode")
    @Operation(summary = "重新生成落座桌码（可指定 H5 前缀）")
    @PreAuthorize("hasAnyAuthority('restaurant:table:update')")
    public CommonResult<String> regenerateQrcode(
            @RequestParam("id") Long id,
            @RequestParam(value = "baseUrl", required = false) String baseUrl) {
        return success(tableService.regenerateQrcode(id, baseUrl));
    }

}
