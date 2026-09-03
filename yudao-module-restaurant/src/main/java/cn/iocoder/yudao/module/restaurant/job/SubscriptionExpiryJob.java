package cn.iocoder.yudao.module.restaurant.job;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.packageconf.TenantSubscriptionDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.packageconf.TenantSubscriptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户订阅到期扫描 Job
 * <p>
 * 定时扫描「生效中但已过期」的订阅，置为已过期（状态=2）。
 * 门店登录处已通过 {@code TenantSubscriptionService.checkActive} 拦截过期/欠费租户，
 * 故此处无需再跨模块停用 yudao 租户，职责单一。
 *
 * @author 餐饮 SaaS
 */
@Component
@TenantIgnore
@Slf4j
public class SubscriptionExpiryJob implements JobHandler {

    @Resource
    private TenantSubscriptionMapper subscriptionMapper;

    @Override
    public String execute(String param) {
        LocalDateTime now = LocalDateTime.now();
        List<TenantSubscriptionDO> expired = subscriptionMapper.selectList(new LambdaQueryWrapperX<TenantSubscriptionDO>()
                .eq(TenantSubscriptionDO::getStatus, 1)
                .lt(TenantSubscriptionDO::getExpireTime, now));
        int count = 0;
        for (TenantSubscriptionDO sub : expired) {
            sub.setStatus(2);
            subscriptionMapper.updateById(sub);
            count++;
        }
        log.info("[订阅到期扫描] 处理 {} 条过期订阅", count);
        return "到期订阅扫描处理 " + count + " 条";
    }

}
