package cn.iocoder.yudao.module.restaurant.convert.member;

import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberAddressVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberAddressDO;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员收货地址 Convert
 *
 * @author 餐饮 SaaS
 */
public class MemberAddressConvert {

    public static MemberAddressVO.RespVO convert(MemberAddressDO bean) {
        if (bean == null) {
            return null;
        }
        MemberAddressVO.RespVO respVO = new MemberAddressVO.RespVO();
        respVO.setId(bean.getId());
        respVO.setUserId(bean.getUserId());
        respVO.setName(bean.getName());
        respVO.setPhone(bean.getPhone());
        respVO.setRegion(bean.getRegion());
        respVO.setDetail(bean.getDetail());
        respVO.setDefaultStatus(bean.getDefaultStatus());
        respVO.setCreateTime(bean.getCreateTime());
        return respVO;
    }

    public static List<MemberAddressVO.RespVO> convertList(List<MemberAddressDO> list) {
        List<MemberAddressVO.RespVO> result = new ArrayList<>();
        if (list != null) {
            for (MemberAddressDO item : list) {
                result.add(convert(item));
            }
        }
        return result;
    }

    public static MemberAddressDO convert(MemberAddressVO.SaveReqVO bean) {
        if (bean == null) {
            return null;
        }
        MemberAddressDO addressDO = new MemberAddressDO();
        addressDO.setId(bean.getId());
        addressDO.setName(bean.getName());
        addressDO.setPhone(bean.getPhone());
        addressDO.setRegion(bean.getRegion());
        addressDO.setDetail(bean.getDetail());
        addressDO.setDefaultStatus(bean.getDefaultStatus());
        return addressDO;
    }

}
