package cn.iocoder.yudao.module.restaurant.dal.dataobject.store;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 门店 DO
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_store")
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreDO extends TenantBaseDO {

    /**
     * 门店编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店名称
     */
    private String name;
    /**
     * 联系人
     */
    private String contact;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 地址
     */
    private String address;
    /**
     * 营业开始（HH:mm）
     */
    private String businessStart;
    /**
     * 营业结束（HH:mm）
     */
    private String businessEnd;
    /**
     * 状态：1营业 0打烊
     */
    private Integer status;
    /**
     * 配送费（单位：分，外卖下单时计入订单总价）
     */
    private Long deliveryFee;
    /**
     * 起送金额（单位：分，外卖订单商品总价需达到此值）
     */
    private Long minOrderAmount;

}
