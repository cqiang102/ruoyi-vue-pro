package cn.iocoder.yudao.module.restaurant.convert.store;

import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.TableVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.TableDO;

/**
 * 桌台 Convert
 *
 * @author 餐饮 SaaS
 */
public class TableConvert {

    public static TableVO.RespVO convert(TableDO bean) {
        if (bean == null) {
            return null;
        }
        TableVO.RespVO respVO = new TableVO.RespVO();
        respVO.setId(bean.getId());
        respVO.setStoreId(bean.getStoreId());
        respVO.setTableNo(bean.getTableNo());
        respVO.setCategory(bean.getCategory());
        respVO.setSeats(bean.getSeats());
        respVO.setStatus(bean.getStatus());
        respVO.setQrcodeContent(bean.getQrcodeContent());
        respVO.setCreateTime(bean.getCreateTime());
        return respVO;
    }

}
