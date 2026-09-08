package cn.iocoder.yudao.module.restaurant.service.portal;

import cn.iocoder.yudao.module.restaurant.controller.admin.portal.vo.PortalMenuVO;

import java.util.List;

/**
 * 我的服务菜单 Service 接口（M-24）
 *
 * @author 餐饮 SaaS
 */
public interface PortalMenuService {

    // ===================== 商户端（admin） =====================

    Long createPortalMenu(PortalMenuVO reqVO);

    void updatePortalMenu(PortalMenuVO reqVO);

    void deletePortalMenu(Long id);

    List<PortalMenuVO> getPortalMenuList();

    // ===================== 会员端（app） =====================

    /**
     * 会员可见菜单：本店启用项优先，本店无配置时回退平台默认（store_id=0）；
     * 按 sort 升序。
     */
    List<PortalMenuVO> getMemberMenuList(Long storeId);

}
