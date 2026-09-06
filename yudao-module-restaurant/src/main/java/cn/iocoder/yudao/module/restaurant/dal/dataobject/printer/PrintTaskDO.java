package cn.iocoder.yudao.module.restaurant.dal.dataobject.printer;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 打印任务 DO（M-10）：订单小票快照 + 发送状态，支持失败重试
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_print_task")
@Data
@EqualsAndHashCode(callSuper = true)
public class PrintTaskDO extends TenantBaseDO {

    /**
     * 任务编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单编号
     */
    private Long orderId;
    /**
     * 门店编号（冗余，用于本店任务隔离）
     */
    private Long storeId;
    /**
     * 打印机编号
     */
    private Long printerId;
    /**
     * 幂等单号（origin_id，client_id 内唯一，≤32 位字母数字）
     */
    private String originId;
    /**
     * 小票内容快照（易联云排版指令文本）
     */
    private String content;
    /**
     * 状态：0待打印 1成功 2失败
     */
    private Integer status;
    /**
     * 失败原因（易联云 error_description）
     */
    private String errorMsg;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 发送时间（最后一次）
     */
    private LocalDateTime sendTime;

}
