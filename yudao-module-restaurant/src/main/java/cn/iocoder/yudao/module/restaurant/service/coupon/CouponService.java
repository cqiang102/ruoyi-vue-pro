package cn.iocoder.yudao.module.restaurant.service.coupon;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.coupon.vo.CouponTemplateVO;
import cn.iocoder.yudao.module.restaurant.controller.app.coupon.vo.CouponVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.coupon.CouponDO;

import java.util.List;

/**
 * 优惠券 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface CouponService {

    // ===================== 模板管理（商户后台） =====================

    Long createTemplate(CouponTemplateVO.SaveReqVO reqVO);

    void updateTemplate(Long id, CouponTemplateVO.SaveReqVO reqVO);

    void deleteTemplate(Long id);

    PageResult<CouponTemplateVO.RespVO> getTemplatePage(CouponTemplateVO.PageReqVO pageReqVO);

    /**
     * 获得优惠券模板
     */
    CouponTemplateVO.RespVO getTemplate(Long id);

    // ===================== 用户券（消费者端） =====================

    /**
     * 领取优惠券（限量 / 每人限领校验，幂等重复领取抛异常）
     */
    CouponDO claimCoupon(Long userId, Long templateId);

    /**
     * 核销优惠券（幂等：已核销直接返回；门槛校验由调用方在订单侧完成）
     *
     * @param couponId  券实例编号
     * @param orderId   核销使用的订单编号
     */
    void useCoupon(Long couponId, Long orderId);

    /**
     * 归还优惠券（订单取消/退款时的逆向回滚，P1-6）。
     * 条件更新保证幂等：仅当券确实绑定该订单且状态为已使用时才归还，并发重复调用只生效一次。
     *
     * @param couponId 券实例编号
     * @param orderId  原核销订单编号
     */
    void releaseCoupon(Long couponId, Long orderId);

    /**
     * 查询我的优惠券
     *
     * @param userId 用户编号
     * @param status 状态：0-未使用 1-已使用 2-已过期；为空查全部
     */
    List<CouponVO.RespVO> getMyCoupons(Long userId, Integer status);

}
