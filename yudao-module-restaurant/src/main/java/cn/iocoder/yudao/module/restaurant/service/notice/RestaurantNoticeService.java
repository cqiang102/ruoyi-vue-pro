package cn.iocoder.yudao.module.restaurant.service.notice;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notice.RestaurantNoticeDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.notice.RestaurantNoticeMapper;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.NOTICE_NOT_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.STORE_STAFF_STORE_MISMATCH;

/**
 * 公告 Service（P-07）
 *
 * @author 餐饮 SaaS
 */
@Service
public class RestaurantNoticeService {

    @Resource
    private RestaurantNoticeMapper restaurantNoticeMapper;
    @Resource
    private StoreAuthService storeAuthService;

    public Long createNotice(NoticeVO reqVO) {
        RestaurantNoticeDO notice = BeanUtils.toBean(reqVO, RestaurantNoticeDO.class);
        if (notice.getStatus() == null) {
            notice.setStatus(0);
        }
        if (notice.getSort() == null) {
            notice.setSort(0);
        }
        // 门店端接口：门店归属取登录账号绑定的门店，不采信入参
        notice.setStoreId(storeAuthService.getLoginUserStoreId());
        restaurantNoticeMapper.insert(notice);
        return notice.getId();
    }

    public void updateNotice(NoticeVO reqVO) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateInStore(reqVO.getId(), storeId);
        RestaurantNoticeDO updateObj = BeanUtils.toBean(reqVO, RestaurantNoticeDO.class);
        // 不允许通过更新把公告搬到其他门店
        updateObj.setStoreId(storeId);
        restaurantNoticeMapper.updateById(updateObj);
    }

    public void deleteNotice(Long id) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateInStore(id, storeId);
        restaurantNoticeMapper.deleteById(id);
    }

    public List<NoticeVO> getNoticeList(Long storeId) {
        List<RestaurantNoticeDO> list = restaurantNoticeMapper.selectList(
                new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<RestaurantNoticeDO>()
                        .eqIfPresent(RestaurantNoticeDO::getStoreId, storeId)
                        .orderByDesc(RestaurantNoticeDO::getId));
        return BeanUtils.toBean(list, NoticeVO.class);
    }

    /**
     * 会员端：本店 + 全平台，只取已发布
     */
    public List<NoticeVO> getNoticeListForMember(Long storeId) {
        return BeanUtils.toBean(restaurantNoticeMapper.selectVisibleList(storeId), NoticeVO.class);
    }

    private void validateExists(Long id) {
        if (id == null || restaurantNoticeMapper.selectById(id) == null) {
            throw exception(NOTICE_NOT_EXISTS);
        }
    }

    /**
     * 门店归属校验（2026-09-28 横向越权排查补充）：
     * 原先只校验"存在性"，A 店店员可删/改 B 店的公告。
     */
    private void validateInStore(Long id, Long storeId) {
        RestaurantNoticeDO notice = id == null ? null : restaurantNoticeMapper.selectById(id);
        if (notice == null) {
            throw exception(NOTICE_NOT_EXISTS);
        }
        if (!Objects.equals(notice.getStoreId(), storeId)) {
            throw exception(STORE_STAFF_STORE_MISMATCH);
        }
    }

}
