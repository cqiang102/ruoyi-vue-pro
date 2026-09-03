package cn.iocoder.yudao.module.restaurant.dal.dataobject.member;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员等级配置 DO（商户可配，每个租户多个等级）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_member_level_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberLevelConfigDO extends TenantBaseDO {

    /**
     * 等级配置编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 等级序号（1 起，数字越大等级越高）
     */
    private Integer level;
    /**
     * 等级名称
     */
    private String name;
    /**
     * 升级所需成长值门槛
     */
    private Integer growthThreshold;
    /**
     * 折扣率（百分比，95 表示 95 折，100 表示不打折）
     */
    private Integer discountRate;
    /**
     * 权益描述
     */
    private String benefits;
    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

}
