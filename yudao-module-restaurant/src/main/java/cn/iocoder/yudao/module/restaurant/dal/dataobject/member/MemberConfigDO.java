package cn.iocoder.yudao.module.restaurant.dal.dataobject.member;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员营销配置 DO（租户级，每个租户一条）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_member_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberConfigDO extends TenantBaseDO {

    /**
     * 配置编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 每消费 1 元获得的积分（默认 1）
     */
    private Integer earnPerYuan;
    /**
     * 每积分可抵扣的金额（单位：分，默认 10 表示 0.1 元）
     */
    private Integer deductPerPoint;
    /**
     * 可使用积分抵现的最低订单金额（单位：分，默认 0）
     */
    private Integer minDeductAmount;
    /**
     * 积分抵现占订单金额的上限比例（百分比，默认 50 表示最多抵 50%）
     */
    private Integer maxDeductRate;
    /**
     * 升级方式：0-按成长值 1-按累计消费（默认 0）
     */
    private Integer levelUpMode;
    /**
     * 状态：0-关闭 1-开启
     */
    private Integer status;

}
