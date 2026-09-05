package cn.iocoder.yudao.module.restaurant.service.member;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberCardVO;
import cn.iocoder.yudao.module.restaurant.convert.member.MemberCardConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberCardDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberCardOrderDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.MemberCardMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.MemberCardOrderMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.restaurant.service.pay.WalletPayService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 会员卡 Service 实现类（M-26）
 *
 * 余额购卡资金安全（与 MemberRecharge 同一水位）：
 * - 扣款走 WalletPayService.consume（芋道钱包：分布式锁 + SQL 级 CAS）；
 * - 已售数累加用 CAS：eq(soldCount, 旧值).set(soldCount, 旧值+1)，失败即抛异常回滚；
 * - 扣款、累加、插入记录在同一事务，钱、卡、记录同生共死。
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class MemberCardServiceImpl implements MemberCardService {

    /**
     * 购卡单号前缀（后续接微信支付回调时按此路由）
     */
    private static final String ORDER_PREFIX = "MCD-";

    /**
     * 会员端 userType（与 MemberRecharge / 前端 getWallet(2) 一致）
     */
    private static final Integer USER_TYPE_MEMBER = 2;

    @Resource
    private MemberCardMapper memberCardMapper;
    @Resource
    private MemberCardOrderMapper memberCardOrderMapper;
    @Resource
    private WalletPayService walletPayService;

    // ========== 管理后台：卡商品 ==========

    @Override
    public Long createCard(MemberCardVO.SaveReqVO createReqVO) {
        MemberCardDO card = MemberCardConvert.convert(createReqVO);
        card.setId(null); // 防止前端回传 id 造成误更新
        if (card.getSoldCount() == null) {
            card.setSoldCount(0);
        }
        memberCardMapper.insert(card);
        return card.getId();
    }

    @Override
    public void updateCard(MemberCardVO.SaveReqVO updateReqVO) {
        validateCardExists(updateReqVO.getId());
        MemberCardDO updateObj = MemberCardConvert.convert(updateReqVO);
        memberCardMapper.updateById(updateObj);
    }

    @Override
    public void deleteCard(Long id) {
        validateCardExists(id);
        memberCardMapper.deleteById(id);
    }

    @Override
    public PageResult<MemberCardVO.RespVO> getCardPage(MemberCardVO.PageReqVO pageReqVO) {
        PageResult<MemberCardDO> page = memberCardMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<MemberCardDO>()
                        .likeIfPresent(MemberCardDO::getName, pageReqVO.getName())
                        .eqIfPresent(MemberCardDO::getStatus, pageReqVO.getStatus())
                        .orderByDesc(MemberCardDO::getSort));
        return new PageResult<>(MemberCardConvert.convertList(page.getList()), page.getTotal());
    }

    // ========== 管理后台：购买记录 ==========

    @Override
    public PageResult<MemberCardVO.OrderRespVO> getOrderPage(MemberCardVO.OrderPageReqVO pageReqVO) {
        PageResult<MemberCardOrderDO> page = memberCardOrderMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<MemberCardOrderDO>()
                        .eqIfPresent(MemberCardOrderDO::getUserId, pageReqVO.getUserId())
                        .eqIfPresent(MemberCardOrderDO::getCardId, pageReqVO.getCardId())
                        .orderByDesc(MemberCardOrderDO::getId));
        return new PageResult<>(MemberCardConvert.convertOrderList(page.getList()), page.getTotal());
    }

    // ========== 消费者端 ==========

    @Override
    public List<MemberCardVO.RespVO> getOnSaleCards() {
        List<MemberCardDO> list = memberCardMapper.selectList(
                new LambdaQueryWrapperX<MemberCardDO>()
                        .eq(MemberCardDO::getStatus, 1)
                        .orderByDesc(MemberCardDO::getSort));
        return MemberCardConvert.convertList(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long buyCard(Long userId, Long cardId) {
        MemberCardDO card = validateCardExists(cardId);
        if (!Integer.valueOf(1).equals(card.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_CARD_NOT_ON_SALE);
        }
        long price = card.getPrice() == null ? 0L : card.getPrice();
        if (price <= 0) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_CARD_NOT_ON_SALE);
        }

        // ① 余额扣款（芋道钱包保证并发安全；余额不足时其内部抛对应异常）
        String orderNo = ORDER_PREFIX + IdUtil.fastSimpleUUID();
        walletPayService.consume(userId, USER_TYPE_MEMBER, orderNo, (int) price);

        // ② CAS 累加已售数：并发购买只允许一方成功，失败则整个事务回滚（含扣款）
        int rows = memberCardMapper.update(null, new LambdaUpdateWrapper<MemberCardDO>()
                .eq(MemberCardDO::getId, cardId)
                .eq(MemberCardDO::getSoldCount, card.getSoldCount() == null ? 0 : card.getSoldCount())
                .set(MemberCardDO::getSoldCount, (card.getSoldCount() == null ? 0 : card.getSoldCount()) + 1));
        if (rows == 0) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_CARD_BUY_CONFLICT);
        }

        // ③ 插入已支付购买记录
        MemberCardOrderDO order = new MemberCardOrderDO()
                .setOrderNo(orderNo)
                .setUserId(userId)
                .setCardId(cardId)
                .setCardName(card.getName())
                .setPrice(price)
                .setPayType(2)
                .setStatus(1)
                .setPaidTime(LocalDateTime.now());
        memberCardOrderMapper.insert(order);
        return order.getId();
    }

    @Override
    public PageResult<MemberCardVO.OrderRespVO> getMyRecords(Long userId, PageParam pageParam) {
        PageResult<MemberCardOrderDO> page = memberCardOrderMapper.selectPageByUser(userId, pageParam);
        return new PageResult<>(MemberCardConvert.convertOrderList(page.getList()), page.getTotal());
    }

    // ========== 辅助 ==========

    private MemberCardDO validateCardExists(Long id) {
        if (id == null) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_CARD_NOT_EXISTS);
        }
        MemberCardDO card = memberCardMapper.selectById(id);
        if (card == null) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_CARD_NOT_EXISTS);
        }
        return card;
    }

}
