package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberLevelConfigVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberLevelConfigDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.MemberLevelConfigMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会员等级配置 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class MemberLevelConfigServiceImpl implements MemberLevelConfigService {

    @Resource
    private MemberLevelConfigMapper memberLevelConfigMapper;

    @Override
    public Long createLevel(MemberLevelConfigVO.SaveReqVO reqVO) {
        MemberLevelConfigDO level = new MemberLevelConfigDO()
                .setLevel(reqVO.getLevel())
                .setName(reqVO.getName())
                .setGrowthThreshold(reqVO.getGrowthThreshold())
                .setDiscountRate(reqVO.getDiscountRate())
                .setBenefits(reqVO.getBenefits())
                .setStatus(reqVO.getStatus());
        memberLevelConfigMapper.insert(level);
        return level.getId();
    }

    @Override
    public void updateLevel(Long id, MemberLevelConfigVO.SaveReqVO reqVO) {
        getLevelRequired(id);
        MemberLevelConfigDO level = new MemberLevelConfigDO()
                .setId(id)
                .setLevel(reqVO.getLevel())
                .setName(reqVO.getName())
                .setGrowthThreshold(reqVO.getGrowthThreshold())
                .setDiscountRate(reqVO.getDiscountRate())
                .setBenefits(reqVO.getBenefits())
                .setStatus(reqVO.getStatus());
        memberLevelConfigMapper.updateById(level);
    }

    @Override
    public void deleteLevel(Long id) {
        getLevelRequired(id);
        memberLevelConfigMapper.deleteById(id);
    }

    @Override
    public PageResult<MemberLevelConfigVO.RespVO> getLevelPage(MemberLevelConfigVO.PageReqVO pageReqVO) {
        PageResult<MemberLevelConfigDO> page = memberLevelConfigMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<MemberLevelConfigDO>()
                        .likeIfPresent(MemberLevelConfigDO::getName, pageReqVO.getName())
                        .eqIfPresent(MemberLevelConfigDO::getStatus, pageReqVO.getStatus())
                        .orderByAsc(MemberLevelConfigDO::getLevel));
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

    @Override
    public List<MemberLevelConfigVO.RespVO> getEnabledLevels() {
        List<MemberLevelConfigDO> list = memberLevelConfigMapper.selectList(
                new LambdaQueryWrapperX<MemberLevelConfigDO>()
                        .eq(MemberLevelConfigDO::getStatus, 1)
                        .orderByAsc(MemberLevelConfigDO::getLevel));
        return convertList(list);
    }

    @Override
    public MemberLevelConfigDO getLevelRequired(Long id) {
        MemberLevelConfigDO level = memberLevelConfigMapper.selectById(id);
        if (level == null) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_LEVEL_NOT_EXISTS);
        }
        return level;
    }

    @Override
    public MemberLevelConfigVO.RespVO getLevel(Long id) {
        return convert(getLevelRequired(id));
    }

    private List<MemberLevelConfigVO.RespVO> convertList(List<MemberLevelConfigDO> list) {
        return CollectionUtils.convertList(list, this::convert);
    }

    private MemberLevelConfigVO.RespVO convert(MemberLevelConfigDO level) {
        return new MemberLevelConfigVO.RespVO()
                .setId(level.getId())
                .setLevel(level.getLevel())
                .setName(level.getName())
                .setGrowthThreshold(level.getGrowthThreshold())
                .setDiscountRate(level.getDiscountRate())
                .setBenefits(level.getBenefits())
                .setStatus(level.getStatus())
                .setCreateTime(level.getCreateTime())
                .setUpdateTime(level.getUpdateTime());
    }

}
