package cn.iocoder.yudao.module.restaurant.service.packageconf;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.packageconf.vo.TenantSubscriptionVO;

/**
 * 租户订阅 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface TenantSubscriptionService {

    /**
     * 开通订阅：创建生效中的订阅记录，并按套餐有效期计算到期时间，
     * 同时为该租户初始化默认商户数据（门店/分类/桌台）。
     *
     * @return 订阅编号
     */
    Long openSubscription(TenantSubscriptionVO.OpenReqVO reqVO);

    /**
     * 租户订阅分页（平台运营视角，跨租户查询）
     */
    PageResult<TenantSubscriptionVO.RespVO> getSubscriptionPage(TenantSubscriptionVO.PageReqVO pageReqVO);

    /**
     * 校验租户当前是否有生效中的订阅，无则抛异常。
     * 供门店登录等入口拦截欠费/过期租户。
     */
    void checkActive(Long tenantId);

}
