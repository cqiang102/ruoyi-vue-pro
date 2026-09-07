package cn.iocoder.yudao.module.restaurant.dal.dataobject.point;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 积分兑换记录 DO（M-27）：兑换即扣积分，到店出示核销码取货
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_point_order")
@Data
@EqualsAndHashCode(callSuper = true)
public class PointOrderDO extends TenantBaseDO {

    /**
     * 兑换编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店编号
     */
    private Long storeId;
    /**
     * 会员用户编号（MemberUserDO.id，即登录态 userId）
     */
    private Long userId;
    /**
     * 会员档案编号（restaurant_member.id）
     */
    private Long memberId;
    /**
     * 积分商品编号
     */
    private Long productId;
    /**
     * 商品名称快照
     */
    private String productName;
    /**
     * 单件消耗积分
     */
    private Integer points;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 总消耗积分（= points * quantity）
     */
    private Integer totalPoints;
    /**
     * 核销码（8 位数字，到店出示）
     */
    private String verifyCode;
    /**
     * 状态：0待核销 1已核销 2已取消（取消退积分退库存）
     */
    private Integer status;
    /**
     * 核销时间
     */
    private LocalDateTime verifyTime;

}
