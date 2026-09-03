package cn.iocoder.yudao.module.restaurant.service.dish;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dish.vo.DishCategoryVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishCategoryDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dish.DishCategoryMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 菜品分类 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class DishCategoryServiceImpl implements DishCategoryService {

    @Resource
    private DishCategoryMapper dishCategoryMapper;

    @Override
    public Long createDishCategory(DishCategoryVO.SaveReqVO createReqVO) {
        DishCategoryDO category = new DishCategoryDO()
                .setName(createReqVO.getName())
                .setSort(createReqVO.getSort())
                .setStatus(createReqVO.getStatus())
                .setRemark(createReqVO.getRemark());
        dishCategoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void updateDishCategory(DishCategoryVO.SaveReqVO updateReqVO) {
        DishCategoryDO category = validateDishCategoryExists(updateReqVO.getId());
        category.setName(updateReqVO.getName())
                .setSort(updateReqVO.getSort())
                .setStatus(updateReqVO.getStatus())
                .setRemark(updateReqVO.getRemark());
        dishCategoryMapper.updateById(category);
    }

    @Override
    public void deleteDishCategory(Long id) {
        validateDishCategoryExists(id);
        dishCategoryMapper.deleteById(id);
    }

    @Override
    public DishCategoryVO.RespVO getDishCategory(Long id) {
        DishCategoryDO category = dishCategoryMapper.selectById(id);
        return convertToRespVO(category);
    }

    @Override
    public PageResult<DishCategoryVO.RespVO> getDishCategoryPage(DishCategoryVO.PageReqVO pageReqVO) {
        PageResult<DishCategoryDO> page = dishCategoryMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<DishCategoryDO>()
                        .likeIfPresent(DishCategoryDO::getName, pageReqVO.getName())
                        .eqIfPresent(DishCategoryDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(DishCategoryDO::getSort));
        return new PageResult<>(convertList(page.getList(), this::convertToRespVO), page.getTotal());
    }

    @Override
    public List<DishCategoryVO.RespVO> getDishCategorySimpleList() {
        List<DishCategoryDO> list = dishCategoryMapper.selectList(
                new LambdaQueryWrapperX<DishCategoryDO>()
                        .orderByDesc(DishCategoryDO::getSort));
        return convertList(list, this::convertToRespVO);
    }

    @Override
    public List<DishCategoryVO.RespVO> getEnabledCategoryList() {
        List<DishCategoryDO> list = dishCategoryMapper.selectList(
                new LambdaQueryWrapperX<DishCategoryDO>()
                        .eq(DishCategoryDO::getStatus, 1)
                        .orderByDesc(DishCategoryDO::getSort));
        return convertList(list, this::convertToRespVO);
    }

    // ========== 辅助 ==========

    private DishCategoryDO validateDishCategoryExists(Long id) {
        DishCategoryDO category = dishCategoryMapper.selectById(id);
        if (category == null) {
            throw new ServiceException(ErrorCodeConstants.DISH_CATEGORY_NOT_EXISTS);
        }
        return category;
    }

    private DishCategoryVO.RespVO convertToRespVO(DishCategoryDO category) {
        if (category == null) {
            return null;
        }
        DishCategoryVO.RespVO respVO = new DishCategoryVO.RespVO();
        respVO.setId(category.getId());
        respVO.setName(category.getName());
        respVO.setSort(category.getSort());
        respVO.setStatus(category.getStatus());
        respVO.setRemark(category.getRemark());
        respVO.setCreateTime(category.getCreateTime());
        return respVO;
    }

}
