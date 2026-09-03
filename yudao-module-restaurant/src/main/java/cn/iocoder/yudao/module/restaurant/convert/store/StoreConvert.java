package cn.iocoder.yudao.module.restaurant.convert.store;

import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;

/**
 * 门店 Convert
 *
 * @author 餐饮 SaaS
 */
public class StoreConvert {

    public static StoreVO.RespVO convert(StoreDO bean) {
        if (bean == null) {
            return null;
        }
        StoreVO.RespVO respVO = new StoreVO.RespVO();
        respVO.setId(bean.getId());
        respVO.setName(bean.getName());
        respVO.setContact(bean.getContact());
        respVO.setPhone(bean.getPhone());
        respVO.setAddress(bean.getAddress());
        respVO.setBusinessStart(bean.getBusinessStart());
        respVO.setBusinessEnd(bean.getBusinessEnd());
        respVO.setStatus(bean.getStatus());
        respVO.setDeliveryFee(bean.getDeliveryFee());
        respVO.setMinOrderAmount(bean.getMinOrderAmount());
        respVO.setCreateTime(bean.getCreateTime());
        return respVO;
    }

}
