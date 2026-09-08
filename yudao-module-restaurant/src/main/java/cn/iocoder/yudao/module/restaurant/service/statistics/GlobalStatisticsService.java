package cn.iocoder.yudao.module.restaurant.service.statistics;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.restaurant.dal.mysql.statistics.GlobalStatisticsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局统计 Service（P-06 平台运营看板）
 *
 * <p>跨租户聚合：方法标注 @TenantIgnore 跳过多租户插件（平台管理员专属接口）。
 *
 * @author 餐饮 SaaS
 */
@Service
public class GlobalStatisticsService {

    @Resource
    private GlobalStatisticsMapper globalStatisticsMapper;

    @TenantIgnore
    public Map<String, Object> getOverview() {
        Map<String, Object> result = globalStatisticsMapper.selectOverview(LocalDate.now().atStartOfDay());
        if (result == null) {
            result = new HashMap<>();
        }
        return result;
    }

    @TenantIgnore
    public List<Map<String, Object>> getTrend() {
        return globalStatisticsMapper.selectTrend(LocalDate.now().minusDays(29).atStartOfDay());
    }

    @TenantIgnore
    public List<Map<String, Object>> getStoreTop() {
        return globalStatisticsMapper.selectStoreTop(LocalDateTime.now().minusDays(29), 10);
    }

}
