package cn.iocoder.yudao.module.restaurant.convert.printer;

import cn.iocoder.yudao.module.restaurant.controller.admin.printer.vo.PrinterVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrinterDO;

import java.util.ArrayList;
import java.util.List;

/**
 * 云打印机 Convert
 *
 * @author 餐饮 SaaS
 */
public class PrinterConvert {

    public static PrinterVO.RespVO convert(PrinterDO bean) {
        if (bean == null) {
            return null;
        }
        PrinterVO.RespVO respVO = new PrinterVO.RespVO();
        respVO.setId(bean.getId());
        respVO.setStoreId(bean.getStoreId());
        respVO.setName(bean.getName());
        respVO.setMachineCode(bean.getMachineCode());
        respVO.setPrintType(bean.getPrintType());
        respVO.setStatus(bean.getStatus());
        respVO.setSort(bean.getSort());
        respVO.setCreateTime(bean.getCreateTime());
        return respVO;
    }

    public static List<PrinterVO.RespVO> convertList(List<PrinterDO> list) {
        List<PrinterVO.RespVO> result = new ArrayList<>();
        if (list != null) {
            for (PrinterDO item : list) {
                result.add(convert(item));
            }
        }
        return result;
    }

    public static PrinterDO convert(PrinterVO.SaveReqVO bean) {
        if (bean == null) {
            return null;
        }
        PrinterDO printerDO = new PrinterDO();
        printerDO.setId(bean.getId());
        printerDO.setName(bean.getName());
        printerDO.setMachineCode(bean.getMachineCode());
        printerDO.setPrintType(bean.getPrintType());
        printerDO.setStatus(bean.getStatus());
        printerDO.setSort(bean.getSort());
        return printerDO;
    }

}
