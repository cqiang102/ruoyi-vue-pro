package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberCardVO;

import java.util.List;

/**
 * 会员卡 Service 接口（M-26：售卡/购买记录）
 *
 * @author 餐饮 SaaS
 */
public interface MemberCardService {

    // ========== 管理后台：卡商品 ==========

    /**
     * 创建会员卡
     *
     * @return 卡编号
     */
    Long createCard(MemberCardVO.SaveReqVO createReqVO);

    /**
     * 更新会员卡
     */
    void updateCard(MemberCardVO.SaveReqVO updateReqVO);

    /**
     * 删除会员卡
     */
    void deleteCard(Long id);

    /**
     * 会员卡分页
     */
    PageResult<MemberCardVO.RespVO> getCardPage(MemberCardVO.PageReqVO pageReqVO);

    // ========== 管理后台：购买记录 ==========

    /**
     * 购卡记录分页（客服排查）
     */
    PageResult<MemberCardVO.OrderRespVO> getOrderPage(MemberCardVO.OrderPageReqVO pageReqVO);

    // ========== 消费者端 ==========

    /**
     * 在售卡列表（status=1，按 sort 倒序）
     */
    List<MemberCardVO.RespVO> getOnSaleCards();

    /**
     * 余额购卡（事务内：扣余额 → CAS 累加已售数 → 插入已支付记录）
     *
     * @param userId 登录用户编号（登录态取）
     * @param cardId 卡编号
     * @return 购卡记录编号
     */
    Long buyCard(Long userId, Long cardId);

    /**
     * 我的购卡记录（默认地址同理：按 id 倒序）
     */
    PageResult<MemberCardVO.OrderRespVO> getMyRecords(Long userId, PageParam pageParam);

}
