package cn.iocoder.yudao.module.restaurant.service.coupon;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.coupon.vo.CouponTemplateVO;
import cn.iocoder.yudao.module.restaurant.controller.app.coupon.vo.CouponVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.coupon.CouponDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.coupon.CouponTemplateDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.coupon.CouponMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.coupon.CouponTemplateMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 优惠券 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, CouponDO> implements CouponService {

    private static final Random RANDOM = new Random();

    @Resource
    private CouponTemplateMapper couponTemplateMapper;

    // ===================== 模板管理 =====================

    @Override
    public Long createTemplate(CouponTemplateVO.SaveReqVO reqVO) {
        CouponTemplateDO template = new CouponTemplateDO();
        template.setName(reqVO.getName());
        template.setType(reqVO.getType());
        template.setThresholdAmount(reqVO.getThresholdAmount());
        template.setDiscountValue(reqVO.getDiscountValue());
        template.setTotal(reqVO.getTotal());
        template.setTakenCount(0);
        template.setPerLimit(reqVO.getPerLimit() != null ? reqVO.getPerLimit() : 1);
        template.setValidDays(reqVO.getValidDays());
        template.setStatus(reqVO.getStatus());
        couponTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void updateTemplate(Long id, CouponTemplateVO.SaveReqVO reqVO) {
        CouponTemplateDO existing = couponTemplateMapper.selectById(id);
        if (existing == null) {
            throw new ServiceException(ErrorCodeConstants.COUPON_TEMPLATE_NOT_EXISTS);
        }
        existing.setName(reqVO.getName());
        existing.setType(reqVO.getType());
        existing.setThresholdAmount(reqVO.getThresholdAmount());
        existing.setDiscountValue(reqVO.getDiscountValue());
        existing.setTotal(reqVO.getTotal());
        existing.setPerLimit(reqVO.getPerLimit() != null ? reqVO.getPerLimit() : 1);
        existing.setValidDays(reqVO.getValidDays());
        existing.setStatus(reqVO.getStatus());
        couponTemplateMapper.updateById(existing);
    }

    @Override
    public void deleteTemplate(Long id) {
        if (couponTemplateMapper.selectById(id) == null) {
            throw new ServiceException(ErrorCodeConstants.COUPON_TEMPLATE_NOT_EXISTS);
        }
        couponTemplateMapper.deleteById(id);
    }

    @Override
    public PageResult<CouponTemplateVO.RespVO> getTemplatePage(CouponTemplateVO.PageReqVO pageReqVO) {
        PageResult<CouponTemplateDO> page = couponTemplateMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<CouponTemplateDO>()
                        .likeIfPresent(CouponTemplateDO::getName, pageReqVO.getName())
                        .eqIfPresent(CouponTemplateDO::getType, pageReqVO.getType())
                        .eqIfPresent(CouponTemplateDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(CouponTemplateDO::getId));
        List<CouponTemplateVO.RespVO> list = convertTemplate(page.getList());
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public CouponTemplateVO.RespVO getTemplate(Long id) {
        CouponTemplateDO template = couponTemplateMapper.selectById(id);
        if (template == null) {
            throw new ServiceException(ErrorCodeConstants.COUPON_TEMPLATE_NOT_EXISTS);
        }
        return convertTemplate(java.util.Collections.singletonList(template)).get(0);
    }

    // ===================== 用户券 =====================

    @Override
    public CouponDO claimCoupon(Long userId, Long templateId) {
        CouponTemplateDO template = couponTemplateMapper.selectById(templateId);
        if (template == null) {
            throw new ServiceException(ErrorCodeConstants.COUPON_TEMPLATE_NOT_EXISTS);
        }
        if (template.getStatus() == null || template.getStatus() != 1) {
            throw new ServiceException(ErrorCodeConstants.COUPON_TEMPLATE_DISABLED);
        }
        int taken = template.getTakenCount() == null ? 0 : template.getTakenCount();
        if (template.getTotal() != null && taken >= template.getTotal()) {
            throw new ServiceException(ErrorCodeConstants.COUPON_TEMPLATE_SOLD_OUT);
        }
        int perLimit = template.getPerLimit() == null ? 1 : template.getPerLimit();
        long userTaken = couponMapper().selectCount(new LambdaQueryWrapperX<CouponDO>()
                .eq(CouponDO::getUserId, userId)
                .eq(CouponDO::getTemplateId, templateId));
        if (userTaken >= perLimit) {
            throw new ServiceException(ErrorCodeConstants.COUPON_LIMIT_REACHED);
        }
        // P2-F：原实现为 read-modify-write（先读 takenCount 再 updateById 全字段覆盖），
        // 并发下两线程同时读到 taken=N 都写 N+1，导致超发。
        // 改原子自增：仅当 takenCount < total 时 setSql("taken_count = taken_count + 1")，
        // rows==0 即模板已被并发领完，DB 层兜底防超发。
        // 注意：per-user perLimit 仍为读检查（同用户并发领券理论上仍可超领，
        // 但实际场景下单用户重复点击会被前端按钮节流；如需严格防重需补 uk(user_id, template_id) 索引）
        int incRows = couponTemplateMapper.update(null, new LambdaUpdateWrapper<CouponTemplateDO>()
                .eq(CouponTemplateDO::getId, templateId)
                .lt(CouponTemplateDO::getTakenCount, template.getTotal())
                .setSql("taken_count = taken_count + 1"));
        if (incRows == 0) {
            throw new ServiceException(ErrorCodeConstants.COUPON_TEMPLATE_SOLD_OUT);
        }
        LocalDateTime now = LocalDateTime.now();
        CouponDO coupon = new CouponDO();
        coupon.setUserId(userId);
        coupon.setTemplateId(templateId);
        coupon.setCode(genCode());
        coupon.setStatus(0);
        coupon.setExpireTime(template.getValidDays() != null
                ? now.plusDays(template.getValidDays()) : null);
        couponMapper().insert(coupon);
        return coupon;
    }

    @Override
    public void useCoupon(Long couponId, Long orderId) {
        CouponDO coupon = couponMapper().selectById(couponId);
        if (coupon == null) {
            throw new ServiceException(ErrorCodeConstants.COUPON_NOT_EXISTS);
        }
        if (coupon.getStatus() != null && coupon.getStatus() == 1) {
            return; // 已核销，幂等返回
        }
        if (coupon.getStatus() != null && coupon.getStatus() == 2) {
            throw new ServiceException(ErrorCodeConstants.COUPON_STATUS_INVALID);
        }
        // P2-E：read-then-updateById 在并发下会双核销（两线程同时读到 status=0 都 updateById 成功）。
        // 改 CAS 条件更新：eq(status=0) set(status=1)，rows==0 说明已被并发核销；
        // 再反查一次区分"已核销幂等"与"已失效不可核销"
        int rows = couponMapper().update(null, new LambdaUpdateWrapper<CouponDO>()
                .eq(CouponDO::getId, couponId)
                .eq(CouponDO::getStatus, 0)
                .set(CouponDO::getStatus, 1)
                .set(CouponDO::getUsedTime, LocalDateTime.now())
                .set(CouponDO::getUsedOrderId, orderId));
        if (rows == 0) {
            CouponDO refreshed = couponMapper().selectById(couponId);
            if (refreshed == null) {
                throw new ServiceException(ErrorCodeConstants.COUPON_NOT_EXISTS);
            }
            if (refreshed.getStatus() != null && refreshed.getStatus() == 1) {
                return; // 并发核销，幂等返回
            }
            throw new ServiceException(ErrorCodeConstants.COUPON_STATUS_INVALID);
        }
    }

    @Override
    public void releaseCoupon(Long couponId, Long orderId) {
        if (couponId == null || orderId == null) {
            return;
        }
        // P1-6：条件更新保证幂等——仅当券确实绑定该订单且已使用时才归还；
        // 并发重复归还 / 券未核销 / 绑定的是其他订单时，影响行数为 0，静默忽略
        couponMapper().update(null, new LambdaUpdateWrapper<CouponDO>()
                .eq(CouponDO::getId, couponId)
                .eq(CouponDO::getUsedOrderId, orderId)
                .eq(CouponDO::getStatus, 1)
                .set(CouponDO::getStatus, 0)
                .set(CouponDO::getUsedOrderId, null)
                .set(CouponDO::getUsedTime, null));
    }

    @Override
    public List<CouponVO.RespVO> getMyCoupons(Long userId, Integer status) {
        List<CouponDO> coupons = couponMapper().selectList(new LambdaQueryWrapperX<CouponDO>()
                .eq(CouponDO::getUserId, userId)
                .eqIfPresent(CouponDO::getStatus, status)
                .orderByAsc(CouponDO::getExpireTime));
        // P2-I：原实现循环内逐条 selectById 模板，N 张券产生 N+1 次查询。
        // 改批量预取：collect templateIds → selectBatchIds → Map 反查，O(1) per coupon
        List<Long> templateIds = new ArrayList<>(coupons.size());
        for (CouponDO c : coupons) {
            if (c.getTemplateId() != null && !templateIds.contains(c.getTemplateId())) {
                templateIds.add(c.getTemplateId());
            }
        }
        Map<Long, CouponTemplateDO> templateMap = templateIds.isEmpty()
                ? Collections.emptyMap()
                : couponTemplateMapper.selectBatchIds(templateIds).stream()
                .collect(Collectors.toMap(CouponTemplateDO::getId, t -> t, (a, b) -> a));
        List<CouponVO.RespVO> list = new ArrayList<>(coupons.size());
        for (CouponDO coupon : coupons) {
            CouponTemplateDO template = templateMap.get(coupon.getTemplateId());
            CouponVO.RespVO respVO = new CouponVO.RespVO();
            respVO.setId(coupon.getId());
            respVO.setTemplateId(coupon.getTemplateId());
            respVO.setName(template != null ? template.getName() : null);
            respVO.setType(template != null ? template.getType() : null);
            respVO.setThresholdAmount(template != null ? template.getThresholdAmount() : null);
            respVO.setDiscountValue(template != null ? template.getDiscountValue() : null);
            respVO.setCode(coupon.getCode());
            respVO.setStatus(coupon.getStatus());
            respVO.setExpireTime(coupon.getExpireTime());
            respVO.setUsedTime(coupon.getUsedTime());
            list.add(respVO);
        }
        return list;
    }

    // ===================== 私有辅助 =====================

    private List<CouponTemplateVO.RespVO> convertTemplate(List<CouponTemplateDO> list) {
        List<CouponTemplateVO.RespVO> result = new ArrayList<>(list.size());
        for (CouponTemplateDO t : list) {
            CouponTemplateVO.RespVO respVO = new CouponTemplateVO.RespVO();
            respVO.setId(t.getId());
            respVO.setName(t.getName());
            respVO.setType(t.getType());
            respVO.setThresholdAmount(t.getThresholdAmount());
            respVO.setDiscountValue(t.getDiscountValue());
            respVO.setTotal(t.getTotal());
            respVO.setTakenCount(t.getTakenCount());
            respVO.setPerLimit(t.getPerLimit());
            respVO.setValidDays(t.getValidDays());
            respVO.setStatus(t.getStatus());
            respVO.setCreateTime(t.getCreateTime());
            respVO.setUpdateTime(t.getUpdateTime());
            result.add(respVO);
        }
        return result;
    }

    private String genCode() {
        return "CP" + System.nanoTime() + String.format("%04d", RANDOM.nextInt(10000));
    }

    private CouponMapper couponMapper() {
        return getBaseMapper();
    }

}
