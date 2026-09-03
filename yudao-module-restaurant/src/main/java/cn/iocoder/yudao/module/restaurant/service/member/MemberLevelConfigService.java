package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberLevelConfigVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberLevelConfigDO;

import java.util.List;

/**
 * 会员等级配置 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface MemberLevelConfigService {

    /**
     * 创建会员等级配置
     */
    Long createLevel(MemberLevelConfigVO.SaveReqVO reqVO);

    /**
     * 更新会员等级配置
     */
    void updateLevel(Long id, MemberLevelConfigVO.SaveReqVO reqVO);

    /**
     * 删除会员等级配置
     */
    void deleteLevel(Long id);

    /**
     * 分页查询会员等级配置
     */
    PageResult<MemberLevelConfigVO.RespVO> getLevelPage(MemberLevelConfigVO.PageReqVO pageReqVO);

    /**
     * 查询已启用的等级配置（升序）
     */
    List<MemberLevelConfigVO.RespVO> getEnabledLevels();

    /**
     * 获取等级配置，校验存在
     */
    MemberLevelConfigDO getLevelRequired(Long id);

    /**
     * 获取会员等级配置（编辑回填）
     */
    MemberLevelConfigVO.RespVO getLevel(Long id);

}
