package cn.iocoder.yudao.module.restaurant.service.printer;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.printer.vo.PrinterVO;
import cn.iocoder.yudao.module.restaurant.convert.printer.PrinterConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrinterDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.printer.PrinterMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.PRINTER_MACHINE_CODE_DUPLICATE;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.PRINTER_NOT_EXISTS;

/**
 * 云打印机 Service 实现（M-10）
 * <p>
 * 归属隔离（P1-A 模式）：storeId 一律由调用方传登录店员绑定门店，
 * create 强制注入、update/delete/page 强制校验/过滤，杜绝跨店管理打印机。
 *
 * @author 餐饮 SaaS
 */
@Service
@Slf4j
public class PrinterServiceImpl implements PrinterService {

    @Resource
    private PrinterMapper printerMapper;

    @Override
    public Long createPrinter(PrinterVO.SaveReqVO saveReqVO, Long storeId) {
        validateMachineCodeUnique(saveReqVO.getMachineCode(), null);
        PrinterDO printer = PrinterConvert.convert(saveReqVO);
        printer.setStoreId(storeId);
        printerMapper.insert(printer);
        return printer.getId();
    }

    @Override
    public void updatePrinter(PrinterVO.SaveReqVO saveReqVO, Long storeId) {
        validatePrinterInStore(saveReqVO.getId(), storeId);
        validateMachineCodeUnique(saveReqVO.getMachineCode(), saveReqVO.getId());
        PrinterDO printer = PrinterConvert.convert(saveReqVO);
        // storeId 不允许通过更新修改（防跨店搬移设备）
        printer.setStoreId(null);
        printerMapper.updateById(printer);
    }

    @Override
    public void deletePrinter(Long id, Long storeId) {
        validatePrinterInStore(id, storeId);
        printerMapper.deleteById(id);
    }

    @Override
    public PageResult<PrinterDO> getPrinterPage(PrinterVO.PageReqVO pageReqVO) {
        // P1-A：强制以登录店员绑定门店过滤
        Long storeId = pageReqVO.getStoreId();
        return printerMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<PrinterDO>()
                        .eq(storeId != null, PrinterDO::getStoreId, storeId)
                        .eqIfPresent(PrinterDO::getStatus, pageReqVO.getStatus())
                        .orderByAsc(PrinterDO::getSort));
    }

    @Override
    public PrinterDO validatePrinterInStore(Long id, Long storeId) {
        PrinterDO printer = printerMapper.selectById(id);
        if (printer == null || !printer.getStoreId().equals(storeId)) {
            throw exception(PRINTER_NOT_EXISTS);
        }
        return printer;
    }

    /**
     * 终端号唯一校验（同一终端号只允许绑定一台记录；跨店也禁止，防止误绑他人设备）
     */
    private void validateMachineCodeUnique(String machineCode, Long excludeId) {
        if (StrUtil.isBlank(machineCode)) {
            return;
        }
        PrinterDO exist = printerMapper.selectOne(new LambdaQueryWrapperX<PrinterDO>()
                .eq(PrinterDO::getMachineCode, machineCode));
        if (exist != null && (excludeId == null || !exist.getId().equals(excludeId))) {
            throw exception(PRINTER_MACHINE_CODE_DUPLICATE);
        }
    }

}
