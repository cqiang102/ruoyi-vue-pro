package cn.iocoder.yudao.module.restaurant.dal.dataobject.delivery;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 门店配送配置 DO（M-11 达达快送）：每店一条
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_delivery_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeliveryConfigDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店编号（唯一）
     */
    private Long storeId;
    /**
     * 达达门店编号 shop_no（在达达商户后台创建门店后获得）
     */
    private String dadaShopNo;
    /**
     * 城市 code（达达城市编码，如 021）
     */
    private String cityCode;
    /**
     * 门店纬度（高德坐标系）
     */
    private BigDecimal storeLat;
    /**
     * 门店经度（高德坐标系）
     */
    private BigDecimal storeLng;
    /**
     * 是否启用达达：1启用 0停用（停用走人工配送）
     */
    private Integer enabled;

}
