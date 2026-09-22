package cn.iocoder.yudao.module.restaurant.service.packageconf;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.packageconf.vo.PackageConfigVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.packageconf.PackageConfigDO;

import java.util.List;

/**
 * 租户套餐定义 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface PackageConfigService {

    /**
     * 创建套餐
     */
    Long createPackage(PackageConfigVO.SaveReqVO reqVO);

    /**
     * 更新套餐
     */
    void updatePackage(Long id, PackageConfigVO.SaveReqVO reqVO);

    /**
     * 删除套餐
     */
    void deletePackage(Long id);

    /**
     * 分页查询套餐
     */
    PageResult<PackageConfigVO.RespVO> getPackagePage(PackageConfigVO.PageReqVO pageReqVO);

    /**
     * 获得套餐
     */
    PackageConfigVO.RespVO getPackage(Long id);

    /**
     * 查询启用的套餐列表（开通订阅时下拉用）
     */
    List<PackageConfigDO> getEnabledPackages();

    /**
     * 获取套餐，校验存在且启用
     */
    PackageConfigDO getActivePackageRequired(Long id);

    /**
     * 查询套餐（不做「必须启用」校验，不存在返回 null）。
     *
     * 用于订阅列表回显套餐名等只读场景：套餐停用/删除后，历史订阅仍要能正常展示，
     * 不能因为套餐状态而让查询接口报错（2026-09-21 修复：原先复用 getActivePackageRequired，
     * 套餐一停用订阅列表就返回「套餐已停用，不可订阅」）。
     */
    PackageConfigDO getPackageOrNull(Long id);

}
