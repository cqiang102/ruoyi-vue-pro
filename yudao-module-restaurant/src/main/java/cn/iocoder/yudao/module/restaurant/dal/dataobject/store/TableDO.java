package cn.iocoder.yudao.module.restaurant.dal.dataobject.store;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 桌台 DO
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_table")
@Data
@EqualsAndHashCode(callSuper = true)
public class TableDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店编号
     */
    private Long storeId;
    /**
     * 桌号
     */
    private String tableNo;
    /**
     * 桌台分类（如：大厅/包间）
     */
    private String category;
    /**
     * 座位数
     */
    private Integer seats;
    /**
     * 状态：0空闲 1占用 2待清理
     */
    private Integer status;
    /**
     * 扫码点餐URL
     */
    private String qrcodeContent;

}
