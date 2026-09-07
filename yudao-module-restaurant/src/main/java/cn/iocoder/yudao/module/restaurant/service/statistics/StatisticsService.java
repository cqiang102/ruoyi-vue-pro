package cn.iocoder.yudao.module.restaurant.service.statistics;

import cn.iocoder.yudao.module.restaurant.controller.admin.statistics.vo.StatisticsVO;

/**
 * 数据看板 Service（M-28）
 * <p>
 * 口径：营业额/订单数只统计已支付（pay_status = 1）订单；
 * 趋势/分布/TOP 默认近 30 天；本店隔离（P1-A）由调用方注入 storeId。
 * 无新表：直接聚合 restaurant_order / restaurant_order_item。
 *
 * @author 餐饮 SaaS
 */
public interface StatisticsService {

    /**
     * 看板聚合数据（概览 + 趋势 + 类型分布 + 菜品 TOP）
     *
     * @param storeId 登录店员绑定门店（P1-A 强制注入）
     * @return 聚合结果
     */
    StatisticsVO.DashboardRespVO getDashboard(Long storeId);

}
