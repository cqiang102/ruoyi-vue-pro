package cn.iocoder.yudao.module.restaurant.service.notice;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notice.NoticeDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.notice.NoticeMapper;
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
public class NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    public Long createNotice(NoticeVO reqVO) {
        NoticeDO notice = BeanUtils.toBean(reqVO, NoticeDO.class);
        if (notice.getStatus() == null) {
            notice.setStatus(0);
        }
        if (notice.getSort() == null) {
            notice.setSort(0);
        }
        if (notice.getStoreId() == null) {
            notice.setStoreId(0L);
        }
        noticeMapper.insert(notice);
        return notice.getId();
    }

    public void updateNotice(NoticeVO reqVO) {
        validateExists(reqVO.getId());
        noticeMapper.updateById(BeanUtils.toBean(reqVO, NoticeDO.class));
    }

    public void deleteNotice(Long id) {
        validateExists(id);
        noticeMapper.deleteById(id);
    }

    public List<NoticeVO> getNoticeList(Long storeId) {
        List<NoticeDO> list = noticeMapper.selectList(
                new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<NoticeDO>()
                        .eqIfPresent(NoticeDO::getStoreId, storeId)
                        .orderByDesc(NoticeDO::getId));
        return BeanUtils.toBean(list, NoticeVO.class);
    }

    /**
     * 会员端：本店 + 全平台，只取已发布
     */
    public List<NoticeVO> getNoticeListForMember(Long storeId) {
        return BeanUtils.toBean(noticeMapper.selectVisibleList(storeId), NoticeVO.class);
    }

    private void validateExists(Long id) {
        if (id == null || noticeMapper.selectById(id) == null) {
            throw exception(NOTICE_NOT_EXISTS);
        }
    }

}
