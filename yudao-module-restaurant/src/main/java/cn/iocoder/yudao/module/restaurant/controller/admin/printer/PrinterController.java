package cn.iocoder.yudao.module.restaurant.controller.admin.printer;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.printer.vo.PrinterVO;
import cn.iocoder.yudao.module.restaurant.convert.printer.PrinterConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrinterDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrintTaskDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrinterDO;
import cn.iocoder.yudao.module.restaurant.service.print.PrintService;
import cn.iocoder.yudao.module.restaurant.service.printer.PrinterService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台/门店 APP - 云打印机")
@RestController
@RequestMapping("/store/printer")
@Validated
public class PrinterController {

    @Resource
    private PrinterService printerService;
    @Resource
    private PrintService printService;
    @Resource
    private StoreAuthService storeAuthService;

    @PostMapping("/create")
    @Operation(summary = "添加打印机（终端号）")
    @PreAuthorize("hasAnyAuthority('restaurant:printer:create')")
    public CommonResult<Long> createPrinter(@RequestBody @Valid PrinterVO.SaveReqVO saveReqVO) {
        // P1-A：强制绑定登录店员的门店，杜绝跨店绑定设备
        return success(printerService.createPrinter(saveReqVO, storeAuthService.getLoginUserStoreId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新打印机")
    @PreAuthorize("hasAnyAuthority('restaurant:printer:update')")
    public CommonResult<Boolean> updatePrinter(@RequestBody @Valid PrinterVO.SaveReqVO saveReqVO) {
        printerService.updatePrinter(saveReqVO, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除打印机")
    @PreAuthorize("hasAnyAuthority('restaurant:printer:delete')")
    public CommonResult<Boolean> deletePrinter(@RequestParam("id") Long id) {
        printerService.deletePrinter(id, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "打印机分页（本店）")
    @PreAuthorize("hasAnyAuthority('restaurant:printer:query')")
    public CommonResult<PageResult<PrinterVO.RespVO>> getPrinterPage(@Valid PrinterVO.PageReqVO pageReqVO) {
        // P1-A：强制本店过滤
        pageReqVO.setStoreId(storeAuthService.getLoginUserStoreId());
        PageResult<PrinterDO> pageResult = printerService.getPrinterPage(pageReqVO);
        return success(new PageResult<>(PrinterConvert.convertList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/task-page")
    @Operation(summary = "打印任务分页（本店，含失败重试入口）")
    @PreAuthorize("hasAnyAuthority('restaurant:printer:query')")
    public CommonResult<PageResult<PrinterVO.TaskRespVO>> getTaskPage(
            @RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        PageResult<PrintTaskDO> pageResult =
                printService.getTaskPage(pageParam, storeId, orderId, status);
        // 打印机名称冗余展示（仅本店打印机）
        PrinterVO.PageReqVO printerReq = new PrinterVO.PageReqVO();
        printerReq.setStoreId(storeId);
        printerReq.setPageNo(1);
        printerReq.setPageSize(100);
        Map<Long, String> printerNames = printerService.getPrinterPage(printerReq).getList().stream()
                .collect(Collectors.toMap(PrinterDO::getId, PrinterDO::getName));
        PageResult<PrinterVO.TaskRespVO> result = new PageResult<>(
                pageResult.getList().stream()
                        .map(t -> printService.convertTask(t, printerNames.getOrDefault(t.getPrinterId(), "")))
                        .collect(Collectors.toList()),
                pageResult.getTotal());
        return success(result);
    }

    @PutMapping("/task/retry")
    @Operation(summary = "重试失败打印任务")
    @PreAuthorize("hasAnyAuthority('restaurant:printer:retry')")
    public CommonResult<Boolean> retryTask(@RequestParam("id") Long id) {
        // P1-A：归属校验内聚在 service（任务 storeId 与登录门店比对）
        printService.retryTask(id, storeAuthService.getLoginUserStoreId());
        return success(true);
    }

}
