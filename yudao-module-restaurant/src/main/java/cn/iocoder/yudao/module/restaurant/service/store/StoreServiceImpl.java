package cn.iocoder.yudao.module.restaurant.service.store;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreVO;
import cn.iocoder.yudao.module.restaurant.convert.store.StoreConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.StoreMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 门店 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class StoreServiceImpl implements StoreService {

    @Resource
    private StoreMapper storeMapper;

    @Override
    public Long createStore(StoreVO.SaveReqVO createReqVO) {
        StoreDO store = new StoreDO()
                .setName(createReqVO.getName())
                .setContact(createReqVO.getContact())
                .setPhone(createReqVO.getPhone())
                .setAddress(createReqVO.getAddress())
                .setBusinessStart(createReqVO.getBusinessStart())
                .setBusinessEnd(createReqVO.getBusinessEnd())
                .setStatus(createReqVO.getStatus())
                .setDeliveryFee(createReqVO.getDeliveryFee())
                .setMinOrderAmount(createReqVO.getMinOrderAmount());
        storeMapper.insert(store);
        return store.getId();
    }

    @Override
    public void updateStore(StoreVO.SaveReqVO updateReqVO) {
        StoreDO existing = validateStoreExists(updateReqVO.getId());
        existing.setName(updateReqVO.getName())
                .setContact(updateReqVO.getContact())
                .setPhone(updateReqVO.getPhone())
                .setAddress(updateReqVO.getAddress())
                .setBusinessStart(updateReqVO.getBusinessStart())
                .setBusinessEnd(updateReqVO.getBusinessEnd())
                .setStatus(updateReqVO.getStatus())
                .setDeliveryFee(updateReqVO.getDeliveryFee())
                .setMinOrderAmount(updateReqVO.getMinOrderAmount());
        storeMapper.updateById(existing);
    }

    @Override
    public void deleteStore(Long id) {
        validateStoreExists(id);
        storeMapper.deleteById(id);
    }

    @Override
    public StoreVO.RespVO getStore(Long id) {
        return StoreConvert.convert(storeMapper.selectById(id));
    }

    @Override
    public PageResult<StoreVO.RespVO> getStorePage(StoreVO.PageReqVO pageReqVO) {
        PageResult<StoreDO> page = storeMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<StoreDO>()
                        .likeIfPresent(StoreDO::getName, pageReqVO.getName())
                        .eqIfPresent(StoreDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(StoreDO::getId));
        return new PageResult<>(convertList(page.getList(), StoreConvert::convert), page.getTotal());
    }

    @Override
    public List<StoreVO.RespVO> getStoreSimpleList() {
        List<StoreDO> list = storeMapper.selectList();
        return convertList(list, StoreConvert::convert);
    }

    // ========== 辅助 ==========

    public StoreDO validateStoreExists(Long id) {
        StoreDO store = storeMapper.selectById(id);
        if (store == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_NOT_EXISTS);
        }
        return store;
    }

}
