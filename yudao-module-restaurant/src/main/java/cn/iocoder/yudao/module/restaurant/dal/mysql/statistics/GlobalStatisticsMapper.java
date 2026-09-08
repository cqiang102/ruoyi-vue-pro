package cn.iocoder.yudao.module.restaurant.dal.mysql.statistics;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 全局统计 Mapper（P-06 平台运营看板）
 *
 * <p>跨租户聚合：SQL <b>不带 tenant_id 条件</b>，由 Service 层 @TenantIgnore 跳过多租户插件。
 * deleted = 0 需手写（自定义 SQL 不走逻辑删除拦截）。
 * 口径：GMV/订单数只统计已支付（pay_status = 1）。
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface GlobalStatisticsMapper {

    /**
     * 总览：商户数 / 会员数 / 累计 GMV / 累计订单数 / 今日 GMV / 今日订单数
     */
    @Select("SELECT (SELECT COUNT(*) FROM restaurant_store s WHERE s.deleted = 0) AS storeCount, " +
            "(SELECT COUNT(*) FROM restaurant_member m WHERE m.deleted = 0) AS memberCount, " +
            "(SELECT IFNULL(SUM(o.pay_price), 0) FROM restaurant_order o " +
            " WHERE o.deleted = 0 AND o.pay_status = 1) AS totalGmv, " +
            "(SELECT COUNT(*) FROM restaurant_order o " +
            " WHERE o.deleted = 0 AND o.pay_status = 1) AS totalOrderCount, " +
            "(SELECT IFNULL(SUM(o.pay_price), 0) FROM restaurant_order o " +
            " WHERE o.deleted = 0 AND o.pay_status = 1 AND o.create_time >= #{todayBegin}) AS todayGmv, " +
            "(SELECT COUNT(*) FROM restaurant_order o " +
            " WHERE o.deleted = 0 AND o.pay_status = 1 AND o.create_time >= #{todayBegin}) AS todayOrderCount")
    Map<String, Object> selectOverview(@Param("todayBegin") LocalDateTime todayBegin);

    /**
     * 全平台近 N 天趋势
     *
     * @return List of {statDate, payTotal, orderCount}
     */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS statDate, " +
            "IFNULL(SUM(pay_price), 0) AS payTotal, COUNT(*) AS orderCount " +
            "FROM restaurant_order " +
            "WHERE deleted = 0 AND pay_status = 1 AND create_time >= #{begin} " +
            "GROUP BY statDate ORDER BY statDate")
    List<Map<String, Object>> selectTrend(@Param("begin") LocalDateTime begin);

    /**
     * 门店 GMV TOP（联店表取名称）
     *
     * @return List of {storeId, storeName, payTotal, orderCount}
     */
    @Select("SELECT o.store_id AS storeId, s.name AS storeName, " +
            "IFNULL(SUM(o.pay_price), 0) AS payTotal, COUNT(*) AS orderCount " +
            "FROM restaurant_order o " +
            "LEFT JOIN restaurant_store s ON s.id = o.store_id AND s.deleted = 0 " +
            "WHERE o.deleted = 0 AND o.pay_status = 1 AND o.create_time >= #{begin} " +
            "GROUP BY o.store_id, s.name " +
            "ORDER BY payTotal DESC LIMIT #{limit}")
    List<Map<String, Object>> selectStoreTop(@Param("begin") LocalDateTime begin,
                                             @Param("limit") Integer limit);

}
