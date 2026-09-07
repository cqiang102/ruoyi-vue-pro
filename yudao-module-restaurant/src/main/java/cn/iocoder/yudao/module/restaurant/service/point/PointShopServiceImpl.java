package cn.iocoder.yudao.module.restaurant.service.point;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.point.vo.PointShopVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.point.PointOrderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.point.PointProductDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.MemberMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.point.PointOrderMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.point.PointProductMapper;
import cn.iocoder.yudao.module.restaurant.service.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;

/**
 * 积分商城 Service 实现（M-27）
 *
 * @author 餐饮 SaaS
 */
@Service
@Slf4j
public class PointShopServiceImpl implements PointShopService {

    @Resource
    private PointProductMapper pointProductMapper;
    @Resource
    private PointOrderMapper pointOrderMapper;
    @Resource
    private MemberMapper memberMapper;
    @Resource
    private MemberService memberService;

    // ===================== admin 端 =====================

    @Override
    public PageResult<PointProductDO> getProductPage(PageParam pageParam, Long storeId, Integer status) {
        return pointProductMapper.selectPage(pageParam, storeId, status);
    }

    @Override
    public Long createProduct(PointShopVO.ProductSaveReqVO reqVO, Long storeId) {
        PointProductDO product = new PointProductDO();
        product.setStoreId(storeId);
        copyProps(reqVO, product);
        pointProductMapper.insert(product);
        return product.getId();
    }

    @Override
    public void updateProduct(PointShopVO.ProductSaveReqVO reqVO, Long storeId) {
        PointProductDO product = validateProduct(reqVO.getId(), storeId);
        copyProps(reqVO, product);
        pointProductMapper.updateById(product);
    }

    @Override
    public void deleteProduct(Long id, Long storeId) {
        PointProductDO product = validateProduct(id, storeId);
        if (product.getStatus() != null && product.getStatus() == 1) {
            throw exception(POINT_PRODUCT_DELETE_FORBIDDEN);
        }
        pointProductMapper.deleteById(id);
    }

    @Override
    public PageResult<PointOrderDO> getOrderPage(PageParam pageParam, Long storeId, Integer status) {
        return pointOrderMapper.selectPage(pageParam, storeId, null, status);
    }

    @Override
    public PointShopVO.VerifyRespVO verify(String verifyCode, Long storeId) {
        PointOrderDO order = pointOrderMapper.selectByVerifyCode(verifyCode);
        if (order == null || !order.getStoreId().equals(storeId)) {
            throw exception(POINT_ORDER_NOT_EXISTS);
        }
        if (!Integer.valueOf(0).equals(order.getStatus())) {
            throw exception(POINT_ORDER_STATUS_INVALID);
        }
        order.setStatus(1);
        order.setVerifyTime(LocalDateTime.now());
        pointOrderMapper.updateById(order);
        PointShopVO.VerifyRespVO resp = new PointShopVO.VerifyRespVO();
        resp.setOrderId(order.getId());
        resp.setProductName(order.getProductName());
        resp.setQuantity(order.getQuantity());
        resp.setStatus(order.getStatus());
        return resp;
    }

    // ===================== 会员端 =====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointShopVO.ExchangeRespVO exchange(Long userId, PointShopVO.ExchangeReqVO reqVO) {
        // 1. 会员档案（积分扣减挂在 restaurant_member.pointBalance）
        MemberDO member = memberMapper.selectOne(new LambdaQueryWrapperX<MemberDO>()
                .eq(MemberDO::getUserId, userId));
        if (member == null) {
            throw exception(MEMBER_NOT_EXISTS);
        }
        // 2. 商品校验（本店上架）
        PointProductDO product = pointProductMapper.selectById(reqVO.getProductId());
        if (product == null) {
            throw exception(POINT_PRODUCT_NOT_EXISTS);
        }
        if (product.getStatus() == null || product.getStatus() != 1) {
            throw exception(POINT_PRODUCT_OFF_SHELF);
        }
        int quantity = reqVO.getQuantity();
        int totalPoints = product.getPoints() * quantity;
        // 3. CAS 扣库存（stock=-1 不限库存，跳过）
        if (product.getStock() != null && product.getStock() >= 0) {
            if (pointProductMapper.decreaseStock(product.getId(), quantity) == 0) {
                throw exception(POINT_PRODUCT_STOCK_NOT_ENOUGH);
            }
        }
        // 4. 扣积分（不足抛 MEMBER_POINT_NOT_ENOUGH，事务整体回滚含上面的库存扣减）
        memberService.adjustPoint(member.getId(), -totalPoints);
        // 5. 生成兑换记录 + 8 位核销码（碰撞重试一次）
        PointOrderDO order = new PointOrderDO();
        order.setStoreId(product.getStoreId());
        order.setUserId(userId);
        order.setMemberId(member.getId());
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setPoints(product.getPoints());
        order.setQuantity(quantity);
        order.setTotalPoints(totalPoints);
        order.setVerifyCode(generateVerifyCode());
        order.setStatus(0);
        pointOrderMapper.insert(order);
        PointShopVO.ExchangeRespVO resp = new PointShopVO.ExchangeRespVO();
        resp.setOrderId(order.getId());
        resp.setVerifyCode(order.getVerifyCode());
        return resp;
    }

    @Override
    public PageResult<PointShopVO.MyOrderRespVO> getMyOrders(Long userId, PageParam pageParam, Integer status) {
        PageResult<PointOrderDO> pageResult = pointOrderMapper.selectPage(pageParam, null, userId, status);
        List<PointShopVO.MyOrderRespVO> list = new ArrayList<>();
        for (PointOrderDO order : pageResult.getList()) {
            PointShopVO.MyOrderRespVO vo = new PointShopVO.MyOrderRespVO();
            vo.setId(order.getId());
            vo.setProductName(order.getProductName());
            PointProductDO product = pointProductMapper.selectById(order.getProductId());
            vo.setImage(product == null ? null : product.getImage());
            vo.setQuantity(order.getQuantity());
            vo.setTotalPoints(order.getTotalPoints());
            vo.setStatus(order.getStatus());
            vo.setVerifyCode(order.getVerifyCode());
            vo.setCreateTime(Convert.toStr(order.getCreateTime(), ""));
            list.add(vo);
        }
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelMyOrder(Long userId, Long orderId) {
        PointOrderDO order = pointOrderMapper.selectById(orderId);
        if (order == null) {
            throw exception(POINT_ORDER_NOT_EXISTS);
        }
        // 防水平越权：仅本人可取消
        if (!ObjectUtil.equal(order.getUserId(), userId)) {
            throw exception(POINT_ORDER_NOT_OWNER);
        }
        if (!Integer.valueOf(0).equals(order.getStatus())) {
            throw exception(POINT_ORDER_STATUS_INVALID);
        }
        order.setStatus(2);
        pointOrderMapper.updateById(order);
        // 退积分
        memberService.adjustPoint(order.getMemberId(), order.getTotalPoints());
        // 退库存（不限库存的商品当初未扣，无需回补）
        PointProductDO product = pointProductMapper.selectById(order.getProductId());
        if (product != null && product.getStock() != null && product.getStock() >= 0) {
            PointProductDO update = new PointProductDO();
            update.setId(product.getId());
            update.setStock(product.getStock() + order.getQuantity());
            pointProductMapper.updateById(update);
        }
    }

    // ===================== 私有辅助 =====================

    private void copyProps(PointShopVO.ProductSaveReqVO reqVO, PointProductDO product) {
        product.setName(reqVO.getName());
        product.setImage(reqVO.getImage());
        product.setPoints(reqVO.getPoints());
        product.setStock(reqVO.getStock());
        product.setDescription(reqVO.getDescription());
        product.setStatus(reqVO.getStatus() == null ? 1 : reqVO.getStatus());
        product.setSort(reqVO.getSort() == null ? 0 : reqVO.getSort());
    }

    private PointProductDO validateProduct(Long id, Long storeId) {
        PointProductDO product = pointProductMapper.selectById(id);
        if (product == null || !product.getStoreId().equals(storeId)) {
            throw exception(POINT_PRODUCT_NOT_EXISTS);
        }
        return product;
    }

    private String generateVerifyCode() {
        for (int i = 0; i < 3; i++) {
            String code = RandomUtil.randomNumbers(8);
            if (pointOrderMapper.selectByVerifyCode(code) == null) {
                return code;
            }
        }
        // 兜底：连续碰撞（概率 ~0）时拼接时间戳尾 6 位
        return RandomUtil.randomNumbers(4) + (System.currentTimeMillis() % 1000000);
    }

}
