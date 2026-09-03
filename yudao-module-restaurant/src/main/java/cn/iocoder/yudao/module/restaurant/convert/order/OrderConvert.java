package cn.iocoder.yudao.module.restaurant.convert.order;

import cn.iocoder.yudao.module.restaurant.controller.admin.order.vo.OrderVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderItemDO;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单 Convert
 *
 * @author 餐饮 SaaS
 */
public class OrderConvert {

    public static OrderVO.RespVO convert(OrderDO bean) {
        if (bean == null) {
            return null;
        }
        OrderVO.RespVO respVO = new OrderVO.RespVO();
        respVO.setId(bean.getId());
        respVO.setStoreId(bean.getStoreId());
        respVO.setTableId(bean.getTableId());
        respVO.setOrderNo(bean.getOrderNo());
        respVO.setType(bean.getType());
        respVO.setStatus(bean.getStatus());
        respVO.setTotalPrice(bean.getTotalPrice());
        respVO.setPayPrice(bean.getPayPrice());
        respVO.setDiscountPrice(bean.getDiscountPrice());
        respVO.setDeliveryFee(bean.getDeliveryFee());
        respVO.setReceiverName(bean.getReceiverName());
        respVO.setReceiverPhone(bean.getReceiverPhone());
        respVO.setReceiverAddress(bean.getReceiverAddress());
        respVO.setPayType(bean.getPayType());
        respVO.setPayStatus(bean.getPayStatus());
        respVO.setMemberId(bean.getMemberId());
        respVO.setUserId(bean.getUserId());
        respVO.setPeopleCount(bean.getPeopleCount());
        respVO.setRemark(bean.getRemark());
        respVO.setPayOrderId(bean.getPayOrderId());
        respVO.setRefundPrice(bean.getRefundPrice());
        respVO.setPickupNo(bean.getPickupNo());
        respVO.setVerifyCode(bean.getVerifyCode());
        respVO.setCalledTime(bean.getCalledTime());
        respVO.setPaidTime(bean.getPaidTime());
        respVO.setFinishTime(bean.getFinishTime());
        respVO.setCreateTime(bean.getCreateTime());
        return respVO;
    }

    public static OrderVO.ItemRespVO convertItem(OrderItemDO bean) {
        if (bean == null) {
            return null;
        }
        OrderVO.ItemRespVO respVO = new OrderVO.ItemRespVO();
        respVO.setId(bean.getId());
        respVO.setDishId(bean.getDishId());
        respVO.setDishName(bean.getDishName());
        respVO.setImage(bean.getImage());
        respVO.setSpecDesc(bean.getSpecDesc());
        respVO.setAddonDesc(bean.getAddonDesc());
        respVO.setUnitPrice(bean.getUnitPrice());
        respVO.setAddonPrice(bean.getAddonPrice());
        respVO.setQuantity(bean.getQuantity());
        respVO.setTotalPrice(bean.getTotalPrice());
        return respVO;
    }

    public static List<OrderVO.ItemRespVO> convertItemList(List<OrderItemDO> list) {
        List<OrderVO.ItemRespVO> result = new ArrayList<>();
        if (list != null) {
            for (OrderItemDO item : list) {
                result.add(convertItem(item));
            }
        }
        return result;
    }

}
