package cn.iocoder.yudao.module.restaurant.dal.dataobject.member;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员卡商品 DO（M-26）
 *
 * 商户配置的可售卡（如「年卡 99 元」），消费者用余额购买，
 * 购买行为落 MemberCardOrderDO 记录。卡本身不直接含余额，
 * 余额走 MemberRecharge 充值链路，卡代表「可售权益商品」。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_member_card")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberCardDO extends TenantBaseDO {

    /**
     * 卡编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 卡名称（如：金卡年卡）
     */
    private String name;
    /**
     * 售价（单位：分）
     */
    private Long price;
    /**
     * 卡描述
     */
    private String description;
    /**
     * 权益说明（MVP 用文本展示，逐行一条权益；结构化权益后续演进）
     */
    private String rights;
    /**
     * 状态：1在售 0下架
     */
    private Integer status;
    /**
     * 已售数量（购买成功后 +1）
     */
    private Integer soldCount;
    /**
     * 排序（越大越靠前）
     */
    private Integer sort;

}
