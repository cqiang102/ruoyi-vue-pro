package cn.iocoder.yudao.module.restaurant.service.order;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.order.vo.OrderVO;
import cn.iocoder.yudao.module.restaurant.convert.order.OrderConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishAddonDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dish.DishSpecDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderItemDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.TableDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dish.DishAddonMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dish.DishMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dish.DishSpecMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.order.OrderItemMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.order.OrderMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.StoreMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.TableMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import cn.iocoder.yudao.module.restaurant.enums.order.OrderStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.order.OrderTypeEnum;
import cn.iocoder.yudao.module.restaurant.service.member.MemberService;
import cn.iocoder.yudao.module.pay.api.refund.dto.PayRefundRespDTO;
import cn.iocoder.yudao.module.restaurant.service.pay.OrderPayService;
import cn.iocoder.yudao.module.restaurant.service.pay.WalletPayService;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.coupon.CouponDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.coupon.CouponTemplateDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.coupon.CouponMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.coupon.CouponTemplateMapper;
import cn.iocoder.yudao.module.restaurant.service.coupon.CouponService;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 订单 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
@Slf4j
public class OrderServiceImpl implements OrderService {

    /**
     * 核销码有效期（天）：超过该时长的历史订单核销码视为失效，防止 6 位码空间被长期枚举
     */
    private static final int VERIFY_CODE_VALID_DAYS = 7;

    /**
     * 核销码撞 DB 唯一索引时的最大重试次数
     */
    private static final int VERIFY_CODE_RETRY_TIMES = 3;

    /**
     * 支付渠道金额上限（分）：100 万元。
     * 芋道 pay 接口入参为 int，超过此值 int 转换会静默溢出导致金额错乱（P1-10），必须提前拦截
     */
    private static final long MAX_PAY_PRICE = 100_000_000L;

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishSpecMapper dishSpecMapper;
    @Resource
    private DishAddonMapper dishAddonMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private TableMapper tableMapper;
    @Resource
    private OrderPayService orderPayService;
    @Resource
    private WalletPayService walletPayService;
    @Resource
    private MemberService memberService;
    @Resource
    private CouponService couponService;
    @Resource
    private CouponMapper couponMapper;
    @Resource
    private CouponTemplateMapper couponTemplateMapper;
    @Resource
    private PlatformTransactionManager transactionManager;
    @Resource
    private cn.iocoder.yudao.module.restaurant.service.print.PrintService printService;

    /**
     * 编程式事务模板——用于「方法整体无需事务、但其中某段子流程需要原子性」的场景
     * （如余额退款：CAS 占位 + 钱包退款 + 状态落地 + 券/积分回滚 必须原子，但前置校验无需事务）
     */
    private TransactionTemplate transactionTemplate;

    @PostConstruct
    private void initTransactionTemplate() {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderVO.CreateReqVO createReqVO) {
        StoreDO store = storeMapper.selectById(createReqVO.getStoreId());
        if (store == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_NOT_EXISTS);
        }
        // P1-2：门店行排他锁——串行化同店并发下单，保证下方「取餐号 = 当日单数+1」不重号。
        // 锁在事务内持有直至提交，后到者在锁上排队，计数时必能看到前者已提交的订单
        storeMapper.selectIdForUpdate(createReqVO.getStoreId());
        if (OrderTypeEnum.DINE_IN.getType().equals(createReqVO.getType())
                && createReqVO.getTableId() == null) {
            throw new ServiceException(ErrorCodeConstants.ORDER_TABLE_REQUIRED);
        }
        // 外卖：必须填写收货信息；配送费取门店配置，计入总价
        long deliveryFee = 0L;
        if (OrderTypeEnum.DELIVERY.getType().equals(createReqVO.getType())) {
            if (StrUtil.isBlank(createReqVO.getReceiverName())
                    || StrUtil.isBlank(createReqVO.getReceiverPhone())
                    || StrUtil.isBlank(createReqVO.getReceiverAddress())) {
                throw new ServiceException(ErrorCodeConstants.ORDER_DELIVERY_ADDRESS_REQUIRED);
            }
            deliveryFee = store.getDeliveryFee() == null ? 0L : store.getDeliveryFee();
        }
        List<OrderItemDO> items = buildItems(0L, createReqVO.getItems());
        long goodsTotal = sumTotal(items);
        // 外卖起送价校验（按商品金额，不含配送费）
        if (OrderTypeEnum.DELIVERY.getType().equals(createReqVO.getType())
                && store.getMinOrderAmount() != null && store.getMinOrderAmount() > 0
                && goodsTotal < store.getMinOrderAmount()) {
            throw new ServiceException(ErrorCodeConstants.ORDER_DELIVERY_MIN_AMOUNT);
        }

        // P2-3：优惠券门槛/折扣按「商品金额」计算，不含配送费（配送费不参与满减语义）
        long discount = computeCouponDiscount(createReqVO.getCouponId(), createReqVO.getUserId(), goodsTotal);
        long total = goodsTotal + deliveryFee;

        // 会员档案派生：app 端下单（controller 已把 userId 强制覆盖为登录用户）自动关联会员
        Long memberId = createReqVO.getMemberId();
        if (memberId == null && createReqVO.getUserId() != null) {
            memberId = memberService.getOrCreateMember(createReqVO.getUserId()).getId();
        }

        OrderDO order = new OrderDO()
                .setStoreId(createReqVO.getStoreId())
                .setTableId(createReqVO.getTableId())
                .setOrderNo(generateOrderNo())
                .setType(createReqVO.getType())
                .setStatus(OrderStatusEnum.UNPAID.getStatus())
                .setTotalPrice(total)
                .setDiscountPrice(discount)
                .setPayPrice(Math.max(0L, total - discount))
                .setPayType(0)
                .setPayStatus(0)
                .setUserId(createReqVO.getUserId())
                .setMemberId(memberId)
                .setCouponId(createReqVO.getCouponId())
                .setPeopleCount(createReqVO.getPeopleCount())
                .setRemark(createReqVO.getRemark())
                .setDeliveryFee(deliveryFee)
                .setReceiverName(createReqVO.getReceiverName())
                .setReceiverPhone(createReqVO.getReceiverPhone())
                .setReceiverAddress(createReqVO.getReceiverAddress())
                .setPickupNo(generatePickupNo(createReqVO.getStoreId()))
                .setVerifyCode(generateVerifyCode());
        // P1-3：核销码唯一性由 DB 唯一索引 uk_verify_code 兜底——
        // 「先查后插」在并发下仍可能撞码，撞唯一键时换码重插（最多 3 次）
        insertOrderWithVerifyCodeRetry(order);
        Long orderId = order.getId();
        for (OrderItemDO item : items) {
            item.setOrderId(orderId);
        }
        orderItemMapper.insertBatch(items);
        // 核销优惠券（绑定订单，置为已使用）
        if (createReqVO.getCouponId() != null) {
            couponService.useCoupon(createReqVO.getCouponId(), orderId);
        }
        // 堂食落座：P1-4——前置校验（存在 / 归属门店 / 空闲）+ CAS 占用，防止并发双占
        if (OrderTypeEnum.DINE_IN.getType().equals(createReqVO.getType())
                && createReqVO.getTableId() != null) {
            TableDO table = tableMapper.selectById(createReqVO.getTableId());
            if (table == null) {
                throw new ServiceException(ErrorCodeConstants.ORDER_TABLE_NOT_EXISTS);
            }
            if (!Objects.equals(table.getStoreId(), createReqVO.getStoreId())) {
                throw new ServiceException(ErrorCodeConstants.ORDER_TABLE_STORE_MISMATCH);
            }
            if (table.getStatus() != null && table.getStatus() != 0) {
                throw new ServiceException(ErrorCodeConstants.ORDER_TABLE_OCCUPIED);
            }
            int rows = tableMapper.update(null, new LambdaUpdateWrapper<TableDO>()
                    .eq(TableDO::getId, createReqVO.getTableId())
                    .eq(TableDO::getStatus, 0)
                    .set(TableDO::getStatus, 1));
            if (rows == 0) {
                throw new ServiceException(ErrorCodeConstants.ORDER_TABLE_OCCUPIED);
            }
        }
        return orderId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrderItems(Long orderId, List<OrderVO.ItemCreateVO> items) {
        OrderDO order = validateOrderExists(orderId);
        // 仅待支付订单可加菜：payPrice/discount 随 totalPrice 同步重算，
        // 若允许已支付订单加菜，将出现"加菜不付钱"（P0-6 资损口径：先加菜、后支付）
        if (!OrderStatusEnum.UNPAID.getStatus().equals(order.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_ADD_ITEMS_STATUS_INVALID);
        }
        List<OrderItemDO> newItems = buildItems(orderId, items);
        long added = sumTotal(newItems);
        orderItemMapper.insertBatch(newItems);
        long newTotal = order.getTotalPrice() + added;
        // 已核销的优惠券按模板重算优惠额（跳过"已使用"状态校验），保证 payPrice = total - discount 恒成立
        long discount = recalcCouponDiscount(order.getCouponId(), newTotal);
        order.setTotalPrice(newTotal)
                .setDiscountPrice(discount)
                .setPayPrice(Math.max(0L, newTotal - discount));
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        OrderDO order = validateOrderExists(orderId);
        if (!OrderStatusEnum.UNPAID.getStatus().equals(order.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_STATUS_INVALID);
        }
        order.setStatus(OrderStatusEnum.CANCELED.getStatus());
        orderMapper.updateById(order);
        // 释放堂食桌台（条件更新：仅占用态才释放，幂等）
        if (OrderTypeEnum.DINE_IN.getType().equals(order.getType()) && order.getTableId() != null) {
            tableMapper.update(null, new LambdaUpdateWrapper<TableDO>()
                    .eq(TableDO::getId, order.getTableId())
                    .eq(TableDO::getStatus, 1)
                    .set(TableDO::getStatus, 0));
        }
        // P1-6：取消订单归还已核销的优惠券（下单时即核销，取消必须归还，否则用户白白损失一张券）
        rollbackOrderBenefits(order);
    }

    @Override
    public void acceptOrder(Long orderId) {
        OrderDO order = validateOrderExists(orderId);
        if (!OrderStatusEnum.PAID.getStatus().equals(order.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_STATUS_INVALID);
        }
        order.setStatus(OrderStatusEnum.COOKING.getStatus());
        orderMapper.updateById(order);
        // M-10：接单成功触发云打印（旁路功能，内部全量 try-catch，失败不影响接单）
        printService.printOrder(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId) {
        OrderDO order = validateOrderExists(orderId);
        if (!OrderStatusEnum.COOKING.getStatus().equals(order.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_STATUS_INVALID);
        }
        doComplete(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyOrder(String verifyCode, Long storeId) {
        if (StrUtil.isBlank(verifyCode)) {
            throw new ServiceException(ErrorCodeConstants.ORDER_VERIFY_CODE_NOT_FOUND);
        }
        // P2-4：统一转大写归一（前端展示为大写，此处与 DB 存储口径对齐，避免大小写不一致核销失败）
        OrderDO order = orderMapper.selectByVerifyCode(verifyCode.trim().toUpperCase());
        if (order == null) {
            throw new ServiceException(ErrorCodeConstants.ORDER_VERIFY_CODE_NOT_FOUND);
        }
        if (storeId != null && !storeId.equals(order.getStoreId())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_VERIFY_CODE_NOT_FOUND);
        }
        // P1-9：核销码有效期——超过 N 天的历史订单核销码视为失效，防止 6 位码空间被长期枚举
        if (order.getCreateTime() == null
                || order.getCreateTime().isBefore(LocalDateTime.now().minusDays(VERIFY_CODE_VALID_DAYS))) {
            throw new ServiceException(ErrorCodeConstants.ORDER_VERIFY_CODE_NOT_FOUND);
        }
        Integer status = order.getStatus();
        // 已核销（已完成/已退款）视为幂等成功，避免重复操作
        if (OrderStatusEnum.COMPLETED.getStatus().equals(status)
                || OrderStatusEnum.REFUNDED.getStatus().equals(status)) {
            return;
        }
        if (!OrderStatusEnum.PAID.getStatus().equals(status)
                && !OrderStatusEnum.COOKING.getStatus().equals(status)) {
            throw new ServiceException(ErrorCodeConstants.ORDER_VERIFY_STATUS_INVALID);
        }
        doComplete(order);
    }

    @Override
    public void callOrder(Long orderId) {
        OrderDO order = validateOrderExists(orderId);
        Integer status = order.getStatus();
        if (!OrderStatusEnum.PAID.getStatus().equals(status)
                && !OrderStatusEnum.COOKING.getStatus().equals(status)) {
            throw new ServiceException(ErrorCodeConstants.ORDER_STATUS_INVALID);
        }
        order.setCalledTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public Long payByWeixin(Long orderId, String appKey, String userIp, Long userId, Integer userType) {
        OrderDO order = validateOrderExists(orderId);
        if (!OrderStatusEnum.UNPAID.getStatus().equals(order.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_UNPAID);
        }
        // P1-B：远程发起微信支付单（不在事务内，避免长事务 + 远程调用）
        Long payOrderId = orderPayService.createWeixinPayOrder(appKey, userIp, userId, userType,
                order.getOrderNo(), "餐饮订单-" + order.getOrderNo(), "餐饮订单",
                toIntPrice(order.getPayPrice()), LocalDateTime.now().plusMinutes(30));
        // P1-B：CAS 条件更新——仅当订单仍为待支付时才写 payOrderId，并发重复发起只成功一次。
        // 失败说明已被并发请求占用，抛异常让前端用已返回的 payOrderId（或提示重复发起），
        // 避免第二次覆盖 order.payOrderId 导致"前端拿旧 payOrderId 付款、回调 payOrderId 不匹配订单卡死"
        int rows = orderMapper.update(null, new LambdaUpdateWrapper<OrderDO>()
                .eq(OrderDO::getId, orderId)
                .eq(OrderDO::getStatus, OrderStatusEnum.UNPAID.getStatus())
                .set(OrderDO::getPayOrderId, payOrderId)
                .set(OrderDO::getPayType, 1)
                .set(OrderDO::getAppKey, appKey));
        if (rows == 0) {
            log.warn("[payByWeixin][订单({})已被并发发起支付，当前 payOrderId({}) 为孤儿单（将随支付单过期自动失效）]",
                    orderId, payOrderId);
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_UNPAID);
        }
        return payOrderId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payByBalance(Long orderId, Long userId) {
        OrderDO order = validateOrderExists(orderId);
        if (!OrderStatusEnum.UNPAID.getStatus().equals(order.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_UNPAID);
        }
        if (userId == null) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_OWNER);
        }
        // 钱包按 userId 定位（钱包体系挂在会员用户维度，不是 memberId）
        walletPayService.consume(userId, UserTypeEnum.MEMBER.getValue(),
                order.getOrderNo(), toIntPrice(order.getPayPrice()));
        // CAS 条件更新：仅当订单仍为待支付时生效，并发重复请求只成功一次；
        // 失败则抛异常回滚整个事务（含上面的钱包扣减），保证钱、单同生共死
        int rows = orderMapper.update(null, new LambdaUpdateWrapper<OrderDO>()
                .eq(OrderDO::getId, orderId)
                .eq(OrderDO::getStatus, OrderStatusEnum.UNPAID.getStatus())
                .set(OrderDO::getStatus, OrderStatusEnum.PAID.getStatus())
                .set(OrderDO::getPayStatus, 1)
                .set(OrderDO::getPayType, 2)
                .set(OrderDO::getPaidTime, LocalDateTime.now()));
        if (rows == 0) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_UNPAID);
        }
        // 顺带回填会员档案关联（散客订单首次用余额支付时）
        if (order.getMemberId() == null) {
            orderMapper.update(null, new LambdaUpdateWrapper<OrderDO>()
                    .eq(OrderDO::getId, orderId)
                    .set(OrderDO::getMemberId, memberService.getOrCreateMember(userId).getId()));
        }
    }

    /**
     * 收银台 M-04：现金收讫。与 payByBalance 同款 CAS 幂等，仅状态流转不涉资金。
     */
    @Override
    public void payByCash(Long orderId) {
        OrderDO order = validateOrderExists(orderId);
        if (!OrderStatusEnum.UNPAID.getStatus().equals(order.getStatus())) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_UNPAID);
        }
        // CAS 条件更新：仅当订单仍为待支付时生效，并发重复收银只成功一次
        int rows = orderMapper.update(null, new LambdaUpdateWrapper<OrderDO>()
                .eq(OrderDO::getId, orderId)
                .eq(OrderDO::getStatus, OrderStatusEnum.UNPAID.getStatus())
                .set(OrderDO::getStatus, OrderStatusEnum.PAID.getStatus())
                .set(OrderDO::getPayStatus, 1)
                .set(OrderDO::getPayType, 4)
                .set(OrderDO::getPaidTime, LocalDateTime.now()));
        if (rows == 0) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_UNPAID);
        }
    }

    @Override
    public void refundOrder(Long orderId, String reason) {
        // P1-D：去掉方法级 @Transactional。原实现在 @Transactional 内调用 orderPayService.createRefund
        // （同步远程 HTTP 微信退款），导致 DB 事务/连接在远程调用期间持续占用，连接池耗尽风险。
        // 改造：微信分支远程调用移出事务，落地用 CAS 单条 update；余额分支用编程式事务
        // 仅包裹「CAS 占位 + 钱包退款 + 状态落地 + 券/积分回滚」保证原子性。
        OrderDO order = validateOrderExists(orderId);
        // 1. 状态校验：仅 已支付 / 制作中 / 已完成 可退
        Integer status = order.getStatus();
        if (!(OrderStatusEnum.PAID.getStatus().equals(status)
                || OrderStatusEnum.COOKING.getStatus().equals(status)
                || OrderStatusEnum.COMPLETED.getStatus().equals(status))) {
            throw new ServiceException(ErrorCodeConstants.ORDER_REFUND_STATUS_INVALID);
        }
        // 2. 支付状态校验：必须已付，且未处于退款中 / 已退
        if (order.getPayStatus() == null || order.getPayStatus() != 1) {
            throw new ServiceException(ErrorCodeConstants.ORDER_REFUND_STATUS_INVALID);
        }
        Long payPrice = order.getPayPrice() != null ? order.getPayPrice() : 0L;
        if (payPrice <= 0) {
            throw new ServiceException(ErrorCodeConstants.ORDER_REFUND_STATUS_INVALID);
        }
        String refundReason = (reason == null || reason.isEmpty()) ? "用户申请退款" : reason;
        // 3. 区分支付渠道
        if (order.getPayOrderId() != null) {
            // 微信支付：走芋道 Pay 退款（原路退回），成功后由回调 onRefundSuccess 置为已退款
            if (order.getAppKey() == null || order.getAppKey().isEmpty()) {
                throw new ServiceException(ErrorCodeConstants.ORDER_REFUND_APPKEY_MISSING);
            }
            // P2-5：退款商户单号固定为 "RR"+订单号——同一订单重复发起退款时复用同一单号，
            // 支付渠道侧天然幂等，避免重试生成新单号导致重复退款
            String merchantRefundId = "RR" + order.getOrderNo();
            // P1-D：远程退款调用移出事务，避免长事务占用 DB 连接；
            // 退款商户单号幂等，重复调用支付渠道会返回同一退款单，不会重复退款
            orderPayService.createRefund(order.getAppKey(), "127.0.0.1",
                    order.getMemberId(), UserTypeEnum.MEMBER.getValue(),
                    order.getOrderNo(), merchantRefundId, refundReason, toIntPrice(payPrice));
            // P1-D：CAS 条件更新——eq(payStatus=1)，rows==0 说明已被并发线程置为退款中/已退，
            // 此时远程退款是幂等的（同一 merchantRefundId），无需重复落地，直接幂等返回
            int rows = orderMapper.update(null, new LambdaUpdateWrapper<OrderDO>()
                    .eq(OrderDO::getId, orderId)
                    .eq(OrderDO::getPayStatus, 1)
                    .set(OrderDO::getStatus, OrderStatusEnum.REFUNDING.getStatus())
                    .set(OrderDO::getPayStatus, 2)
                    .set(OrderDO::getRefundPrice, payPrice));
            if (rows == 0) {
                log.warn("[refundOrder][订单({}) 微信退款已被并发发起，CAS 未命中，退款单号={}]",
                        orderId, merchantRefundId);
            }
            // 同步内存实例，便于后续日志/回调使用
            order.setStatus(OrderStatusEnum.REFUNDING.getStatus())
                    .setPayStatus(2)
                    .setRefundPrice(payPrice);
        } else {
            // 余额支付：直接回滚会员钱包（同步完成，无异步回调）
            // 钱包按 userId 定位（与 payByBalance 的 consume 口径一致；memberId 是档案编号，不是钱包键）
            if (order.getUserId() == null) {
                throw new ServiceException(ErrorCodeConstants.ORDER_REFUND_STATUS_INVALID);
            }
            // P1-D：余额分支用编程式事务保证「CAS 占位 + 钱包退款 + 状态落地 + 券/积分回滚」原子性。
            // CAS 占位 eq(payStatus=1) 在事务内最先执行，rows==0 直接抛异常回滚——
            // 钱包 refund 因此不会被并发线程重复触发，杜绝双倍退款。
            final Long fpayPrice = payPrice;
            transactionTemplate.execute(ts -> {
                int rows = orderMapper.update(null, new LambdaUpdateWrapper<OrderDO>()
                        .eq(OrderDO::getId, orderId)
                        .eq(OrderDO::getPayStatus, 1)
                        .set(OrderDO::getStatus, OrderStatusEnum.REFUNDED.getStatus())
                        .set(OrderDO::getPayStatus, 3)
                        .set(OrderDO::getRefundPrice, fpayPrice)
                        .set(OrderDO::getRefundTime, LocalDateTime.now()));
                if (rows == 0) {
                    log.warn("[refundOrder][订单({}) 余额退款已被并发处理，CAS 未命中]", orderId);
                    throw new ServiceException(ErrorCodeConstants.ORDER_REFUND_STATUS_INVALID);
                }
                walletPayService.refund(order.getUserId(), UserTypeEnum.MEMBER.getValue(),
                        order.getOrderNo(), toIntPrice(fpayPrice));
                // 同步内存实例字段，rollbackOrderBenefits 读取的 couponId/finishTime/memberId/payPrice
                // 均为不可变字段，无需额外同步；此处同步仅为保持实例与 DB 一致
                order.setStatus(OrderStatusEnum.REFUNDED.getStatus())
                        .setPayStatus(3)
                        .setRefundPrice(fpayPrice)
                        .setRefundTime(LocalDateTime.now());
                // P1-6/P1-7：余额退款同步完成，立即逆向回滚（归还优惠券 + 冲正已完成订单的消费积分）
                rollbackOrderBenefits(order);
                return null;
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onPaySuccess(String merchantOrderId, Long payOrderId) {
        // 1. 按 orderNo 查订单；不存在时抛异常（而非静默返回），让 pay 模块标记通知失败并重试。
        //    否则会出现"用户已付款、订单永远停在待支付、且不再有重试机会"的资损事故（P0-2）
        // P1-8：回调链路无登录态、无 tenant-id 请求头——先忽略租户定位订单，
        //    再切回订单所属租户执行更新；不依赖"无租户上下文时全表扫描"的隐式行为
        OrderDO order = TenantUtils.executeIgnore(() ->
                orderMapper.selectOne(OrderDO::getOrderNo, merchantOrderId));
        if (order == null) {
            log.error("[onPaySuccess][回调订单不存在 orderNo({}) payOrderId({})，抛异常触发 pay 模块重试]",
                    merchantOrderId, payOrderId);
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_EXISTS);
        }
        // 2. 支付单号必须一致，防止串单（重复回调时 payOrderId 相同，放行）；
        //    不匹配属于数据异常，必须暴露出来触发重试 + 告警，不能静默吞掉
        if (order.getPayOrderId() != null && !order.getPayOrderId().equals(payOrderId)) {
            log.error("[onPaySuccess][支付单号不匹配 orderNo({}) 本地({}) 回调({})]",
                    merchantOrderId, order.getPayOrderId(), payOrderId);
            throw new ServiceException(ErrorCodeConstants.ORDER_PAY_ORDER_MISMATCH);
        }
        // 3. 仅待支付订单可处理，已支付则幂等返回
        if (!OrderStatusEnum.UNPAID.getStatus().equals(order.getStatus())) {
            return;
        }
        // 4. 置为已支付（在订单所属租户上下文中执行）
        // P2-K：移除 .setPayPrice(order.getPayPrice()) 自赋值死代码——
        // payPrice 在下单时已固定，支付回调无需重置
        order.setStatus(OrderStatusEnum.PAID.getStatus())
                .setPayStatus(1)
                .setPayOrderId(payOrderId)
                .setPaidTime(LocalDateTime.now());
        TenantUtils.execute(order.getTenantId(), () -> orderMapper.updateById(order));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onRefundSuccess(String merchantOrderId, String merchantRefundId, Long payRefundId) {
        // P1-8：与 onPaySuccess 同理，回调无租户上下文，先忽略租户定位再切换
        OrderDO order = TenantUtils.executeIgnore(() ->
                orderMapper.selectOne(OrderDO::getOrderNo, merchantOrderId));
        if (order == null) {
            log.error("[onRefundSuccess][回调订单不存在 orderNo({})，抛异常触发 pay 模块重试]", merchantOrderId);
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_EXISTS);
        }
        // 幂等：已退则直接返回
        if (order.getPayStatus() != null && order.getPayStatus() == 3) {
            return;
        }
        // P2-6：退款单查询失败时容错降级——使用订单本地记录的退款金额，不让单次查询失败卡死整个回调
        Long refundAmount = order.getRefundPrice();
        try {
            PayRefundRespDTO refund = orderPayService.getRefund(payRefundId);
            if (refund != null && refund.getRefundPrice() != null) {
                refundAmount = refund.getRefundPrice().longValue();
            }
        } catch (Exception e) {
            log.warn("[onRefundSuccess][查询退款单失败 payRefundId({})，降级使用订单本地退款金额({})]",
                    payRefundId, refundAmount, e);
        }
        order.setPayStatus(3)
                .setStatus(OrderStatusEnum.REFUNDED.getStatus())
                .setRefundTime(LocalDateTime.now())
                .setRefundPrice(refundAmount != null ? refundAmount : 0L);
        // P1-8：更新与逆向回滚都必须在订单所属租户上下文中执行。
        // 回调链路无租户上下文，若不显式切换，券归还/积分冲正的 SQL 会带上 tenant_id=null 而静默失效
        TenantUtils.execute(order.getTenantId(), () -> {
            orderMapper.updateById(order);
            // P1-6/P1-7：微信退款落地，逆向回滚（归还优惠券 + 冲正已完成订单的消费积分）
            rollbackOrderBenefits(order);
        });
    }

    @Override
    public OrderVO.RespVO getOrder(Long id) {
        OrderDO order = orderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_EXISTS);
        }
        OrderVO.RespVO respVO = OrderConvert.convert(order);
        respVO.setItems(OrderConvert.convertItemList(
                orderItemMapper.selectList(OrderItemDO::getOrderId, id)));
        return respVO;
    }

    @Override
    public void validateOrderOwner(Long orderId, Long userId) {
        // 消费者端防水平越权：订单必须归属当前登录用户（散客/他人订单一律视为不存在）
        if (userId == null) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_OWNER);
        }
        OrderDO order = orderMapper.selectById(orderId);
        if (order == null || !Objects.equals(order.getUserId(), userId)) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_OWNER);
        }
    }

    @Override
    public PageResult<OrderVO.RespVO> getOrderPage(OrderVO.PageReqVO pageReqVO) {
        PageResult<OrderDO> page = orderMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<OrderDO>()
                        .eqIfPresent(OrderDO::getStoreId, pageReqVO.getStoreId())
                        .likeIfPresent(OrderDO::getOrderNo, pageReqVO.getOrderNo())
                        .eqIfPresent(OrderDO::getType, pageReqVO.getType())
                        .eqIfPresent(OrderDO::getStatus, pageReqVO.getStatus())
                        // 消费者端"我的订单"：controller 会把 userId 强制注入为登录用户
                        .eqIfPresent(OrderDO::getUserId, pageReqVO.getUserId())
                        .orderByDesc(OrderDO::getId));
        // P2-1：一次性批量查明细再内存分组，消除每单一查的 N+1
        List<Long> orderIds = convertList(page.getList(), OrderDO::getId);
        Map<Long, List<OrderItemDO>> itemsMap = orderIds.isEmpty() ? Collections.emptyMap()
                : orderItemMapper.selectList(new LambdaQueryWrapperX<OrderItemDO>()
                        .in(OrderItemDO::getOrderId, orderIds))
                        .stream().collect(Collectors.groupingBy(OrderItemDO::getOrderId));
        List<OrderVO.RespVO> list = new ArrayList<>(page.getList().size());
        for (OrderDO order : page.getList()) {
            OrderVO.RespVO respVO = OrderConvert.convert(order);
            respVO.setItems(OrderConvert.convertItemList(
                    itemsMap.getOrDefault(order.getId(), Collections.emptyList())));
            list.add(respVO);
        }
        return new PageResult<>(list, page.getTotal());
    }

    // ========== 辅助 ==========

    private List<OrderItemDO> buildItems(Long orderId, List<OrderVO.ItemCreateVO> reqItems) {
        List<OrderItemDO> items = new ArrayList<>();
        if (reqItems == null || reqItems.isEmpty()) {
            return items;
        }
        // P2-2：菜品/规格/加料批量预取，消除循环内逐条 selectById 的 N+1
        Set<Long> dishIds = convertList(reqItems, OrderVO.ItemCreateVO::getDishId).stream()
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> specIds = reqItems.stream().map(OrderVO.ItemCreateVO::getSpecId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> addonIds = reqItems.stream()
                .filter(it -> it.getAddonIds() != null)
                .flatMap(it -> it.getAddonIds().stream())
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, DishDO> dishMap = dishIds.isEmpty() ? Collections.emptyMap()
                : dishMapper.selectBatchIds(dishIds).stream()
                        .collect(Collectors.toMap(DishDO::getId, Function.identity(), (a, b) -> a));
        Map<Long, DishSpecDO> specMap = specIds.isEmpty() ? Collections.emptyMap()
                : dishSpecMapper.selectBatchIds(specIds).stream()
                        .collect(Collectors.toMap(DishSpecDO::getId, Function.identity(), (a, b) -> a));
        Map<Long, DishAddonDO> addonMap = addonIds.isEmpty() ? Collections.emptyMap()
                : dishAddonMapper.selectBatchIds(addonIds).stream()
                        .collect(Collectors.toMap(DishAddonDO::getId, Function.identity(), (a, b) -> a));
        for (OrderVO.ItemCreateVO it : reqItems) {
            DishDO dish = dishMap.get(it.getDishId());
            if (dish == null) {
                throw new ServiceException(ErrorCodeConstants.ORDER_ITEM_DISH_NOT_EXISTS);
            }
            // P1-5：下架 / 沽清菜品禁止下单（此前只判存在性，导致下架菜仍可被下单）
            if (dish.getStatus() == null || dish.getStatus() != 1) {
                throw new ServiceException(ErrorCodeConstants.ORDER_ITEM_DISH_OFF_SHELF);
            }
            if (dish.getSoldOut() != null && dish.getSoldOut() == 1) {
                throw new ServiceException(ErrorCodeConstants.ORDER_ITEM_DISH_SOLD_OUT);
            }
            long unitPrice = dish.getPrice() == null ? 0 : dish.getPrice();
            String specDesc = null;
            if (it.getSpecId() != null) {
                DishSpecDO spec = specMap.get(it.getSpecId());
                if (spec != null) {
                    unitPrice += spec.getPriceDelta() == null ? 0 : spec.getPriceDelta();
                    specDesc = spec.getGroupName() + ":" + spec.getOptionName();
                }
            }
            long addonPrice = 0;
            StringBuilder addonDesc = new StringBuilder();
            if (it.getAddonIds() != null) {
                for (Long addonId : it.getAddonIds()) {
                    DishAddonDO addon = addonMap.get(addonId);
                    if (addon != null) {
                        addonPrice += addon.getPriceDelta() == null ? 0 : addon.getPriceDelta();
                        if (addonDesc.length() > 0) {
                            addonDesc.append("、");
                        }
                        addonDesc.append(addon.getGroupName()).append(":").append(addon.getOptionName());
                    }
                }
            }
            int quantity = it.getQuantity() == null ? 1 : it.getQuantity();
            // P0-7：数量值域校验，防负数金额订单（VO 上有 @Min/@Max，此处服务层兜底）
            if (quantity < 1 || quantity > 999) {
                throw new ServiceException(ErrorCodeConstants.ORDER_ITEM_QUANTITY_INVALID);
            }
            long lineTotal = unitPrice * quantity + addonPrice;
            items.add(new OrderItemDO()
                    .setOrderId(orderId)
                    .setDishId(dish.getId())
                    .setDishName(dish.getName())
                    .setImage(dish.getImage())
                    .setSpecDesc(specDesc)
                    .setAddonDesc(addonDesc.length() == 0 ? null : addonDesc.toString())
                    .setUnitPrice(unitPrice)
                    .setAddonPrice(addonPrice)
                    .setQuantity(quantity)
                    .setTotalPrice(lineTotal));
        }
        return items;
    }

    private long sumTotal(List<OrderItemDO> items) {
        long total = 0;
        for (OrderItemDO item : items) {
            total += item.getTotalPrice() == null ? 0 : item.getTotalPrice();
        }
        return total;
    }

    /**
     * 计算优惠券抵扣金额（分）。返回 0 表示无抵扣。
     * 校验：券存在、未使用、未过期、归属当前用户、满足使用门槛。
     */
    private long computeCouponDiscount(Long couponId, Long userId, long total) {
        if (couponId == null) {
            return 0L;
        }
        CouponDO coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new ServiceException(ErrorCodeConstants.COUPON_NOT_EXISTS);
        }
        if (coupon.getStatus() != null && coupon.getStatus() != 0) {
            throw new ServiceException(ErrorCodeConstants.COUPON_STATUS_INVALID);
        }
        if (coupon.getExpireTime() != null && coupon.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ErrorCodeConstants.COUPON_EXPIRED);
        }
        if (userId != null && coupon.getUserId() != null && !coupon.getUserId().equals(userId)) {
            throw new ServiceException(ErrorCodeConstants.COUPON_USER_MISMATCH);
        }
        CouponTemplateDO template = couponTemplateMapper.selectById(coupon.getTemplateId());
        if (template == null) {
            throw new ServiceException(ErrorCodeConstants.COUPON_TEMPLATE_NOT_EXISTS);
        }
        if (template.getType() != null && template.getType() == 1) {
            // 满减：需达到门槛
            int threshold = template.getThresholdAmount() == null ? 0 : template.getThresholdAmount();
            if (total < threshold) {
                throw new ServiceException(ErrorCodeConstants.COUPON_THRESHOLD_NOT_MET);
            }
        }
        return calcDiscountByTemplate(template, total);
    }

    /**
     * 已核销优惠券的重算入口（加菜后按新总价重算优惠额）。
     * 与 {@link #computeCouponDiscount} 的区别：跳过券状态校验（券此时已是"已使用"），只按模板计算。
     */
    private long recalcCouponDiscount(Long couponId, long total) {
        if (couponId == null) {
            return 0L;
        }
        CouponDO coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return 0L;
        }
        CouponTemplateDO template = couponTemplateMapper.selectById(coupon.getTemplateId());
        if (template == null) {
            return 0L;
        }
        // 满减门槛不满足时按 0 优惠处理（加菜只会抬价，正常不会触发；防御性兜底）
        if (template.getType() != null && template.getType() == 1) {
            int threshold = template.getThresholdAmount() == null ? 0 : template.getThresholdAmount();
            if (total < threshold) {
                return 0L;
            }
        }
        return calcDiscountByTemplate(template, total);
    }

    /**
     * 按模板计算优惠额（分）。type=1 满减（门槛已校验）、type=2 折扣（discountValue 为折扣率，如 95 表示 95 折）。
     */
    private long calcDiscountByTemplate(CouponTemplateDO template, long total) {
        long discount;
        if (template.getType() != null && template.getType() == 1) {
            discount = template.getDiscountValue() == null ? 0 : template.getDiscountValue();
        } else if (template.getType() != null && template.getType() == 2) {
            int rate = template.getDiscountValue() == null ? 100 : template.getDiscountValue();
            if (rate <= 0 || rate >= 100) {
                discount = 0;
            } else {
                discount = (long) Math.round(total * (100 - rate) / 100.0);
            }
        } else {
            discount = 0;
        }
        // 优惠不得超过订单总额
        return Math.min(discount, total);
    }

    /**
     * 完成订单的统一逻辑：置已完成、记录完成时间、释放堂食桌台、累加会员消费。
     * completeOrder（按编号）与 verifyOrder（按核销码）共用，保证闭环一致。
     */
    private void doComplete(OrderDO order) {
        order.setStatus(OrderStatusEnum.COMPLETED.getStatus());
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
        // 堂食订单完成即释放桌台（占桌缺口修复：此前仅取消订单会释放）。
        // 条件更新（仅占用态才释放）：并发重复完成 / 手动改过桌台状态时不会产生错误覆盖
        if (OrderTypeEnum.DINE_IN.getType().equals(order.getType()) && order.getTableId() != null) {
            tableMapper.update(null, new LambdaUpdateWrapper<TableDO>()
                    .eq(TableDO::getId, order.getTableId())
                    .eq(TableDO::getStatus, 1)
                    .set(TableDO::getStatus, 0));
        }
        // 消费升级：订单完成后给会员累加成长值 / 积分 / 累计消费（散客忽略）
        Long payPrice = order.getPayPrice() != null ? order.getPayPrice() : order.getTotalPrice();
        memberService.addConsume(order.getMemberId(), payPrice);
    }

    private OrderDO validateOrderExists(Long id) {
        OrderDO order = orderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException(ErrorCodeConstants.ORDER_NOT_EXISTS);
        }
        return order;
    }

    private String generateOrderNo() {
        return "RO" + System.currentTimeMillis() + (int) (Math.random() * 9000 + 1000);
    }

    /**
     * 生成取餐号：取当前门店当日已下单数量 + 1（叫号展示用，不要求全局唯一）。
     */
    private int generatePickupNo(Long storeId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        Long count = orderMapper.selectCount(new LambdaQueryWrapperX<OrderDO>()
                .eq(OrderDO::getStoreId, storeId)
                .ge(OrderDO::getCreateTime, startOfDay));
        return (int) (count + 1);
    }

    /**
     * 生成核销码：6 位大写字母+数字，全局唯一（与已存在核销码冲突则重试）。
     */
    /**
     * 金额（分）转支付渠道入参（int）。
     * P1-10：芋道 pay 接口入参为 int，此处做上界断言——
     * 直接 intValue() 会在超过 2147 万元时静默溢出，导致"付 1 分钱"级别的事故
     */
    private int toIntPrice(Long price) {
        long value = price == null ? 0L : price;
        if (value <= 0 || value > MAX_PAY_PRICE) {
            throw new ServiceException(ErrorCodeConstants.ORDER_PRICE_OVERFLOW);
        }
        return (int) value;
    }

    /**
     * 插入订单并处理核销码唯一键冲突（P1-3）。
     * 核销码在 DB 上有唯一索引 uk_verify_code，"先查后插"在并发下仍可能撞码，
     * 撞 DuplicateKeyException 时换新码重试；重试耗尽属于异常低频事件，直接向上抛。
     */
    private void insertOrderWithVerifyCodeRetry(OrderDO order) {
        for (int i = 0; i < VERIFY_CODE_RETRY_TIMES; i++) {
            try {
                orderMapper.insert(order);
                return;
            } catch (DuplicateKeyException e) {
                // P2-H：原实现只换 verifyCode，但 uk_order_no(order_no) 也可能撞键
                // （并发下单同毫秒+同随机后缀），此时必须同时换 orderNo，
                // 否则下一次 insert 仍因 order_no 重复而失败、重试耗尽后抛
                // ORDER_VERIFY_CODE_NOT_FOUND（语义错误且资损）。
                log.warn("[insertOrderWithVerifyCodeRetry][订单插入冲突重试 orderNo({}) verifyCode({}) 第{}次]",
                        order.getOrderNo(), order.getVerifyCode(), i + 1);
                order.setVerifyCode(RandomUtil.randomStringUpper(6));
                order.setOrderNo(generateOrderNo());
            }
        }
        throw new ServiceException(ErrorCodeConstants.ORDER_VERIFY_CODE_NOT_FOUND);
    }

    /**
     * 逆向回滚订单权益（P1-6 券归还 + P1-7 消费积分冲正）。
     * 订单取消 / 退款时调用，保证"下单即核销的券被归还、已完成订单发放过的积分被冲正"。
     *
     * <p>幂等性：券归还走条件更新（仅绑定本订单且已使用的券才归还）；
     * 积分冲正只在订单曾处于已完成态时执行（{@code finishTime != null} 即视为已发放过积分）。
     */
    private void rollbackOrderBenefits(OrderDO order) {
        // 1. 归还优惠券
        if (order.getCouponId() != null) {
            couponService.releaseCoupon(order.getCouponId(), order.getId());
        }
        // 2. 冲正消费积分：仅对已完成过（发放过积分）的订单退款时执行，
        //    未完成订单从未发放积分，无需冲正，避免把历史正常消费的指标扣成负数
        if (order.getFinishTime() != null && order.getMemberId() != null) {
            Long payPrice = order.getPayPrice() != null ? order.getPayPrice() : order.getTotalPrice();
            memberService.reduceConsume(order.getMemberId(), payPrice);
        }
    }

    private String generateVerifyCode() {
        String code;
        do {
            code = RandomUtil.randomStringUpper(6);
        } while (orderMapper.selectByVerifyCode(code) != null);
        return code;
    }

}
