package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberConfigVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.RestaurantMemberConfigDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.RestaurantMemberConfigMapper;
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
public class RestaurantMemberConfigServiceImpl implements RestaurantMemberConfigService {

    @Resource
    private RestaurantMemberConfigMapper restaurantMemberConfigMapper;

    @Override
    public MemberConfigVO.RespVO getConfig() {
        RestaurantMemberConfigDO config = getOrInitConfig();
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
        RestaurantMemberConfigDO exist = getCurrentTenantConfig();
        if (exist == null) {
            RestaurantMemberConfigDO config = new RestaurantMemberConfigDO()
                    .setEarnPerYuan(reqVO.getEarnPerYuan())
                    .setDeductPerPoint(reqVO.getDeductPerPoint())
                    .setMinDeductAmount(reqVO.getMinDeductAmount())
                    .setMaxDeductRate(reqVO.getMaxDeductRate())
                    .setLevelUpMode(reqVO.getLevelUpMode())
                    .setStatus(reqVO.getStatus());
            restaurantMemberConfigMapper.insert(config);
        } else {
            exist.setEarnPerYuan(reqVO.getEarnPerYuan())
                    .setDeductPerPoint(reqVO.getDeductPerPoint())
                    .setMinDeductAmount(reqVO.getMinDeductAmount())
                    .setMaxDeductRate(reqVO.getMaxDeductRate())
                    .setLevelUpMode(reqVO.getLevelUpMode())
                    .setStatus(reqVO.getStatus());
            restaurantMemberConfigMapper.updateById(exist);
        }
    }

    @Override
    public RestaurantMemberConfigDO getOrInitConfig() {
        RestaurantMemberConfigDO config = getCurrentTenantConfig();
        if (config == null) {
            config = new RestaurantMemberConfigDO()
                    .setEarnPerYuan(1)
                    .setDeductPerPoint(10)
                    .setMinDeductAmount(0)
                    .setMaxDeductRate(50)
                    .setLevelUpMode(0)
                    .setStatus(1);
            restaurantMemberConfigMapper.insert(config);
        }
        return config;
    }

    private RestaurantMemberConfigDO getCurrentTenantConfig() {
        List<RestaurantMemberConfigDO> list = restaurantMemberConfigMapper.selectList(new LambdaQueryWrapperX<>());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
