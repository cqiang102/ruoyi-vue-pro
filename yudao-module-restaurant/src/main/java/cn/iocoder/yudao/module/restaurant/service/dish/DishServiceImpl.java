package cn.iocoder.yudao.module.restaurant.service.dish;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo.DishVO;
import cn.iocoder.yudao.module.restaurant.convert.dish.DishConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishAddonDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishCategoryDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishSpecDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dish.DishAddonMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dish.DishCategoryMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dish.DishMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dish.DishSpecMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 菜品 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishCategoryMapper dishCategoryMapper;
    @Resource
    private DishSpecMapper dishSpecMapper;
    @Resource
    private DishAddonMapper dishAddonMapper;

    @Override
    public Long createDish(DishVO.SaveReqVO createReqVO) {
        validateCategoryExists(createReqVO.getCategoryId());
        DishDO dish = new DishDO()
                .setCategoryId(createReqVO.getCategoryId())
                .setName(createReqVO.getName())
                .setImage(createReqVO.getImage())
                .setDescription(createReqVO.getDescription())
                .setPrice(createReqVO.getPrice())
                .setStatus(createReqVO.getStatus())
                .setSoldOut(0)
                .setSort(createReqVO.getSort());
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        saveSpecsAndAddons(dishId, createReqVO.getSpecs(), createReqVO.getAddons());
        return dishId;
    }

    @Override
    public void updateDish(DishVO.SaveReqVO updateReqVO) {
        DishDO existing = validateDishExists(updateReqVO.getId());
        validateCategoryExists(updateReqVO.getCategoryId());
        existing.setCategoryId(updateReqVO.getCategoryId())
                .setName(updateReqVO.getName())
                .setImage(updateReqVO.getImage())
                .setDescription(updateReqVO.getDescription())
                .setPrice(updateReqVO.getPrice())
                .setStatus(updateReqVO.getStatus())
                .setSort(updateReqVO.getSort());
        dishMapper.updateById(existing);
        // 规格/加料：先删后插，保证与前端提交一致
        dishSpecMapper.delete(DishSpecDO::getDishId, updateReqVO.getId());
        dishAddonMapper.delete(DishAddonDO::getDishId, updateReqVO.getId());
        saveSpecsAndAddons(updateReqVO.getId(), updateReqVO.getSpecs(), updateReqVO.getAddons());
    }

    @Override
    public void deleteDish(Long id) {
        validateDishExists(id);
        dishMapper.deleteById(id);
        dishSpecMapper.delete(DishSpecDO::getDishId, id);
        dishAddonMapper.delete(DishAddonDO::getDishId, id);
    }

    @Override
    public DishVO.RespVO getDish(Long id) {
        DishDO dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new ServiceException(ErrorCodeConstants.DISH_NOT_EXISTS);
        }
        return buildRespVO(dish);
    }

    @Override
    public PageResult<DishVO.RespVO> getDishPage(DishVO.PageReqVO pageReqVO) {
        PageResult<DishDO> page = dishMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<DishDO>()
                        .likeIfPresent(DishDO::getName, pageReqVO.getName())
                        .eqIfPresent(DishDO::getCategoryId, pageReqVO.getCategoryId())
                        .eqIfPresent(DishDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(DishDO::getSort));
        return new PageResult<>(convertList(page.getList(), this::buildRespVO), page.getTotal());
    }

    @Override
    public List<DishVO.RespVO> getDishSimpleList(Long categoryId) {
        List<DishDO> list = categoryId == null
                ? dishMapper.selectList()
                : dishMapper.selectList(DishDO::getCategoryId, categoryId);
        return convertList(list, this::buildRespVO);
    }

    @Override
    public List<DishVO.RespVO> getDishMenu(Long categoryId) {
        LambdaQueryWrapperX<DishDO> wrapper = new LambdaQueryWrapperX<DishDO>()
                .eqIfPresent(DishDO::getCategoryId, categoryId)
                .eq(DishDO::getStatus, 1)
                .eq(DishDO::getSoldOut, 0)
                .orderByDesc(DishDO::getSort);
        return convertList(dishMapper.selectList(wrapper), this::buildRespVO);
    }

    // ========== 辅助 ==========

    private void saveSpecsAndAddons(Long dishId, List<DishVO.SpecSaveVO> specs, List<DishVO.AddonSaveVO> addons) {
        if (specs != null && !specs.isEmpty()) {
            dishSpecMapper.insertBatch(DishConvert.convertSpecSaveList(specs, dishId));
        }
        if (addons != null && !addons.isEmpty()) {
            dishAddonMapper.insertBatch(DishConvert.convertAddonSaveList(addons, dishId));
        }
    }

    private DishVO.RespVO buildRespVO(DishDO dish) {
        DishVO.RespVO respVO = DishConvert.convert(dish);
        if (respVO == null) {
            return null;
        }
        List<DishSpecDO> specs = dishSpecMapper.selectList(DishSpecDO::getDishId, dish.getId());
        List<DishAddonDO> addons = dishAddonMapper.selectList(DishAddonDO::getDishId, dish.getId());
        respVO.setSpecs(DishConvert.convertSpecList(specs));
        respVO.setAddons(DishConvert.convertAddonList(addons));
        return respVO;
    }

    private DishDO validateDishExists(Long id) {
        DishDO dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new ServiceException(ErrorCodeConstants.DISH_NOT_EXISTS);
        }
        return dish;
    }

    private void validateCategoryExists(Long categoryId) {
        DishCategoryDO category = dishCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new ServiceException(ErrorCodeConstants.DISH_CATEGORY_NOT_EXISTS_FOR_DISH);
        }
    }

}
