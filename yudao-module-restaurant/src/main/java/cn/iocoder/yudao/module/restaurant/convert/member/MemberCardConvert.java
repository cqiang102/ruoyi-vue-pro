package cn.iocoder.yudao.module.restaurant.convert.member;

import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberCardVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberCardDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberCardOrderDO;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员卡 Convert
 *
 * @author 餐饮 SaaS
 */
public class MemberCardConvert {

    public static MemberCardVO.RespVO convert(MemberCardDO bean) {
        if (bean == null) {
            return null;
        }
        MemberCardVO.RespVO respVO = new MemberCardVO.RespVO();
        respVO.setId(bean.getId());
        respVO.setName(bean.getName());
        respVO.setPrice(bean.getPrice());
        respVO.setDescription(bean.getDescription());
        respVO.setRights(bean.getRights());
        respVO.setStatus(bean.getStatus());
        respVO.setSoldCount(bean.getSoldCount());
        respVO.setSort(bean.getSort());
        respVO.setCreateTime(bean.getCreateTime());
        return respVO;
    }

    public static List<MemberCardVO.RespVO> convertList(List<MemberCardDO> list) {
        List<MemberCardVO.RespVO> result = new ArrayList<>();
        if (list != null) {
            for (MemberCardDO item : list) {
                result.add(convert(item));
            }
        }
        return result;
    }

    public static MemberCardDO convert(MemberCardVO.SaveReqVO bean) {
        if (bean == null) {
            return null;
        }
        MemberCardDO cardDO = new MemberCardDO();
        cardDO.setId(bean.getId());
        cardDO.setName(bean.getName());
        cardDO.setPrice(bean.getPrice());
        cardDO.setDescription(bean.getDescription());
        cardDO.setRights(bean.getRights());
        cardDO.setStatus(bean.getStatus());
        cardDO.setSort(bean.getSort());
        return cardDO;
    }

    public static MemberCardVO.OrderRespVO convertOrder(MemberCardOrderDO bean) {
        if (bean == null) {
            return null;
        }
        MemberCardVO.OrderRespVO respVO = new MemberCardVO.OrderRespVO();
        respVO.setId(bean.getId());
        respVO.setOrderNo(bean.getOrderNo());
        respVO.setUserId(bean.getUserId());
        respVO.setCardId(bean.getCardId());
        respVO.setCardName(bean.getCardName());
        respVO.setPrice(bean.getPrice());
        respVO.setPayType(bean.getPayType());
        respVO.setStatus(bean.getStatus());
        respVO.setPaidTime(bean.getPaidTime());
        respVO.setCreateTime(bean.getCreateTime());
        return respVO;
    }

    public static List<MemberCardVO.OrderRespVO> convertOrderList(List<MemberCardOrderDO> list) {
        List<MemberCardVO.OrderRespVO> result = new ArrayList<>();
        if (list != null) {
            for (MemberCardOrderDO item : list) {
                result.add(convertOrder(item));
            }
        }
        return result;
    }

}
