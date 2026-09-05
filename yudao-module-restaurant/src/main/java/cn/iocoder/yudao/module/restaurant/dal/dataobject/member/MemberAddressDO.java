package cn.iocoder.yudao.module.restaurant.dal.dataobject.member;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员收货地址 DO（M-23）
 *
 * 归属以 system_users.id（userId）为准，与会员档案 MemberDO.userId 同源，
 * 不依赖 restaurant_member.id，避免「档案未创建时无法存地址」的前置耦合。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_member_address")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberAddressDO extends TenantBaseDO {

    /**
     * 地址编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户编号（system_users.id，取登录态，不信任前端入参）
     */
    private Long userId;
    /**
     * 收货人姓名
     */
    private String name;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 省市区（文本：省 市 区，空格分隔，MVP 不引入地区库）
     */
    private String region;
    /**
     * 详细地址（街道/门牌/楼栋）
     */
    private String detail;
    /**
     * 是否默认：1是 0否
     */
    private Integer defaultStatus;

}
