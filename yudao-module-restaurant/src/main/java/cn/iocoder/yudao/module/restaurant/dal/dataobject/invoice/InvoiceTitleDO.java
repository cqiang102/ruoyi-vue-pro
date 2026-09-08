package cn.iocoder.yudao.module.restaurant.dal.dataobject.invoice;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发票抬头 DO（M-35 电子发票 MVP）
 *
 * <p>按 userId 归属（会员维度，与租户插件隔离互补）。
 * MVP 不接第三方开票通道，抬头仅供申请时引用。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_invoice_title")
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceTitleDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会员用户编号（system_users.user_id / member 维度登录用户）
     */
    private Long userId;

    /**
     * 抬头类型：1-企业单位 2-个人
     */
    private Integer type;

    /**
     * 抬头名称（企业全称或个人姓名）
     */
    private String title;

    /**
     * 税号（企业抬头必填，个人可空）
     */
    private String taxNo;

    /**
     * 是否默认：0-否 1-是（每用户最多一个默认，置默认时清旧）
     */
    private Integer isDefault;

}
