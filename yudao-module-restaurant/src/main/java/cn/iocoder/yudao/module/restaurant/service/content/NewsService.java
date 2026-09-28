package cn.iocoder.yudao.module.restaurant.service.content;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.content.NewsDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.content.NewsMapper;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.NEWS_NOT_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.STORE_STAFF_STORE_MISMATCH;

/**
 * 资讯 Service（M-10）
 *
 * @author 餐饮 SaaS
 */
@Service
public class NewsService {

    @Resource
    private NewsMapper newsMapper;
    @Resource
    private StoreAuthService storeAuthService;

    public Long createNews(NewsVO reqVO) {
        NewsDO news = BeanUtils.toBean(reqVO, NewsDO.class);
        if (news.getStatus() == null) {
            news.setStatus(0);
        }
        if (news.getSort() == null) {
            news.setSort(0);
        }
        // 门店端接口：门店归属取登录账号绑定的门店，不采信入参
        news.setStoreId(storeAuthService.getLoginUserStoreId());
        newsMapper.insert(news);
        return news.getId();
    }

    public void updateNews(NewsVO reqVO) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateInStore(reqVO.getId(), storeId);
        NewsDO updateObj = BeanUtils.toBean(reqVO, NewsDO.class);
        // 不允许通过更新把内容搬到其他门店
        updateObj.setStoreId(storeId);
        newsMapper.updateById(updateObj);
    }

    public void deleteNews(Long id) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateInStore(id, storeId);
        newsMapper.deleteById(id);
    }

    public NewsVO getNews(Long id) {
        NewsDO news = newsMapper.selectById(id);
        if (news == null) {
            throw exception(NEWS_NOT_EXISTS);
        }
        return BeanUtils.toBean(news, NewsVO.class);
    }

    public List<NewsVO> getNewsList(Long storeId) {
        List<NewsDO> list = newsMapper.selectList(
                new LambdaQueryWrapperX<NewsDO>()
                        .eqIfPresent(NewsDO::getStoreId, storeId)
                        .orderByAsc(NewsDO::getSort)
                        .orderByDesc(NewsDO::getId));
        return BeanUtils.toBean(list, NewsVO.class);
    }

    /**
     * 会员端：本店 + 全平台，只取已发布
     */
    public List<NewsVO> getNewsListForMember(Long storeId) {
        return BeanUtils.toBean(newsMapper.selectVisibleList(storeId), NewsVO.class);
    }

    private void validateExists(Long id) {
        if (id == null || newsMapper.selectById(id) == null) {
            throw exception(NEWS_NOT_EXISTS);
        }
    }

    /**
     * 门店归属校验（2026-09-28 横向越权排查补充）：
     * 原先只校验"存在性"，A 店店员可删/改 B 店的资讯。
     */
    private void validateInStore(Long id, Long storeId) {
        NewsDO news = id == null ? null : newsMapper.selectById(id);
        if (news == null) {
            throw exception(NEWS_NOT_EXISTS);
        }
        if (!Objects.equals(news.getStoreId(), storeId)) {
            throw exception(STORE_STAFF_STORE_MISMATCH);
        }
    }

}
