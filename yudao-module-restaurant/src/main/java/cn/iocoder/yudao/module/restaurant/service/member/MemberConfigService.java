package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberConfigVO;

/**
 * 会员营销配置 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface MemberConfigService {

    /**
     * 获取当前租户的会员营销配置（不存在时返回默认配置）
     */
    MemberConfigVO.RespVO getConfig();

    /**
     * 保存（存在则更新，不存在则插入）当前租户的会员营销配置
     */
    void saveConfig(MemberConfigVO.SaveReqVO reqVO);

    /**
     * 获取当前租户配置，不存在则写入默认并返回。供业务侧（如消费升级）调用。
     */
    cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberConfigDO getOrInitConfig();

}
