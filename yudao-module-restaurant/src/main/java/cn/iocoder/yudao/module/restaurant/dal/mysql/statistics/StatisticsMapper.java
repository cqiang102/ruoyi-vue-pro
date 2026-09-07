package cn.iocoder.yudao.module.restaurant.dal.mysql.statistics;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 统计聚合 Mapper（M-28）
 * <p>
 * 用 @Select 原生 SQL 做聚合（MyBatis-Plus 的 Wrapper 不擅长 group by）。
 * 注意：<b>deleted = 0 需手写</b>（自定义 SQL 不走 BaseMapper 逻辑删除拦截）；
 * tenant_id 由多租户插件自动注入，无需手写。
 * <p>
 * 口径：营业额/订单数只统计 <b>已支付</b>（pay_status = 1）订单，含退款单（退款也是真实流水）。
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface StatisticsMapper {

    /**
     * 营业额 + 订单数（时间区间）
     *
     * @return pay_total 营业额(分) / order_count 订单数 / user_count 下单人数
     */
    @Select("SELECT IFNULL(SUM(pay_price), 0) AS payTotal, COUNT(*) AS orderCount, " +
            "COUNT(DISTINCT user_id) AS userCount " +
            "FROM restaurant_order " +
            "WHERE store_id = #{storeId} AND deleted = 0 AND pay_status = 1 " +
            "AND create_time >= #{begin} AND create_time < #{end}")
    Map<String, Object> selectOverview(@Param("storeId") Long storeId,
                                       @Param("begin") LocalDateTime begin,
                                       @Param("end") LocalDateTime end);

    /**
     * 按天趋势（近 N 天）
     *
     * @return List of {statDate, payTotal, orderCount}
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS statDate, " +
            "IFNULL(SUM(pay_price), 0) AS payTotal, COUNT(*) AS orderCount " +
            "FROM restaurant_order " +
            "WHERE store_id = #{storeId} AND deleted = 0 AND pay_status = 1 " +
            "AND create_time >= #{begin} " +
            "GROUP BY statDate ORDER BY statDate")
    List<Map<String, Object>> selectTrend(@Param("storeId") Long storeId,
                                          @Param("begin") LocalDateTime begin);

    /**
     * 订单类型分布（1堂食 2自取 3外卖）
     *
     * @return List of {orderType, orderCount, payTotal}
     */
    @Select("SELECT type AS orderType, COUNT(*) AS orderCount, IFNULL(SUM(pay_price), 0) AS payTotal " +
            "FROM restaurant_order " +
            "WHERE store_id = #{storeId} AND deleted = 0 AND pay_status = 1 " +
            "AND create_time >= #{begin} " +
            "GROUP BY type")
    List<Map<String, Object>> selectTypeDistribution(@Param("storeId") Long storeId,
                                                     @Param("begin") LocalDateTime begin);

    /**
     * 菜品销量 TOP（联明细表）
     *
     * @return List of {dishId, dishName, soldQuantity, salesAmount}
     */
    @Select("SELECT i.dish_id AS dishId, i.dish_name AS dishName, " +
            "SUM(i.quantity) AS soldQuantity, SUM(i.unit_price * i.quantity) AS salesAmount " +
            "FROM restaurant_order_item i " +
            "JOIN restaurant_order o ON o.id = i.order_id AND o.deleted = 0 AND o.pay_status = 1 " +
            "WHERE o.store_id = #{storeId} AND i.deleted = 0 " +
            "AND o.create_time >= #{begin} " +
            "GROUP BY i.dish_id, i.dish_name " +
            "ORDER BY soldQuantity DESC LIMIT #{limit}")
    List<Map<String, Object>> selectDishTop(@Param("storeId") Long storeId,
                                            @Param("begin") LocalDateTime begin,
                                            @Param("limit") Integer limit);

}
