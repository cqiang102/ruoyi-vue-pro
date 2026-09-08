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

    @Override
    public List<StoreVO.RespVO> getStoreListForMember(Double latitude, Double longitude) {
        List<StoreVO.RespVO> list = getStoreSimpleList();
        boolean located = latitude != null && longitude != null
                && Math.abs(latitude) <= 90 && Math.abs(longitude) <= 180;
        if (located) {
            for (StoreVO.RespVO vo : list) {
                vo.setDistanceKm(distanceKm(latitude, longitude, vo.getLatitude(), vo.getLongitude()));
            }
            // 有坐标的按距离升序，无坐标的排最后
            list.sort((a, b) -> {
                Double da = a.getDistanceKm();
                Double db = b.getDistanceKm();
                if (da == null && db == null) return Long.compare(a.getId(), b.getId());
                if (da == null) return 1;
                if (db == null) return -1;
                return Double.compare(da, db);
            });
        }
        return list;
    }

    /**
     * Haversine 球面距离（公里）；任一端缺坐标返回 null
     */
    private Double distanceKm(double lat1, double lng1, Double lat2, Double lng2) {
        if (lat2 == null || lng2 == null) {
            return null;
        }
        double r = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
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
