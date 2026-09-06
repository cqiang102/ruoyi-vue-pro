package cn.iocoder.yudao.module.restaurant.service.print;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.printer.vo.PrinterVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrintTaskDO;

/**
 * 打印 Service（M-10）
 *
 * @author 餐饮 SaaS
 */
public interface PrintService {

    /**
     * 订单打印触发（接单成功后调用）：为订单门店所有启用的打印机建任务并发送。
     * 内部全量 try-catch，任何失败不影响订单主流程。
     *
     * @param orderId 订单编号
     */
    void printOrder(Long orderId);

    /**
     * 重试失败任务（店员手动触发；校验任务归属本店）
     *
     * @param taskId  任务编号
     * @param storeId 登录店员绑定门店（P1-A 归属校验）
     */
    void retryTask(Long taskId, Long storeId);

    /**
     * 打印任务分页（本店隔离）
     *
     * @param storeId 登录店员绑定门店（P1-A 强制注入）
     */
    PageResult<PrintTaskDO> getTaskPage(PageParam pageParam, Long storeId, Long orderId, Integer status);

    /**
     * 任务 VO 转换（含打印机名称）
     */
    PrinterVO.TaskRespVO convertTask(PrintTaskDO task, String printerName);

}
