package cn.iocoder.yudao.module.restaurant.dal.dataobject.printer;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 云打印机终端 DO（M-10 易联云）
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_printer")
@Data
@EqualsAndHashCode(callSuper = true)
public class PrinterDO extends TenantBaseDO {

    /**
     * 打印机编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店编号（归属门店，店员端按登录门店隔离）
     */
    private Long storeId;
    /**
     * 打印机名称（如"后厨单"、"收银台"）
     */
    private String name;
    /**
     * 易联云终端号 machine_code
     */
    private String machineCode;
    /**
     * 打印联类型：1 客用单 2 后厨单
     */
    private Integer printType;
    /**
     * 状态：1启用 0停用
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;

}
