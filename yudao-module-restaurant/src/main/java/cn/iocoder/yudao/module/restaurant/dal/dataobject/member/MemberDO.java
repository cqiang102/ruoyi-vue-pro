package cn.iocoder.yudao.module.restaurant.dal.dataobject.member;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员档案 DO（一个用户在一个租户下一条）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_member")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberDO extends TenantBaseDO {

    /**
     * 会员编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户编号（关联 system_users）
     */
    private Long userId;
    /**
     * 当前等级配置编号（关联 restaurant_member_level_config.id）
     */
    private Long levelId;
    /**
     * 当前成长值
     */
    private Integer growthValue;
    /**
     * 积分余额
     */
    private Integer pointBalance;
    /**
     * 累计消费金额（单位：分）
     */
    private Long totalConsume;
    /**
     * 乐观锁版本号（CAS 并发控制，复用芋道 pay 余额方案）
     */
    @Version
    private Integer version;

}
