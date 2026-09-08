package cn.iocoder.yudao.module.restaurant.service.content;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.content.NewsDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.content.NewsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.NEWS_NOT_EXISTS;

/**
 * 资讯 Service（M-10）
 *
 * @author 餐饮 SaaS
 */
@Service
public class NewsService {

    @Resource
    private NewsMapper newsMapper;

    public Long createNews(NewsVO reqVO) {
        NewsDO news = BeanUtils.toBean(reqVO, NewsDO.class);
        if (news.getStatus() == null) {
            news.setStatus(0);
        }
        if (news.getSort() == null) {
            news.setSort(0);
        }
        if (news.getStoreId() == null) {
            news.setStoreId(0L);
        }
        newsMapper.insert(news);
        return news.getId();
    }

    public void updateNews(NewsVO reqVO) {
        validateExists(reqVO.getId());
        newsMapper.updateById(BeanUtils.toBean(reqVO, NewsDO.class));
    }

    public void deleteNews(Long id) {
        validateExists(id);
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

}
