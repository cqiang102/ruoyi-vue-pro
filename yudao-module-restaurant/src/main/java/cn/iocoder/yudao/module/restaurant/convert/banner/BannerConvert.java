package cn.iocoder.yudao.module.restaurant.convert.banner;

import cn.iocoder.yudao.module.restaurant.controller.admin.banner.vo.BannerVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.banner.BannerDO;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播图 Convert
 *
 * @author 餐饮 SaaS
 */
public class BannerConvert {

    public static BannerVO.RespVO convert(BannerDO bean) {
        if (bean == null) {
            return null;
        }
        BannerVO.RespVO respVO = new BannerVO.RespVO();
        respVO.setId(bean.getId());
        respVO.setTitle(bean.getTitle());
        respVO.setImage(bean.getImage());
        respVO.setLinkType(bean.getLinkType());
        respVO.setLinkValue(bean.getLinkValue());
        respVO.setStatus(bean.getStatus());
        respVO.setSort(bean.getSort());
        respVO.setCreateTime(bean.getCreateTime());
        return respVO;
    }

    public static List<BannerVO.RespVO> convertList(List<BannerDO> list) {
        List<BannerVO.RespVO> result = new ArrayList<>();
        if (list != null) {
            for (BannerDO item : list) {
                result.add(convert(item));
            }
        }
        return result;
    }

    public static BannerDO convert(BannerVO.SaveReqVO bean) {
        if (bean == null) {
            return null;
        }
        BannerDO bannerDO = new BannerDO();
        bannerDO.setId(bean.getId());
        bannerDO.setTitle(bean.getTitle());
        bannerDO.setImage(bean.getImage());
        bannerDO.setLinkType(bean.getLinkType());
        bannerDO.setLinkValue(bean.getLinkValue());
        bannerDO.setStatus(bean.getStatus());
        bannerDO.setSort(bean.getSort());
        return bannerDO;
    }

}
