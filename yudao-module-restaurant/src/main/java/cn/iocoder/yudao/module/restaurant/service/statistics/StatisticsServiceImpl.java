package cn.iocoder.yudao.module.restaurant.service.statistics;

import cn.hutool.core.convert.Convert;
import cn.iocoder.yudao.module.restaurant.controller.admin.statistics.vo.StatisticsVO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.statistics.StatisticsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据看板 Service 实现（M-28）
 *
 * @author 餐饮 SaaS
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private StatisticsMapper statisticsMapper;

    @Override
    public StatisticsVO.DashboardRespVO getDashboard(Long storeId) {
        LocalDateTime todayBegin = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowBegin = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime monthBegin = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime days30Begin = LocalDate.now().minusDays(29).atStartOfDay();

        StatisticsVO.DashboardRespVO resp = new StatisticsVO.DashboardRespVO();
        resp.setOverview(buildOverview(storeId, todayBegin, tomorrowBegin, monthBegin));
        resp.setTrend(buildTrend(storeId, days30Begin));
        resp.setTypeDist(buildTypeDist(storeId, days30Begin));
        resp.setDishTop(buildDishTop(storeId, days30Begin));
        return resp;
    }

    private StatisticsVO.OverviewRespVO buildOverview(Long storeId, LocalDateTime todayBegin,
                                                      LocalDateTime tomorrowBegin, LocalDateTime monthBegin) {
        Map<String, Object> today = statisticsMapper.selectOverview(storeId, todayBegin, tomorrowBegin);
        Map<String, Object> month = statisticsMapper.selectOverview(storeId, monthBegin, tomorrowBegin);
        StatisticsVO.OverviewRespVO vo = new StatisticsVO.OverviewRespVO();
        long todayPay = Convert.toLong(today.get("payTotal"), 0L);
        long todayCount = Convert.toLong(today.get("orderCount"), 0L);
        long monthPay = Convert.toLong(month.get("payTotal"), 0L);
        long monthCount = Convert.toLong(month.get("orderCount"), 0L);
        vo.setTodayPayTotal(todayPay);
        vo.setTodayOrderCount(todayCount);
        vo.setTodayAvgPrice(todayCount > 0 ? todayPay / todayCount : 0L);
        vo.setMonthPayTotal(monthPay);
        vo.setMonthOrderCount(monthCount);
        vo.setMonthAvgPrice(monthCount > 0 ? monthPay / monthCount : 0L);
        return vo;
    }

    private List<StatisticsVO.TrendRespVO> buildTrend(Long storeId, LocalDateTime begin) {
        // 无订单的日期补 0，保证折线连续
        Map<String, StatisticsVO.TrendRespVO> byDate = new java.util.LinkedHashMap<>();
        LocalDate cur = begin.toLocalDate();
        LocalDate end = LocalDate.now();
        while (!cur.isAfter(end)) {
            StatisticsVO.TrendRespVO vo = new StatisticsVO.TrendRespVO();
            vo.setStatDate(cur.toString());
            vo.setPayTotal(0L);
            vo.setOrderCount(0L);
            byDate.put(cur.toString(), vo);
            cur = cur.plusDays(1);
        }
        for (Map<String, Object> row : statisticsMapper.selectTrend(storeId, begin)) {
            String date = Convert.toStr(row.get("statDate"));
            StatisticsVO.TrendRespVO vo = byDate.get(date);
            if (vo != null) {
                vo.setPayTotal(Convert.toLong(row.get("payTotal"), 0L));
                vo.setOrderCount(Convert.toLong(row.get("orderCount"), 0L));
            }
        }
        return new ArrayList<>(byDate.values());
    }

    private List<StatisticsVO.TypeDistRespVO> buildTypeDist(Long storeId, LocalDateTime begin) {
        List<StatisticsVO.TypeDistRespVO> list = new ArrayList<>();
        for (Map<String, Object> row : statisticsMapper.selectTypeDistribution(storeId, begin)) {
            StatisticsVO.TypeDistRespVO vo = new StatisticsVO.TypeDistRespVO();
            vo.setOrderType(Convert.toInt(row.get("orderType"), 0));
            vo.setOrderCount(Convert.toLong(row.get("orderCount"), 0L));
            vo.setPayTotal(Convert.toLong(row.get("payTotal"), 0L));
            list.add(vo);
        }
        return list;
    }

    private List<StatisticsVO.DishTopRespVO> buildDishTop(Long storeId, LocalDateTime begin) {
        List<StatisticsVO.DishTopRespVO> list = new ArrayList<>();
        for (Map<String, Object> row : statisticsMapper.selectDishTop(storeId, begin, 10)) {
            StatisticsVO.DishTopRespVO vo = new StatisticsVO.DishTopRespVO();
            vo.setDishId(Convert.toLong(row.get("dishId")));
            vo.setDishName(Convert.toStr(row.get("dishName"), "未知菜品"));
            vo.setSoldQuantity(Convert.toLong(row.get("soldQuantity"), 0L));
            vo.setSalesAmount(Convert.toLong(row.get("salesAmount"), 0L));
            list.add(vo);
        }
        return list;
    }

}
