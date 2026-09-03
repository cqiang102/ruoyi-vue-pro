package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberConfigDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberLevelConfigDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.MemberLevelConfigMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.MemberMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员档案 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, MemberDO> implements MemberService {

    @Resource
    private MemberConfigService memberConfigService;
    @Resource
    private MemberLevelConfigMapper memberLevelConfigMapper;

    @Override
    public MemberDO getOrCreateMember(Long userId) {
        if (userId == null) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_NOT_EXISTS);
        }
        MemberDO existing = getMemberByUserId(userId);
        if (existing != null) {
            return existing;
        }
        // P2-G：read-then-insert 在并发下两线程同时通过 selectOne 检查后并发 insert，
        // 后者会撞 uk_tenant_user(user_id, tenant_id) 唯一索引抛 DuplicateKeyException。
        // 此处 catch 后回查一次，保证最终一致；非 DuplicateKey 异常继续抛出
        MemberDO member = new MemberDO();
        member.setUserId(userId);
        member.setGrowthValue(0);
        member.setPointBalance(0);
        member.setTotalConsume(0L);
        member.setVersion(0);
        try {
            memberMapper().insert(member);
            return member;
        } catch (DuplicateKeyException e) {
            MemberDO fallback = getMemberByUserId(userId);
            if (fallback != null) {
                return fallback;
            }
            throw e;
        }
    }

    @Override
    public MemberDO getMemberRequired(Long id) {
        MemberDO member = getById(id);
        if (member == null) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_NOT_EXISTS);
        }
        return member;
    }

    @Override
    public MemberDO getMemberByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return memberMapper().selectOne(MemberDO::getUserId, userId);
    }

    @Override
    public PageResult<MemberVO.RespVO> getMemberPage(MemberVO.PageReqVO pageReqVO) {
        PageResult<MemberDO> page = memberMapper().selectPage(pageReqVO,
                new LambdaQueryWrapperX<MemberDO>()
                        .eqIfPresent(MemberDO::getUserId, pageReqVO.getUserId())
                        .orderByDesc(MemberDO::getId));
        List<MemberVO.RespVO> list = new ArrayList<>(page.getList().size());
        for (MemberDO member : page.getList()) {
            list.add(convertToResp(member));
        }
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public void addConsume(Long memberId, Long payPrice) {
        if (memberId == null || payPrice == null || payPrice <= 0) {
            return; // 散客 / 无效金额直接忽略
        }
        if (getById(memberId) == null) {
            return; // 尚未建档的散客，忽略消费升级
        }
        MemberConfigDO config = memberConfigService.getOrInitConfig();
        int earnPerYuan = (config.getEarnPerYuan() != null && config.getEarnPerYuan() > 0)
                ? config.getEarnPerYuan() : 1;
        int yuan = (int) (payPrice / 100L);
        int points = yuan * earnPerYuan;
        int growth = yuan; // 每消费 1 元得 1 成长值

        for (int i = 0; i < 5; i++) {
            MemberDO cur = getById(memberId);
            if (cur == null) {
                return;
            }
            int newGrowth = (cur.getGrowthValue() == null ? 0 : cur.getGrowthValue()) + growth;
            int newPoint = (cur.getPointBalance() == null ? 0 : cur.getPointBalance()) + points;
            long newTotal = (cur.getTotalConsume() == null ? 0L : cur.getTotalConsume()) + payPrice;
            cur.setGrowthValue(newGrowth);
            cur.setPointBalance(newPoint);
            cur.setTotalConsume(newTotal);
            cur.setLevelId(computeLevel(cur, config));
            if (memberMapper().updateById(cur) > 0) {
                return;
            }
        }
        throw new ServiceException(ErrorCodeConstants.MEMBER_POINT_CHANGE_FAILED);
    }

    @Override
    public void reduceConsume(Long memberId, Long payPrice) {
        if (memberId == null || payPrice == null || payPrice <= 0) {
            return; // 散客 / 无效金额直接忽略
        }
        if (getById(memberId) == null) {
            return; // 尚未建档的散客，忽略冲正
        }
        MemberConfigDO config = memberConfigService.getOrInitConfig();
        int earnPerYuan = (config.getEarnPerYuan() != null && config.getEarnPerYuan() > 0)
                ? config.getEarnPerYuan() : 1;
        int yuan = (int) (payPrice / 100L);
        int points = yuan * earnPerYuan;
        int growth = yuan;

        // 与 addConsume 对称的 CAS 冲正：各项指标向下钳制为 0（已花掉的积分不再追负数），
        // 避免退款后刷积分成为无本之利（P1-7）
        for (int i = 0; i < 5; i++) {
            MemberDO cur = getById(memberId);
            if (cur == null) {
                return;
            }
            int newGrowth = Math.max(0, (cur.getGrowthValue() == null ? 0 : cur.getGrowthValue()) - growth);
            int newPoint = Math.max(0, (cur.getPointBalance() == null ? 0 : cur.getPointBalance()) - points);
            long newTotal = Math.max(0L, (cur.getTotalConsume() == null ? 0L : cur.getTotalConsume()) - payPrice);
            cur.setGrowthValue(newGrowth);
            cur.setPointBalance(newPoint);
            cur.setTotalConsume(newTotal);
            cur.setLevelId(computeLevel(cur, config));
            if (memberMapper().updateById(cur) > 0) {
                return;
            }
        }
        throw new ServiceException(ErrorCodeConstants.MEMBER_POINT_CHANGE_FAILED);
    }

    @Override
    public void adjustPoint(Long memberId, Integer delta) {
        if (memberId == null || delta == null || delta == 0) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            MemberDO cur = getById(memberId);
            if (cur == null) {
                throw new ServiceException(ErrorCodeConstants.MEMBER_NOT_EXISTS);
            }
            int newPoint = (cur.getPointBalance() == null ? 0 : cur.getPointBalance()) + delta;
            if (newPoint < 0) {
                throw new ServiceException(ErrorCodeConstants.MEMBER_POINT_NOT_ENOUGH);
            }
            cur.setPointBalance(newPoint);
            if (memberMapper().updateById(cur) > 0) {
                return;
            }
        }
        throw new ServiceException(ErrorCodeConstants.MEMBER_POINT_CHANGE_FAILED);
    }

    // ===================== 私有辅助 =====================

    private MemberVO.RespVO convertToResp(MemberDO member) {
        MemberVO.RespVO respVO = new MemberVO.RespVO();
        respVO.setId(member.getId());
        respVO.setUserId(member.getUserId());
        respVO.setLevelId(member.getLevelId());
        if (member.getLevelId() != null) {
            MemberLevelConfigDO level = memberLevelConfigMapper.selectById(member.getLevelId());
            respVO.setLevelName(level != null ? level.getName() : null);
        }
        respVO.setGrowthValue(member.getGrowthValue());
        respVO.setPointBalance(member.getPointBalance());
        respVO.setTotalConsume(member.getTotalConsume());
        respVO.setCreateTime(member.getCreateTime());
        respVO.setUpdateTime(member.getUpdateTime());
        return respVO;
    }

    /**
     * 根据消费升级基准，计算命中的最高等级。
     * levelUpMode=0 按成长值；=1 按累计消费（分）。
     */
    private Long computeLevel(MemberDO member, MemberConfigDO config) {
        List<MemberLevelConfigDO> levels = memberLevelConfigMapper.selectList(
                MemberLevelConfigDO::getStatus, 1);
        if (levels == null || levels.isEmpty()) {
            return member.getLevelId();
        }
        long value;
        if (config != null && config.getLevelUpMode() != null && config.getLevelUpMode() == 1) {
            value = member.getTotalConsume() == null ? 0L : member.getTotalConsume();
        } else {
            value = member.getGrowthValue() == null ? 0 : member.getGrowthValue();
        }
        Long bestLevelId = member.getLevelId();
        for (MemberLevelConfigDO lvl : levels) {
            int threshold = lvl.getGrowthThreshold() == null ? 0 : lvl.getGrowthThreshold();
            if (value >= threshold) {
                bestLevelId = lvl.getId();
            }
        }
        return bestLevelId;
    }

    private MemberMapper memberMapper() {
        return getBaseMapper();
    }

}
