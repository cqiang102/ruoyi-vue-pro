package cn.iocoder.yudao.module.restaurant.service.print;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.printer.vo.PrinterVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderItemDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrintTaskDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.printer.PrinterDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.order.OrderItemMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.order.OrderMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.printer.PrintTaskMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.printer.PrinterMapper;
import cn.iocoder.yudao.module.restaurant.service.print.client.PrintClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 打印 Service 实现（M-10）
 * <p>
 * 设计要点：
 * 1. printOrder 全量 try-catch + 逐台打印机独立 try-catch：打印是旁路功能，
 *    任何失败只落任务表（status=2 + errorMsg），绝不影响接单主流程；
 * 2. 远程调用在事务外（acceptOrder 本就无方法级事务，且本方法不写订单表）；
 * 3. origin_id 幂等单号 = "T" + taskId（≤32 位字母数字），重试复用同一 taskId 的 originId，
 *    易联云侧同 originId 重复提交不会重复出纸。
 *
 * @author 餐饮 SaaS
 */
@Service
@Slf4j
public class PrintServiceImpl implements PrintService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private PrinterMapper printerMapper;
    @Resource
    private PrintTaskMapper printTaskMapper;
    @Resource
    private PrintClient printClient;

    @Override
    public void printOrder(Long orderId) {
        try {
            OrderDO order = orderMapper.selectById(orderId);
            if (order == null) {
                log.warn("[printOrder][订单({})不存在，跳过打印]", orderId);
                return;
            }
            List<PrinterDO> printers = printerMapper.selectEnabledListByStore(order.getStoreId());
            if (printers.isEmpty()) {
                log.info("[printOrder][门店({})无启用打印机，跳过打印]", order.getStoreId());
                return;
            }
            String content = buildReceipt(order, orderItemMapper.selectList(
                    new LambdaQueryWrapperX<OrderItemDO>()
                            .eq(OrderItemDO::getOrderId, orderId)));
            for (PrinterDO printer : printers) {
                try {
                    createAndSend(order, printer, content);
                } catch (Exception e) {
                    log.error("[printOrder][打印机({})打印异常 orderId({})]", printer.getId(), orderId, e);
                }
            }
        } catch (Exception e) {
            // 打印为旁路功能：任何异常只记日志，不影响订单主流程
            log.error("[printOrder][订单({})打印触发异常]", orderId, e);
        }
    }

    private void createAndSend(OrderDO order, PrinterDO printer, String content) {
        PrintTaskDO task = new PrintTaskDO();
        task.setOrderId(order.getId());
        task.setStoreId(order.getStoreId());
        task.setPrinterId(printer.getId());
        task.setContent(content);
        task.setStatus(0);
        task.setRetryCount(0);
        printTaskMapper.insert(task);
        // origin_id 用任务编号生成，保证 client 内唯一且重试幂等（同 originId 重复提交不重复出纸）
        task.setOriginId("T" + task.getId());
        send(task, printer);
    }

    /**
     * 发送任务并落状态（status：1 成功 / 2 失败）
     */
    private void send(PrintTaskDO task, PrinterDO printer) {
        String errMsg = printClient.print(printer.getMachineCode(), task.getContent(), task.getOriginId());
        task.setStatus(errMsg == null ? 1 : 2);
        task.setErrorMsg(errMsg);
        task.setRetryCount(task.getRetryCount() == null ? 0 : task.getRetryCount());
        task.setSendTime(LocalDateTime.now());
        printTaskMapper.updateById(task);
    }

    @Override
    public void retryTask(Long taskId, Long storeId) {
        PrintTaskDO task = printTaskMapper.selectById(taskId);
        if (task == null || !storeId.equals(task.getStoreId())) {
            return;
        }
        PrinterDO printer = printerMapper.selectById(task.getPrinterId());
        if (printer == null) {
            task.setStatus(2);
            task.setErrorMsg("打印机不存在或已删除");
            printTaskMapper.updateById(task);
            return;
        }
        task.setRetryCount((task.getRetryCount() == null ? 0 : task.getRetryCount()) + 1);
        send(task, printer);
    }

    @Override
    public PageResult<PrintTaskDO> getTaskPage(PageParam pageParam, Long storeId, Long orderId, Integer status) {
        return printTaskMapper.selectPage(pageParam, storeId, orderId, status);
    }

    @Override
    public PrinterVO.TaskRespVO convertTask(PrintTaskDO task, String printerName) {
        PrinterVO.TaskRespVO resp = new PrinterVO.TaskRespVO();
        resp.setId(task.getId());
        resp.setOrderId(task.getOrderId());
        resp.setPrinterId(task.getPrinterId());
        resp.setPrinterName(printerName);
        resp.setStatus(task.getStatus());
        resp.setErrorMsg(task.getErrorMsg());
        resp.setRetryCount(task.getRetryCount());
        resp.setSendTime(task.getSendTime());
        return resp;
    }

    /**
     * 生成小票内容（易联云排版指令：<BR> 换行，<B> 加粗，<CB> 居中）
     */
    private String buildReceipt(OrderDO order, List<OrderItemDO> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("<CB><B>门店小票</B></CB><BR>");
        sb.append("--------------------------------<BR>");
        sb.append("单号：").append(order.getOrderNo()).append("<BR>");
        sb.append("时间：").append(order.getCreateTime() == null ? "" : TIME_FMT.format(order.getCreateTime())).append("<BR>");
        sb.append("类型：").append(typeText(order)).append("<BR>");
        if (order.getPickupNo() != null) {
            sb.append("<B>取餐号：").append(order.getPickupNo()).append("</B><BR>");
        }
        if (order.getTableId() != null) {
            sb.append("桌台：").append(order.getTableId()).append("<BR>");
        }
        if (StrUtil.isNotBlank(order.getReceiverName())) {
            sb.append("客户：").append(order.getReceiverName()).append(" ").append(order.getReceiverPhone()).append("<BR>");
            sb.append("地址：").append(order.getReceiverAddress()).append("<BR>");
        }
        sb.append("--------------------------------<BR>");
        for (OrderItemDO item : items) {
            String spec = StrUtil.blankToDefault(item.getSpecDesc(), "");
            String addon = StrUtil.blankToDefault(item.getAddonDesc(), "");
            String suffix = (spec + (spec.isEmpty() || addon.isEmpty() ? "" : "/") + addon);
            sb.append(item.getDishName()).append(suffix.isEmpty() ? "" : "(" + suffix + ")")
                    .append(" x").append(item.getQuantity())
                    .append("  ¥").append(fen(item.getTotalPrice()))
                    .append("<BR>");
        }
        sb.append("--------------------------------<BR>");
        if (order.getDiscountPrice() != null && order.getDiscountPrice() > 0) {
            sb.append("优惠：-¥").append(fen(order.getDiscountPrice())).append("<BR>");
        }
        if (order.getDeliveryFee() != null && order.getDeliveryFee() > 0) {
            sb.append("配送费：¥").append(fen(order.getDeliveryFee())).append("<BR>");
        }
        sb.append("<B>实付：¥").append(fen(order.getPayPrice() == null ? order.getTotalPrice() : order.getPayPrice())).append("</B><BR>");
        if (StrUtil.isNotBlank(order.getRemark())) {
            sb.append("备注：").append(order.getRemark()).append("<BR>");
        }
        sb.append("<CB>谢谢惠顾</CB>");
        return sb.toString();
    }

    private static String typeText(OrderDO order) {
        Integer type = order.getType();
        if (type == null) {
            return "堂食";
        }
        switch (type) {
            case 2:
                return "自取";
            case 3:
                return "外卖";
            default:
                return "堂食";
        }
    }

    private static String fen(Long amount) {
        return amount == null ? "0.00" : String.format("%.2f", amount / 100.0);
    }

}
