package cn.iocoder.yudao.module.restaurant.dal.dataobject.withdraw;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提现账户 DO（M-30）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_withdraw_account")
@Data
@EqualsAndHashCode(callSuper = true)
public class WithdrawAccountDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 账户类型：1-对公银行 2-微信 3-支付宝
     */
    private Integer accountType;

    /**
     * 账户名称（收款人/企业名）
     */
    private String accountName;

    /**
     * 账号（卡号/微信号/支付宝账号）
     */
    private String accountNo;

    /**
     * 状态：0-启用 1-停用
     */
    private Integer status;

}
