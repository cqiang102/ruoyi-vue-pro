package cn.iocoder.yudao.module.restaurant.service.packageconf;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.packageconf.vo.PackageConfigVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.packageconf.PackageConfigDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.packageconf.PackageConfigMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.packageconf.TenantSubscriptionMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 租户套餐定义 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
public class PackageConfigServiceImpl extends ServiceImpl<PackageConfigMapper, PackageConfigDO> implements PackageConfigService {

    @Resource
    private PackageConfigMapper packageConfigMapper;

    @Resource
    private TenantSubscriptionMapper tenantSubscriptionMapper;

    @Override
    public Long createPackage(PackageConfigVO.SaveReqVO reqVO) {
        PackageConfigDO pkg = new PackageConfigDO();
        pkg.setName(reqVO.getName());
        pkg.setPrice(reqVO.getPrice());
        pkg.setDurationMonths(reqVO.getDurationMonths() != null ? reqVO.getDurationMonths() : 12);
        pkg.setMaxStores(reqVO.getMaxStores());
        pkg.setFeatures(reqVO.getFeatures());
        pkg.setStatus(reqVO.getStatus());
        pkg.setRemark(reqVO.getRemark());
        packageConfigMapper.insert(pkg);
        return pkg.getId();
    }

    @Override
    public void updatePackage(Long id, PackageConfigVO.SaveReqVO reqVO) {
        PackageConfigDO existing = packageConfigMapper.selectById(id);
        if (existing == null) {
            throw new ServiceException(ErrorCodeConstants.PACKAGE_NOT_EXISTS);
        }
        existing.setName(reqVO.getName());
        existing.setPrice(reqVO.getPrice());
        existing.setDurationMonths(reqVO.getDurationMonths() != null ? reqVO.getDurationMonths() : 12);
        existing.setMaxStores(reqVO.getMaxStores());
        existing.setFeatures(reqVO.getFeatures());
        existing.setStatus(reqVO.getStatus());
        existing.setRemark(reqVO.getRemark());
        packageConfigMapper.updateById(existing);
    }

    @Override
    public void deletePackage(Long id) {
        if (packageConfigMapper.selectById(id) == null) {
            throw new ServiceException(ErrorCodeConstants.PACKAGE_NOT_EXISTS);
        }
        // 防止留下悬空引用：restaurant_tenant_subscription.package_id 为 NOT NULL，
        // 若套餐已被（任何租户、含历史）订阅，直接删除会让订阅记录指向不存在的套餐。
        if (tenantSubscriptionMapper.selectCountByPackageId(id) > 0) {
            throw new ServiceException(ErrorCodeConstants.PACKAGE_IN_USE);
        }
        packageConfigMapper.deleteById(id);
    }

    @Override
    public PageResult<PackageConfigVO.RespVO> getPackagePage(PackageConfigVO.PageReqVO pageReqVO) {
        PageResult<PackageConfigDO> page = packageConfigMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<PackageConfigDO>()
                        .likeIfPresent(PackageConfigDO::getName, pageReqVO.getName())
                        .eqIfPresent(PackageConfigDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(PackageConfigDO::getId));
        List<PackageConfigVO.RespVO> list = new ArrayList<>(page.getList().size());
        for (PackageConfigDO p : page.getList()) {
            PackageConfigVO.RespVO respVO = new PackageConfigVO.RespVO();
            respVO.setId(p.getId());
            respVO.setName(p.getName());
            respVO.setPrice(p.getPrice());
            respVO.setDurationMonths(p.getDurationMonths());
            respVO.setMaxStores(p.getMaxStores());
            respVO.setFeatures(p.getFeatures());
            respVO.setStatus(p.getStatus());
            respVO.setRemark(p.getRemark());
            respVO.setCreateTime(p.getCreateTime());
            respVO.setUpdateTime(p.getUpdateTime());
            list.add(respVO);
        }
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public PackageConfigVO.RespVO getPackage(Long id) {
        PackageConfigDO pkg = packageConfigMapper.selectById(id);
        if (pkg == null) {
            throw new ServiceException(ErrorCodeConstants.PACKAGE_NOT_EXISTS);
        }
        PackageConfigVO.RespVO respVO = new PackageConfigVO.RespVO();
        respVO.setId(pkg.getId());
        respVO.setName(pkg.getName());
        respVO.setPrice(pkg.getPrice());
        respVO.setDurationMonths(pkg.getDurationMonths());
        respVO.setMaxStores(pkg.getMaxStores());
        respVO.setFeatures(pkg.getFeatures());
        respVO.setStatus(pkg.getStatus());
        respVO.setRemark(pkg.getRemark());
        respVO.setCreateTime(pkg.getCreateTime());
        respVO.setUpdateTime(pkg.getUpdateTime());
        return respVO;
    }

    @Override
    public List<PackageConfigDO> getEnabledPackages() {
        return packageConfigMapper.selectList(new LambdaQueryWrapperX<PackageConfigDO>()
                .eq(PackageConfigDO::getStatus, 1)
                .orderByDesc(PackageConfigDO::getId));
    }

    @Override
    public PackageConfigDO getActivePackageRequired(Long id) {
        PackageConfigDO pkg = packageConfigMapper.selectById(id);
        if (pkg == null) {
            throw new ServiceException(ErrorCodeConstants.PACKAGE_NOT_EXISTS);
        }
        if (pkg.getStatus() == null || pkg.getStatus() != 1) {
            throw new ServiceException(ErrorCodeConstants.PACKAGE_STATUS_INVALID);
        }
        return pkg;
    }

}
