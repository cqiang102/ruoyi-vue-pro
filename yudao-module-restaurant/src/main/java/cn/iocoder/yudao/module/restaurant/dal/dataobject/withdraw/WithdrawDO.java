package cn.iocoder.yudao.module.restaurant.dal.dataobject.withdraw;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 提现单 DO（M-30）
 *
 * <p>MVP：审核通过后由平台「线下打款 + 人工标记」，不接自动代付通道；
 * 收入口径 = 已支付订单实付金额（真实流水），提现只记账不冻结。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_withdraw")
@Data
@EqualsAndHashCode(callSuper = true)
public class WithdrawDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long storeId;

    /**
     * 提现金额（分）
     */
    private Long amount;

    /**
     * 状态：0-待审核 1-已打款 2-已驳回
     */
    private Integer status;

    /**
     * 提现账户快照（打款依据，防账户事后被改）
     */
    private String accountSnapshot;

    /**
     * 申请备注
     */
    private String applyRemark;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核人（用户名字符串，跨表免联查）
     */
    private String auditUser;

}
