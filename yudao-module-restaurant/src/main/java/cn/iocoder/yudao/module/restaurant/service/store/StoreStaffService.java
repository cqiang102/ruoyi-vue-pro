package cn.iocoder.yudao.module.restaurant.service.store;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreStaffVO;

/**
 * 门店店员映射 Service
 * <p>
 * P1-A 闭环：管理后台维护 admin 账号 ↔ 门店 的归属映射。
 *
 * @author 餐饮 SaaS
 */
public interface StoreStaffService {

    /**
     * 创建店员映射
     */
    Long createStoreStaff(StoreStaffVO.SaveReqVO createReqVO);

    /**
     * 更新店员映射
     */
    void updateStoreStaff(StoreStaffVO.SaveReqVO updateReqVO);

    /**
     * 删除店员映射
     */
    void deleteStoreStaff(Long id);

    /**
     * 获得店员映射
     */
    StoreStaffVO.RespVO getStoreStaff(Long id);

    /**
     * 分页查询店员映射
     */
    PageResult<StoreStaffVO.RespVO> getStoreStaffPage(StoreStaffVO.PageReqVO pageReqVO);

}
