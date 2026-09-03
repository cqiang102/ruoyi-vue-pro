package cn.iocoder.yudao.module.restaurant.service.store;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreStaffVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreStaffDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.StoreMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.StoreStaffMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 门店店员映射 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class StoreStaffServiceImpl implements StoreStaffService {

    @Resource
    private StoreStaffMapper storeStaffMapper;
    @Resource
    private StoreMapper storeMapper;

    @Override
    public Long createStoreStaff(StoreStaffVO.SaveReqVO createReqVO) {
        validateStoreExists(createReqVO.getStoreId());
        // P1-A 闭环：一人一店简单模型，uk_admin_user 兜底；并发绑定 catch 后回查
        StoreStaffDO staff = new StoreStaffDO()
                .setAdminUserId(createReqVO.getAdminUserId())
                .setStoreId(createReqVO.getStoreId());
        try {
            storeStaffMapper.insert(staff);
        } catch (DuplicateKeyException e) {
            StoreStaffDO existing = storeStaffMapper.selectByAdminUserId(createReqVO.getAdminUserId());
            if (existing != null) {
                throw new ServiceException(ErrorCodeConstants.STORE_STAFF_DUPLICATE);
            }
            throw e;
        }
        return staff.getId();
    }

    @Override
    public void updateStoreStaff(StoreStaffVO.SaveReqVO updateReqVO) {
        if (updateReqVO.getId() == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_STAFF_NOT_EXISTS);
        }
        StoreStaffDO existing = storeStaffMapper.selectById(updateReqVO.getId());
        if (existing == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_STAFF_NOT_EXISTS);
        }
        validateStoreExists(updateReqVO.getStoreId());
        // 一人一店：若改了 adminUserId，需保证新 adminUserId 未被其他记录占用
        if (!existing.getAdminUserId().equals(updateReqVO.getAdminUserId())) {
            StoreStaffDO conflict = storeStaffMapper.selectByAdminUserId(updateReqVO.getAdminUserId());
            if (conflict != null && !conflict.getId().equals(updateReqVO.getId())) {
                throw new ServiceException(ErrorCodeConstants.STORE_STAFF_DUPLICATE);
            }
        }
        StoreStaffDO update = new StoreStaffDO()
                .setId(updateReqVO.getId())
                .setAdminUserId(updateReqVO.getAdminUserId())
                .setStoreId(updateReqVO.getStoreId());
        storeStaffMapper.updateById(update);
    }

    @Override
    public void deleteStoreStaff(Long id) {
        if (storeStaffMapper.selectById(id) == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_STAFF_NOT_EXISTS);
        }
        storeStaffMapper.deleteById(id);
    }

    @Override
    public StoreStaffVO.RespVO getStoreStaff(Long id) {
        StoreStaffDO staff = storeStaffMapper.selectById(id);
        if (staff == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_STAFF_NOT_EXISTS);
        }
        return toRespVO(staff);
    }

    @Override
    public PageResult<StoreStaffVO.RespVO> getStoreStaffPage(StoreStaffVO.PageReqVO pageReqVO) {
        PageResult<StoreStaffDO> page = storeStaffMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<StoreStaffDO>()
                        .eqIfPresent(StoreStaffDO::getAdminUserId, pageReqVO.getAdminUserId())
                        .eqIfPresent(StoreStaffDO::getStoreId, pageReqVO.getStoreId())
                        .orderByDesc(StoreStaffDO::getId));
        List<StoreStaffVO.RespVO> list = new ArrayList<>(page.getList().size());
        for (StoreStaffDO staff : page.getList()) {
            list.add(toRespVO(staff));
        }
        return new PageResult<>(list, page.getTotal());
    }

    // ===================== 私有辅助 =====================

    private void validateStoreExists(Long storeId) {
        StoreDO store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_NOT_EXISTS);
        }
    }

    private StoreStaffVO.RespVO toRespVO(StoreStaffDO staff) {
        StoreStaffVO.RespVO respVO = new StoreStaffVO.RespVO();
        respVO.setId(staff.getId());
        respVO.setAdminUserId(staff.getAdminUserId());
        respVO.setStoreId(staff.getStoreId());
        respVO.setCreateTime(staff.getCreateTime());
        respVO.setUpdateTime(staff.getUpdateTime());
        return respVO;
    }

}
