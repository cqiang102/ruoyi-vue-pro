package cn.iocoder.yudao.module.restaurant.service.reserve;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.reserve.ReserveRuleDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.order.OrderMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.reserve.ReserveRuleMapper;
import cn.iocoder.yudao.module.restaurant.enums.order.OrderStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.order.OrderTypeEnum;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.RESERVE_RULE_NOT_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.STORE_STAFF_STORE_MISMATCH;

/**
 * 预约规则 Service 实现（M-09）
 *
 * @author 餐饮 SaaS
 */
@Service
public class ReserveRuleServiceImpl implements ReserveRuleService {

    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");

    @Resource
    private ReserveRuleMapper reserveRuleMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private StoreAuthService storeAuthService;

    // ===================== 商户端（admin） =====================

    @Override
    public Long createRule(ReserveRuleSaveReqVO reqVO) {
        ReserveRuleDO rule = BeanUtils.toBean(reqVO, ReserveRuleDO.class);
        if (rule.getStatus() == null) {
            rule.setStatus(0);
        }
        // 门店端接口：门店归属取登录账号绑定的门店，不采信入参
        rule.setStoreId(storeAuthService.getLoginUserStoreId());
        reserveRuleMapper.insert(rule);
        return rule.getId();
    }

    @Override
    public void updateRule(ReserveRuleSaveReqVO reqVO) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateInStore(reqVO.getId(), storeId);
        ReserveRuleDO updateObj = BeanUtils.toBean(reqVO, ReserveRuleDO.class);
        // 不允许通过更新把预约规则搬到其他门店
        updateObj.setStoreId(storeId);
        reserveRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteRule(Long id) {
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateInStore(id, storeId);
        reserveRuleMapper.deleteById(id);
    }

    @Override
    public List<ReserveRuleSaveReqVO> getRuleList(Long storeId) {
        List<ReserveRuleDO> list = new ArrayList<>();
        if (storeId != null) {
            list = reserveRuleMapper.selectList(
                    new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<ReserveRuleDO>()
                            .eq(ReserveRuleDO::getStoreId, storeId)
                            .orderByAsc(ReserveRuleDO::getWeekday, ReserveRuleDO::getStartTime));
        } else {
            list = reserveRuleMapper.selectList();
        }
        return BeanUtils.toBean(list, ReserveRuleSaveReqVO.class);
    }

    // ===================== 会员端（app） =====================

    @Override
    public List<SlotRespVO> getAvailableSlots(Long storeId, LocalDate date) {
        List<SlotRespVO> result = new ArrayList<>();
        if (storeId == null || date == null) {
            return result;
        }
        // 该日星期：java DayOfWeek 周日=7 → 转成 0
        int weekday = date.getDayOfWeek().getValue() % 7;
        List<ReserveRuleDO> rules = reserveRuleMapper.selectEnabledByStore(storeId).stream()
                .filter(r -> r.getWeekday() == -1 || r.getWeekday() == weekday)
                .collect(java.util.stream.Collectors.toList());
        if (rules.isEmpty()) {
            return result;
        }
        // 已预约人数：type=4（预约）且未取消/未退款，按 reserveTime 归集
        Map<String, Integer> usedMap = loadUsedPeople(storeId, date);

        LocalDateTime now = LocalDateTime.now();
        for (ReserveRuleDO rule : rules) {
            LocalTime start = parseTime(rule.getStartTime());
            LocalTime end = parseTime(rule.getEndTime());
            if (start == null || end == null || !end.isAfter(start)) {
                continue;
            }
            int interval = rule.getSlotInterval() == null || rule.getSlotInterval() <= 0 ? 30 : rule.getSlotInterval();
            int maxPeople = rule.getMaxPeople() == null || rule.getMaxPeople() <= 0 ? 10 : rule.getMaxPeople();
            LocalTime cursor = start;
            while (!cursor.isAfter(end.minusMinutes(1))) {
                String time = cursor.format(HM);
                int used = usedMap.getOrDefault(time, 0);
                int remain = Math.max(0, maxPeople - used);
                // 当天时段：已过时间点不可选
                boolean past = date.equals(LocalDate.now())
                        && LocalDateTime.of(date, cursor).isBefore(now);
                boolean available = remain > 0 && !past;
                result.add(new SlotRespVO(time, remain, available));
                cursor = cursor.plusMinutes(interval);
            }
        }
        result.sort(Comparator.comparing(SlotRespVO::getTime));
        return result;
    }

    private Map<String, Integer> loadUsedPeople(Long storeId, LocalDate date) {
        Map<String, Integer> map = new HashMap<>();
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        List<OrderDO> orders = orderMapper.selectList(
                new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<OrderDO>()
                        .eq(OrderDO::getStoreId, storeId)
                        .eq(OrderDO::getType, OrderTypeEnum.RESERVED.getType())
                        .between(OrderDO::getReserveTime, dayStart, dayEnd)
                        .notIn(OrderDO::getStatus, OrderStatusEnum.CANCELED.getStatus(), OrderStatusEnum.REFUNDED.getStatus()));
        for (OrderDO order : orders) {
            if (order.getReserveTime() == null) {
                continue;
            }
            String time = order.getReserveTime().format(HM);
            int people = order.getPeopleCount() == null ? 1 : order.getPeopleCount();
            map.merge(time, people, Integer::sum);
        }
        return map;
    }

    private LocalTime parseTime(String hhmm) {
        try {
            return LocalTime.parse(hhmm, HM);
        } catch (Exception e) {
            return null;
        }
    }

    private void validateExists(Long id) {
        if (id == null || reserveRuleMapper.selectById(id) == null) {
            throw exception(RESERVE_RULE_NOT_EXISTS);
        }
    }

    /**
     * 门店归属校验（2026-09-28 横向越权排查补充）：
     * 原先只校验"存在性"，A 店店员可删/改 B 店的预约规则。
     */
    private void validateInStore(Long id, Long storeId) {
        ReserveRuleDO rule = id == null ? null : reserveRuleMapper.selectById(id);
        if (rule == null) {
            throw exception(RESERVE_RULE_NOT_EXISTS);
        }
        if (!Objects.equals(rule.getStoreId(), storeId)) {
            throw exception(STORE_STAFF_STORE_MISMATCH);
        }
    }

}
