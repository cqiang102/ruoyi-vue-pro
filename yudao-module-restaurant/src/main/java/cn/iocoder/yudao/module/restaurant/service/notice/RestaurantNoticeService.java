package cn.iocoder.yudao.module.restaurant.service.notice;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notice.RestaurantNoticeDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.notice.RestaurantNoticeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.NOTICE_NOT_EXISTS;

/**
 * 公告 Service（P-07）
 *
 * @author 餐饮 SaaS
 */
@Service
public class RestaurantNoticeService {

    @Resource
    private RestaurantNoticeMapper restaurantNoticeMapper;

    public Long createNotice(NoticeVO reqVO) {
        RestaurantNoticeDO notice = BeanUtils.toBean(reqVO, RestaurantNoticeDO.class);
        if (notice.getStatus() == null) {
            notice.setStatus(0);
        }
        if (notice.getSort() == null) {
            notice.setSort(0);
        }
        if (notice.getStoreId() == null) {
            notice.setStoreId(0L);
        }
        restaurantNoticeMapper.insert(notice);
        return notice.getId();
    }

    public void updateNotice(NoticeVO reqVO) {
        validateExists(reqVO.getId());
        restaurantNoticeMapper.updateById(BeanUtils.toBean(reqVO, RestaurantNoticeDO.class));
    }

    public void deleteNotice(Long id) {
        validateExists(id);
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

}
