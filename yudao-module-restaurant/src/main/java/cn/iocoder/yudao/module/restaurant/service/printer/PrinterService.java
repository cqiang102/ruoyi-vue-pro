package cn.iocoder.yudao.module.restaurant.service.printer;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.printer.vo.PrinterVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrinterDO;

import javax.validation.Valid;

/**
 * 云打印机 Service（M-10）
 *
 * @author 餐饮 SaaS
 */
public interface PrinterService {

    /**
     * 创建打印机（storeId 由服务端按登录店员强制注入，杜绝跨店绑定）
     */
    Long createPrinter(@Valid PrinterVO.SaveReqVO saveReqVO, Long storeId);

    /**
     * 更新打印机（校验归属本店）
     */
    void updatePrinter(@Valid PrinterVO.SaveReqVO saveReqVO, Long storeId);

    /**
     * 删除打印机（校验归属本店）
     */
    void deletePrinter(Long id, Long storeId);

    /**
     * 打印机分页（本店隔离）
     */
    PageResult<PrinterDO> getPrinterPage(PrinterVO.PageReqVO pageReqVO);

    /**
     * 校验打印机存在且归属本店，返回 DO
     */
    PrinterDO validatePrinterInStore(Long id, Long storeId);

}
