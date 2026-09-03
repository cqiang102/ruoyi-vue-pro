package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberConfigVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberConfigDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.MemberConfigMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会员营销配置 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class MemberConfigServiceImpl implements MemberConfigService {

    @Resource
    private MemberConfigMapper memberConfigMapper;

    @Override
    public MemberConfigVO.RespVO getConfig() {
        MemberConfigDO config = getOrInitConfig();
        return new MemberConfigVO.RespVO()
                .setId(config.getId())
                .setEarnPerYuan(config.getEarnPerYuan())
                .setDeductPerPoint(config.getDeductPerPoint())
                .setMinDeductAmount(config.getMinDeductAmount())
                .setMaxDeductRate(config.getMaxDeductRate())
                .setLevelUpMode(config.getLevelUpMode())
                .setStatus(config.getStatus())
                .setCreateTime(config.getCreateTime())
                .setUpdateTime(config.getUpdateTime());
    }

    @Override
    public void saveConfig(MemberConfigVO.SaveReqVO reqVO) {
        MemberConfigDO exist = getCurrentTenantConfig();
        if (exist == null) {
            MemberConfigDO config = new MemberConfigDO()
                    .setEarnPerYuan(reqVO.getEarnPerYuan())
                    .setDeductPerPoint(reqVO.getDeductPerPoint())
                    .setMinDeductAmount(reqVO.getMinDeductAmount())
                    .setMaxDeductRate(reqVO.getMaxDeductRate())
                    .setLevelUpMode(reqVO.getLevelUpMode())
                    .setStatus(reqVO.getStatus());
            memberConfigMapper.insert(config);
        } else {
            exist.setEarnPerYuan(reqVO.getEarnPerYuan())
                    .setDeductPerPoint(reqVO.getDeductPerPoint())
                    .setMinDeductAmount(reqVO.getMinDeductAmount())
                    .setMaxDeductRate(reqVO.getMaxDeductRate())
                    .setLevelUpMode(reqVO.getLevelUpMode())
                    .setStatus(reqVO.getStatus());
            memberConfigMapper.updateById(exist);
        }
    }

    @Override
    public MemberConfigDO getOrInitConfig() {
        MemberConfigDO config = getCurrentTenantConfig();
        if (config == null) {
            config = new MemberConfigDO()
                    .setEarnPerYuan(1)
                    .setDeductPerPoint(10)
                    .setMinDeductAmount(0)
                    .setMaxDeductRate(50)
                    .setLevelUpMode(0)
                    .setStatus(1);
            memberConfigMapper.insert(config);
        }
        return config;
    }

    private MemberConfigDO getCurrentTenantConfig() {
        List<MemberConfigDO> list = memberConfigMapper.selectList(new LambdaQueryWrapperX<>());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
