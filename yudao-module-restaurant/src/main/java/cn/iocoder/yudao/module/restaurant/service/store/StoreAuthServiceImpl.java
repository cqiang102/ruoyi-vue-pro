package cn.iocoder.yudao.module.restaurant.service.store;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreStaffDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.order.OrderMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.StoreStaffMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 门店归属鉴权 Service 实现
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class StoreAuthServiceImpl implements StoreAuthService {

    @Resource
    private StoreStaffMapper storeStaffMapper;
    @Resource
    private OrderMapper orderMapper;

    @Override
    public Long getLoginUserStoreId() {
        Long adminUserId = SecurityFrameworkUtils.getLoginUserId();
        if (adminUserId == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_STAFF_NOT_BOUND);
        }
        StoreStaffDO staff = storeStaffMapper.selectByAdminUserId(adminUserId);
        if (staff == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_STAFF_NOT_BOUND);
        }
        return staff.getStoreId();
    }

    @Override
    public void validateStoreAccess(Long targetStoreId) {
        Long loginStoreId = getLoginUserStoreId();
        if (!Objects.equals(loginStoreId, targetStoreId)) {
            throw new ServiceException(ErrorCodeConstants.STORE_STAFF_STORE_MISMATCH);
        }
    }

    @Override
    public void validateOrderOwnership(Long orderId) {
        OrderDO order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_EXISTS);
        }
        validateStoreAccess(order.getStoreId());
    }

}
