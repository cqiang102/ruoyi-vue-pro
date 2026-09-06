package cn.iocoder.yudao.module.restaurant.service.delivery;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.delivery.vo.DeliveryVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.delivery.DeliveryOrderDO;

/**
 * 达达配送 Service（M-11）
 *
 * @author 餐饮 SaaS
 */
public interface DeliveryService {

    /**
     * 保存门店配送配置（storeId 服务端注入，一店一条 upsert）
     */
    void saveConfig(DeliveryVO.ConfigSaveReqVO reqVO, Long storeId);

    /**
     * 获取门店配送配置（无配置返回 null）
     */
    DeliveryVO.ConfigRespVO getConfig(Long storeId);

    /**
     * 发单（外卖订单 → 达达运单；取消/失败后可重发，复用同 origin_id）
     *
     * @param orderId 餐饮订单编号
     * @param storeId 登录店员绑定门店（P1-A）
     * @return 运单编号
     */
    Long sendDelivery(Long orderId, Long storeId);

    /**
     * 商家取消运单（调用达达取消 + 本地置已取消；仅待接单/待取货/配送中可取消）
     */
    void cancelDelivery(Long orderId, Long storeId);

    /**
     * 运单分页（本店隔离）
     */
    PageResult<DeliveryOrderDO> getDeliveryPage(PageParam pageParam, Long storeId, Integer status);

    /**
     * 达达状态回调处理（验签 + 状态映射；无租户上下文，内部忽略租户查单后按运单租户执行）
     */
    void handleCallback(DeliveryVO.CallbackReqVO reqVO);

}
