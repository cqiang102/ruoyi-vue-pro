package cn.iocoder.yudao.module.restaurant.service.portal;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.portal.vo.PortalMenuVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.portal.PortalMenuDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.portal.PortalMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.PORTAL_MENU_NOT_EXISTS;

/**
 * 我的服务菜单 Service 实现（M-24）
 *
 * @author 餐饮 SaaS
 */
@Service
public class PortalMenuServiceImpl implements PortalMenuService {

    @Resource
    private PortalMenuMapper portalMenuMapper;

    // ===================== 商户端（admin） =====================

    @Override
    public Long createPortalMenu(PortalMenuVO reqVO) {
        PortalMenuDO menu = BeanUtils.toBean(reqVO, PortalMenuDO.class);
        if (menu.getStoreId() == null) {
            menu.setStoreId(0L);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        portalMenuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    public void updatePortalMenu(PortalMenuVO reqVO) {
        validateExists(reqVO.getId());
        PortalMenuDO updateObj = BeanUtils.toBean(reqVO, PortalMenuDO.class);
        portalMenuMapper.updateById(updateObj);
    }

    @Override
    public void deletePortalMenu(Long id) {
        validateExists(id);
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

}
