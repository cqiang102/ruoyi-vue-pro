package cn.iocoder.yudao.module.restaurant.dal.dataobject.store;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 门店店员映射 DO
 * <p>
 * P1-A：建立后台账号（system_users.id）↔ 门店 的归属关系。
 * 店员即 ADMIN 账号，登录后通过 admin_user_id 反查 store_id，
 * 用于 AdminOrderController 的门店归属校验，杜绝横向越权。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_store_staff")
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreStaffDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 后台账号编号（system_users.id）
     */
    private Long adminUserId;
    /**
     * 门店编号（restaurant_store.id）
     */
    private Long storeId;

}
