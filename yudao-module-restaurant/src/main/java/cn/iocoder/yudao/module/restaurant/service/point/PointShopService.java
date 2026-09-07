package cn.iocoder.yudao.module.restaurant.service.point;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.point.vo.PointShopVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.point.PointOrderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.point.PointProductDO;

/**
 * 积分商城 Service（M-27）
 * <p>
 * 兑换流程：校验上架 + CAS 扣库存 → adjustPoint 扣积分（不足抛异常回滚）→ 生成核销码。
 * 扣积分失败时整体回滚（事务内），扣库存的 CAS 影响行数为 0 即库存不足。
 *
 * @author 餐饮 SaaS
 */
public interface PointShopService {

    // ========== admin 端 ==========

    /**
     * 商品分页（本店）
     */
    PageResult<PointProductDO> getProductPage(PageParam pageParam, Long storeId, Integer status);

    /**
     * 创建商品（P1-A：storeId 注入）
     */
    Long createProduct(PointShopVO.ProductSaveReqVO reqVO, Long storeId);

    /**
     * 更新商品（本店校验）
     */
    void updateProduct(PointShopVO.ProductSaveReqVO reqVO, Long storeId);

    /**
     * 删除商品（仅下架状态可删）
     */
    void deleteProduct(Long id, Long storeId);

    /**
     * 兑换记录分页（本店）
     */
    PageResult<PointOrderDO> getOrderPage(PageParam pageParam, Long storeId, Integer status);

    /**
     * 店员核销（按核销码；本店校验）
     */
    PointShopVO.VerifyRespVO verify(String verifyCode, Long storeId);

    // ========== 会员端 ==========

    /**
     * 兑换（登录会员）：扣积分 + 扣库存 + 生成核销码
     *
     * @param userId 登录会员用户编号（MemberUserDO.id）
     * @param reqVO  兑换请求
     * @return 兑换结果（含核销码）
     */
    PointShopVO.ExchangeRespVO exchange(Long userId, PointShopVO.ExchangeReqVO reqVO);

    /**
     * 我的兑换列表（待核销/已核销/已取消）
     */
    PageResult<PointShopVO.MyOrderRespVO> getMyOrders(Long userId, PageParam pageParam, Integer status);

    /**
     * 会员取消兑换（仅待核销；退积分退库存）
     */
    void cancelMyOrder(Long userId, Long orderId);

}
