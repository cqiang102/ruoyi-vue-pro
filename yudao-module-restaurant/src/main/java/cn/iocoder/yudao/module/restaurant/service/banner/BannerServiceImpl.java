package cn.iocoder.yudao.module.restaurant.service.banner;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.banner.vo.BannerVO;
import cn.iocoder.yudao.module.restaurant.convert.banner.BannerConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.banner.BannerDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.banner.BannerMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 轮播图 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class BannerServiceImpl implements BannerService {

    @Resource
    private BannerMapper bannerMapper;

    @Override
    public Long createBanner(BannerVO.SaveReqVO createReqVO) {
        BannerDO banner = BannerConvert.convert(createReqVO);
        // 防止前端回传 id 造成误更新，新增场景强制置空
        banner.setId(null);
        bannerMapper.insert(banner);
        return banner.getId();
    }

    @Override
    public void updateBanner(BannerVO.SaveReqVO updateReqVO) {
        validateBannerExists(updateReqVO.getId());
        BannerDO updateObj = BannerConvert.convert(updateReqVO);
        bannerMapper.updateById(updateObj);
    }

    @Override
    public void deleteBanner(Long id) {
        validateBannerExists(id);
        bannerMapper.deleteById(id);
    }

    @Override
    public BannerVO.RespVO getBanner(Long id) {
        return BannerConvert.convert(validateBannerExists(id));
    }

    @Override
    public PageResult<BannerVO.RespVO> getBannerPage(BannerVO.PageReqVO pageReqVO) {
        PageResult<BannerDO> page = bannerMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<BannerDO>()
                        .likeIfPresent(BannerDO::getTitle, pageReqVO.getTitle())
                        .eqIfPresent(BannerDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(BannerDO::getSort));
        return new PageResult<>(BannerConvert.convertList(page.getList()), page.getTotal());
    }

    @Override
    public List<BannerVO.RespVO> getBannerList() {
        List<BannerDO> list = bannerMapper.selectList(
                new LambdaQueryWrapperX<BannerDO>()
                        .eq(BannerDO::getStatus, 1)
                        .orderByDesc(BannerDO::getSort));
        return BannerConvert.convertList(list);
    }

    // ========== 辅助 ==========

    private BannerDO validateBannerExists(Long id) {
        if (id == null) {
            throw new ServiceException(ErrorCodeConstants.BANNER_NOT_EXISTS);
        }
        BannerDO banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new ServiceException(ErrorCodeConstants.BANNER_NOT_EXISTS);
        }
        return banner;
    }

}
