package cn.iocoder.yudao.module.restaurant.service.packageconf;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.packageconf.vo.TenantSubscriptionVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishCategoryDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.packageconf.PackageConfigDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.packageconf.TenantSubscriptionDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.TableDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dish.DishCategoryMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.packageconf.TenantSubscriptionMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.StoreMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.TableMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 租户订阅 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
public class TenantSubscriptionServiceImpl extends ServiceImpl<TenantSubscriptionMapper, TenantSubscriptionDO>
        implements TenantSubscriptionService {

    @Resource
    private PackageConfigService packageConfigService;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private DishCategoryMapper dishCategoryMapper;
    @Resource
    private TableMapper tableMapper;

    @Override
    public Long openSubscription(TenantSubscriptionVO.OpenReqVO reqVO) {
        PackageConfigDO pkg = packageConfigService.getActivePackageRequired(reqVO.getPackageId());
        Long tenantId = reqVO.getTenantId();
        LocalDateTime now = LocalDateTime.now();
        return TenantUtils.execute(tenantId, () -> {
            TenantSubscriptionDO sub = new TenantSubscriptionDO();
            sub.setTenantId(tenantId);
            sub.setPackageId(pkg.getId());
            sub.setStartTime(now);
            sub.setExpireTime(now.plusMonths(pkg.getDurationMonths()));
            sub.setStatus(1);
            sub.setPayOrderId(reqVO.getPayOrderId());
            sub.setAmount(reqVO.getAmount());
            getBaseMapper().insert(sub);
            initTenantDefaultData();
            return sub.getId();
        });
    }

    @Override
    public PageResult<TenantSubscriptionVO.RespVO> getSubscriptionPage(TenantSubscriptionVO.PageReqVO pageReqVO) {
        return TenantUtils.executeIgnore(() -> {
            PageResult<TenantSubscriptionDO> page = getBaseMapper().selectPage(pageReqVO,
                    new LambdaQueryWrapperX<TenantSubscriptionDO>()
                            .eqIfPresent(TenantSubscriptionDO::getTenantId, pageReqVO.getTenantId())
                            .eqIfPresent(TenantSubscriptionDO::getPackageId, pageReqVO.getPackageId())
                            .eqIfPresent(TenantSubscriptionDO::getStatus, pageReqVO.getStatus())
                            .orderByDesc(TenantSubscriptionDO::getId));
            List<TenantSubscriptionVO.RespVO> list = new ArrayList<>(page.getList().size());
            for (TenantSubscriptionDO s : page.getList()) {
                TenantSubscriptionVO.RespVO respVO = new TenantSubscriptionVO.RespVO();
                respVO.setId(s.getId());
                respVO.setTenantId(s.getTenantId());
                respVO.setPackageId(s.getPackageId());
                PackageConfigDO pkg = packageConfigService.getActivePackageRequired(s.getPackageId());
                respVO.setPackageName(pkg != null ? pkg.getName() : null);
                respVO.setStartTime(s.getStartTime());
                respVO.setExpireTime(s.getExpireTime());
                respVO.setStatus(s.getStatus());
                respVO.setPayOrderId(s.getPayOrderId());
                respVO.setAmount(s.getAmount());
                respVO.setCreateTime(s.getCreateTime());
                respVO.setUpdateTime(s.getUpdateTime());
                list.add(respVO);
            }
            return new PageResult<>(list, page.getTotal());
        });
    }

    @Override
    public void checkActive(Long tenantId) {
        boolean active = TenantUtils.executeIgnore(() -> {
            TenantSubscriptionDO sub = getBaseMapper().selectOne(new LambdaQueryWrapperX<TenantSubscriptionDO>()
                    .eq(TenantSubscriptionDO::getTenantId, tenantId)
                    .eq(TenantSubscriptionDO::getStatus, 1)
                    .gt(TenantSubscriptionDO::getExpireTime, LocalDateTime.now())
                    .last("LIMIT 1"));
            return sub != null;
        });
        if (!active) {
            throw new ServiceException(ErrorCodeConstants.SUBSCRIPTION_NOT_ACTIVE);
        }
    }

    // ===================== 私有辅助：初始化默认商户数据 =====================

    /**
     * 在目标租户上下文内执行：建 1 个总店、4 个菜品分类、10 张桌台。
     * 当前租户上下文已由 TenantUtils.execute 切换，DO 的 tenant_id 由框架自动注入。
     */
    private void initTenantDefaultData() {
        StoreDO store = new StoreDO();
        store.setName("总店");
        store.setStatus(1);
        storeMapper.insert(store);
        Long storeId = store.getId();

        List<String> categories = Arrays.asList("热菜", "凉菜", "主食", "饮品");
        int sort = 1;
        for (String name : categories) {
            DishCategoryDO category = new DishCategoryDO();
            category.setName(name);
            category.setSort(sort++);
            category.setStatus(1);
            dishCategoryMapper.insert(category);
        }

        for (int i = 1; i <= 10; i++) {
            TableDO table = new TableDO();
            table.setStoreId(storeId);
            table.setTableNo(String.format("A%02d", i));
            table.setCategory("大厅");
            table.setSeats(4);
            table.setStatus(1);
            tableMapper.insert(table);
        }
    }

}
