package cn.iocoder.yudao.module.restaurant.controller.admin.statistics.vo;

import lombok.Data;

import java.util.List;

/**
 * 数据看板 VO（M-28）
 *
 * @author 餐饮 SaaS
 */
@Data
public class StatisticsVO {

    /**
     * 概览：今日 + 本月
     */
    @Data
    public static class OverviewRespVO {
        /**
         * 今日营业额（分）
         */
        private Long todayPayTotal;
        /**
         * 今日订单数
         */
        private Long todayOrderCount;
        /**
         * 今日客单价（分，= 营业额/订单数，无订单为 0）
         */
        private Long todayAvgPrice;
        /**
         * 本月营业额（分）
         */
        private Long monthPayTotal;
        /**
         * 本月订单数
         */
        private Long monthOrderCount;
        /**
         * 本月客单价（分）
         */
        private Long monthAvgPrice;
    }

    /**
     * 按天趋势
     */
    @Data
    public static class TrendRespVO {
        /**
         * 日期 yyyy-MM-dd
         */
        private String statDate;
        /**
         * 营业额（分）
         */
        private Long payTotal;
        /**
         * 订单数
         */
        private Long orderCount;
    }

    /**
     * 订单类型分布
     */
    @Data
    public static class TypeDistRespVO {
        /**
         * 订单类型：1堂食 2自取 3外卖
         */
        private Integer orderType;
        private Long orderCount;
        private Long payTotal;
    }

    /**
     * 菜品销量 TOP
     */
    @Data
    public static class DishTopRespVO {
        private Long dishId;
        private String dishName;
        private Long soldQuantity;
        /**
         * 销售额（分）
         */
        private Long salesAmount;
    }

    /**
     * 看板聚合响应
     */
    @Data
    public static class DashboardRespVO {
        private OverviewRespVO overview;
        /**
         * 近 30 天趋势
         */
        private List<TrendRespVO> trend;
        /**
         * 近 30 天订单类型分布
         */
        private List<TypeDistRespVO> typeDist;
        /**
         * 近 30 天菜品销量 TOP10
         */
        private List<DishTopRespVO> dishTop;
    }

}
