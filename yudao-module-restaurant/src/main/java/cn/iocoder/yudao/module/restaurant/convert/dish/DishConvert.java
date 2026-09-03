package cn.iocoder.yudao.module.restaurant.convert.dish;

import cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo.DishVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishAddonDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishSpecDO;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品 Convert
 *
 * @author 餐饮 SaaS
 */
public class DishConvert {

    public static DishVO.RespVO convert(DishDO bean) {
        if (bean == null) {
            return null;
        }
        DishVO.RespVO respVO = new DishVO.RespVO();
        respVO.setId(bean.getId());
        respVO.setCategoryId(bean.getCategoryId());
        respVO.setName(bean.getName());
        respVO.setImage(bean.getImage());
        respVO.setDescription(bean.getDescription());
        respVO.setPrice(bean.getPrice());
        respVO.setStatus(bean.getStatus());
        respVO.setSoldOut(bean.getSoldOut());
        respVO.setSort(bean.getSort());
        respVO.setCreateTime(bean.getCreateTime());
        return respVO;
    }

    public static DishVO.SpecRespVO convertSpec(DishSpecDO bean) {
        if (bean == null) {
            return null;
        }
        DishVO.SpecRespVO respVO = new DishVO.SpecRespVO();
        respVO.setId(bean.getId());
        respVO.setGroupName(bean.getGroupName());
        respVO.setOptionName(bean.getOptionName());
        respVO.setPriceDelta(bean.getPriceDelta());
        respVO.setSort(bean.getSort());
        return respVO;
    }

    public static DishVO.AddonRespVO convertAddon(DishAddonDO bean) {
        if (bean == null) {
            return null;
        }
        DishVO.AddonRespVO respVO = new DishVO.AddonRespVO();
        respVO.setId(bean.getId());
        respVO.setGroupName(bean.getGroupName());
        respVO.setOptionName(bean.getOptionName());
        respVO.setPriceDelta(bean.getPriceDelta());
        respVO.setMulti(bean.getMulti());
        respVO.setSort(bean.getSort());
        return respVO;
    }

    public static List<DishVO.SpecRespVO> convertSpecList(List<DishSpecDO> list) {
        List<DishVO.SpecRespVO> result = new ArrayList<>();
        if (list != null) {
            for (DishSpecDO item : list) {
                result.add(convertSpec(item));
            }
        }
        return result;
    }

    public static List<DishVO.AddonRespVO> convertAddonList(List<DishAddonDO> list) {
        List<DishVO.AddonRespVO> result = new ArrayList<>();
        if (list != null) {
            for (DishAddonDO item : list) {
                result.add(convertAddon(item));
            }
        }
        return result;
    }

    public static DishSpecDO convertSpecSave(DishVO.SpecSaveVO bean, Long dishId) {
        if (bean == null) {
            return null;
        }
        DishSpecDO specDO = new DishSpecDO();
        specDO.setId(bean.getId());
        specDO.setDishId(dishId);
        specDO.setGroupName(bean.getGroupName());
        specDO.setOptionName(bean.getOptionName());
        specDO.setPriceDelta(bean.getPriceDelta());
        specDO.setSort(bean.getSort());
        return specDO;
    }

    public static DishAddonDO convertAddonSave(DishVO.AddonSaveVO bean, Long dishId) {
        if (bean == null) {
            return null;
        }
        DishAddonDO addonDO = new DishAddonDO();
        addonDO.setId(bean.getId());
        addonDO.setDishId(dishId);
        addonDO.setGroupName(bean.getGroupName());
        addonDO.setOptionName(bean.getOptionName());
        addonDO.setPriceDelta(bean.getPriceDelta());
        addonDO.setMulti(bean.getMulti());
        addonDO.setSort(bean.getSort());
        return addonDO;
    }

    public static List<DishSpecDO> convertSpecSaveList(List<DishVO.SpecSaveVO> list, Long dishId) {
        List<DishSpecDO> result = new ArrayList<>();
        if (list != null) {
            for (DishVO.SpecSaveVO item : list) {
                result.add(convertSpecSave(item, dishId));
            }
        }
        return result;
    }

    public static List<DishAddonDO> convertAddonSaveList(List<DishVO.AddonSaveVO> list, Long dishId) {
        List<DishAddonDO> result = new ArrayList<>();
        if (list != null) {
            for (DishVO.AddonSaveVO item : list) {
                result.add(convertAddonSave(item, dishId));
            }
        }
        return result;
    }

}
