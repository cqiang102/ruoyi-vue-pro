package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberDO;

/**
 * 会员档案 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface MemberService {

    /**
     * 获取或创建会员档案（按 userId 单租户唯一）
     */
    MemberDO getOrCreateMember(Long userId);

    /**
     * 按会员编号获取档案，不存在抛异常
     */
    MemberDO getMemberRequired(Long id);

    /**
     * 按用户编号查询会员档案（可能为空）
     */
    MemberDO getMemberByUserId(Long userId);

    /**
     * 分页查询会员档案（含等级名称）
     */
    PageResult<MemberVO.RespVO> getMemberPage(MemberVO.PageReqVO pageReqVO);

    /**
     * 消费升级：订单完成后累加成长值、积分、累计消费，并按规则自动升级。
     * 积分余额使用 @Version 乐观锁（CAS）并发控制，复用芋道 pay 余额方案。
     *
     * @param memberId 会员编号，为空（散客）则直接忽略
     * @param payPrice 实付金额（单位：分）
     */
    void addConsume(Long memberId, Long payPrice);

    /**
     * 消费冲正：订单退款时扣回已发放的成长值 / 积分 / 累计消费（P1-7）。
     * 与 {@link #addConsume} 对称，各项指标向下钳制为 0（不出现负数），CAS 并发控制。
     * 仅对"已完成（发放过积分）"的订单退款时调用。
     *
     * @param memberId 会员编号，为空（散客）则直接忽略
     * @param payPrice 冲正金额（单位：分，正数）
     */
    void reduceConsume(Long memberId, Long payPrice);

    /**
     * 调整会员积分余额（可正可负），用于后台手动调整 / 积分抵现扣减。
     * 使用 @Version CAS 并发控制，扣减不足时抛异常。
     *
     * @param memberId 会员编号
     * @param delta    积分变动（正=加，负=减）
     */
    void adjustPoint(Long memberId, Integer delta);

}
