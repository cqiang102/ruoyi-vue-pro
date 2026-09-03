package cn.iocoder.yudao.module.restaurant.service.store;

/**
 * 门店归属鉴权 Service
 * <p>
 * P1-A：门店端横向越权修复。
 * AdminOrderController 的所有写/读方法通过本服务校验当前登录账号
 * 与目标门店/订单的归属关系，杜绝 A 店店员操作 B 店订单。
 *
 * @author 餐饮 SaaS
 */
public interface StoreAuthService {

    /**
     * 获取当前登录账号绑定的门店编号。
     * <p>
     * 通过 SecurityFrameworkUtils.getLoginUserId() 取登录账号，
     * 反查 restaurant_store_staff 映射得到 storeId；未绑定抛 STORE_STAFF_NOT_BOUND。
     */
    Long getLoginUserStoreId();

    /**
     * 校验目标门店编号与当前登录账号绑定的门店一致，否则抛 STORE_STAFF_STORE_MISMATCH。
     */
    void validateStoreAccess(Long targetStoreId);

    /**
     * 校验订单归属：订单的 storeId 必须与当前登录账号绑定的门店一致，
     * 订单不存在或跨店时抛对应异常。
     */
    void validateOrderOwnership(Long orderId);

}
