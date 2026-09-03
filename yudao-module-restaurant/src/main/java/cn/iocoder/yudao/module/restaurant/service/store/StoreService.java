package cn.iocoder.yudao.module.restaurant.service.store;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreVO;

import java.util.List;

/**
 * 门店 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface StoreService {

    /**
     * 创建门店
     */
    Long createStore(StoreVO.SaveReqVO createReqVO);

    /**
     * 更新门店
     */
    void updateStore(StoreVO.SaveReqVO updateReqVO);

    /**
     * 删除门店
     */
    void deleteStore(Long id);

    /**
     * 获得门店
     */
    StoreVO.RespVO getStore(Long id);

    /**
     * 分页查询门店
     */
    PageResult<StoreVO.RespVO> getStorePage(StoreVO.PageReqVO pageReqVO);

    /**
     * 获得全部门店（下拉用）
     */
    List<StoreVO.RespVO> getStoreSimpleList();

}
