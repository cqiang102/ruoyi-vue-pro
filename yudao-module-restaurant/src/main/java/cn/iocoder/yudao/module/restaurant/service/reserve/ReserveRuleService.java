package cn.iocoder.yudao.module.restaurant.service.reserve;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约规则 Service 接口（M-09）
 *
 * @author 餐饮 SaaS
 */
public interface ReserveRuleService {

    Long createRule(ReserveRuleSaveReqVO reqVO);

    void updateRule(ReserveRuleSaveReqVO reqVO);

    void deleteRule(Long id);

    List<ReserveRuleSaveReqVO> getRuleList(Long storeId);

    /**
     * 查询某店某天的可用时段（已扣减该时段已预约人数）
     */
    List<SlotRespVO> getAvailableSlots(Long storeId, LocalDate date);

}
