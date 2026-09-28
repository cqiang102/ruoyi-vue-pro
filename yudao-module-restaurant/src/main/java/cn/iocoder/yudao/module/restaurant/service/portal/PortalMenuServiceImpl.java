package cn.iocoder.yudao.module.restaurant.service.portal;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.portal.vo.PortalMenuVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.portal.PortalMenuDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.portal.PortalMenuMapper;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.PORTAL_MENU_NOT_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.STORE_STAFF_STORE_MISMATCH;

/**
 * 我的服务菜单 Service 实现（M-24）
 *
 * @author 餐饮 SaaS
 */
@Service
public class PortalMenuServiceImpl implements PortalMenuService {

    @Resource
    private PortalMenuMapper portalMenuMapper;
    @Resource
    private StoreAuthService storeAuthService;

    // ===================== 商户端（admin） =====================

    @Override
    public Long createPortalMenu(PortalMenuVO reqVO) {
        PortalMenuDO menu = BeanUtils.toBean(reqVO, PortalMenuDO.class);
        // 门店端接口：门店归属取登录账号绑定的门店，不采信入参
        menu.setStoreId(storeAuthService.getLoginUserStoreId());
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        portalMenuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    public void updatePortalMenu(PortalMenuVO reqVO) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateInStore(reqVO.getId(), storeId);
        PortalMenuDO updateObj = BeanUtils.toBean(reqVO, PortalMenuDO.class);
        // 不允许通过更新把菜单项搬到其他门店
        updateObj.setStoreId(storeId);
        portalMenuMapper.updateById(updateObj);
    }

    @Override
    public void deletePortalMenu(Long id) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateInStore(id, storeId);
        portalMenuMapper.deleteById(id);
    }

    @Override
    public List<PortalMenuVO> getPortalMenuList() {
        List<PortalMenuDO> list = portalMenuMapper.selectList(new LambdaQueryWrapper<>());
        list.sort(Comparator.comparing(m -> m.getSort() == null ? 0 : m.getSort()));
        return BeanUtils.toBean(list, PortalMenuVO.class);
    }

    // ===================== 会员端（app） =====================

    @Override
    public List<PortalMenuVO> getMemberMenuList(Long storeId) {
        // 本店启用项
        List<PortalMenuDO> storeItems = portalMenuMapper.selectList(new LambdaQueryWrapper<PortalMenuDO>()
                .eq(PortalMenuDO::getStoreId, storeId)
                .eq(PortalMenuDO::getStatus, 0));
        if (!storeItems.isEmpty()) {
            storeItems.sort(Comparator.comparing(m -> m.getSort() == null ? 0 : m.getSort()));
            return BeanUtils.toBean(storeItems, PortalMenuVO.class);
        }
        // 本店无配置 → 回退平台默认（store_id = 0）
        List<PortalMenuDO> defaultItems = portalMenuMapper.selectList(new LambdaQueryWrapper<PortalMenuDO>()
                .eq(PortalMenuDO::getStoreId, 0L)
                .eq(PortalMenuDO::getStatus, 0));
        defaultItems.sort(Comparator.comparing(m -> m.getSort() == null ? 0 : m.getSort()));
        return BeanUtils.toBean(defaultItems, PortalMenuVO.class);
    }

    private void validateExists(Long id) {
        if (id == null || portalMenuMapper.selectById(id) == null) {
            throw exception(PORTAL_MENU_NOT_EXISTS);
        }
    }

    /**
     * 门店归属校验（2026-09-28 横向越权排查补充）：
     * 原先只校验"存在性"，A 店店员可删/改 B 店的服务菜单项。
     */
    private void validateInStore(Long id, Long storeId) {
        PortalMenuDO menu = id == null ? null : portalMenuMapper.selectById(id);
        if (menu == null) {
            throw exception(PORTAL_MENU_NOT_EXISTS);
        }
        if (!Objects.equals(menu.getStoreId(), storeId)) {
            throw exception(STORE_STAFF_STORE_MISMATCH);
        }
    }

}
