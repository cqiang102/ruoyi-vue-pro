package cn.iocoder.yudao.module.restaurant.service.banner;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.banner.vo.BannerVO;

import java.util.List;

/**
 * 轮播图 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface BannerService {

    /**
     * 创建轮播图
     *
     * @param createReqVO 创建信息
     * @return 轮播图编号
     */
    Long createBanner(BannerVO.SaveReqVO createReqVO);

    /**
     * 更新轮播图
     *
     * @param updateReqVO 更新信息
     */
    void updateBanner(BannerVO.SaveReqVO updateReqVO);

    /**
     * 删除轮播图
     *
     * @param id 轮播图编号
     */
    void deleteBanner(Long id);

    /**
     * 获得轮播图
     *
     * @param id 轮播图编号
     * @return 轮播图
     */
    BannerVO.RespVO getBanner(Long id);

    /**
     * 获得轮播图分页
     *
     * @param pageReqVO 分页查询条件
     * @return 轮播图分页
     */
    PageResult<BannerVO.RespVO> getBannerPage(BannerVO.PageReqVO pageReqVO);

    /**
     * 获得启用中的轮播图列表（小程序端使用，按排序倒序）
     *
     * @return 轮播图列表
     */
    List<BannerVO.RespVO> getBannerList();

}
