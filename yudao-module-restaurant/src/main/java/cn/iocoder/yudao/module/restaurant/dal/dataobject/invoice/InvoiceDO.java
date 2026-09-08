package cn.iocoder.yudao.module.restaurant.dal.dataobject.invoice;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 开票申请 DO（M-35 电子发票 MVP 壳）
 *
 * <p>MVP 不接第三方开票通道（诺诺/百望等）：申请落库 → 管理端线下开具 → 人工标记已开票/驳回。
 * 订单号/金额/抬头在申请时**快照落库**，事后改单不影响申请单（与 M-30 账户快照同思路）。
 * uk_order_id 唯一键保证一单只可申请一次。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_invoice")
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * 门店编号（申请时订单所属门店快照）
     */
    private Long storeId;

    /**
     * 订单编号（唯一，快照）
     */
    private Long orderId;

    private String orderNo;

    /**
     * 开票金额（分，订单实付快照）
     */
    private Long amount;

    /**
     * 抬头类型：1-企业单位 2-个人
     */
    private Integer type;

    /**
     * 抬头名称（申请时快照，不引用抬头表——抬头可删，申请单不受影响）
     */
    private String title;

    /**
     * 税号快照（可空）
     */
    private String taxNo;

    /**
     * 状态：0-申请中 1-已开票 2-已驳回
     */
    private Integer status;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 开票时间（人工标记）
     */
    private LocalDateTime invoiceTime;

}
