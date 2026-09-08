package cn.iocoder.yudao.module.restaurant.dal.dataobject.reserve;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预约规则 DO（M-09）
 *
 * <p>按「门店 + 星期」配置可预约时段。weekday = -1 表示每天生效。
 * 会员端据此生成可选时段，并扣减已预约人数给出余量。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_reserve_rule")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReserveRuleDO extends TenantBaseDO {

    /**
     * 规则编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 门店编号
     */
    private Long storeId;

    /**
     * 星期：0-周日 1-周一 … 6-周六；-1 = 每天
     */
    private Integer weekday;

    /**
     * 开始时间（HH:mm）
     */
    private String startTime;

    /**
     * 结束时间（HH:mm）
     */
    private String endTime;

    /**
     * 时段间隔（分钟）
     */
    private Integer slotInterval;

    /**
     * 单时段可预约人数
     */
    private Integer maxPeople;

    /**
     * 最多可提前预约天数
     */
    private Integer advanceDays;

    /**
     * 状态：0-启用 1-停用
     */
    private Integer status;

}
