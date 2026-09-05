package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberAddressVO;
import cn.iocoder.yudao.module.restaurant.convert.member.MemberAddressConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberAddressDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.member.MemberAddressMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 会员收货地址 Service 实现类（M-23）
 *
 * 越权防护（同 P0-3 模式）：消费者端的 id 类入参一律先查归属，
 * address.userId 必须等于登录 userId，否则视为地址不存在（不泄露他人生存性）。
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class MemberAddressServiceImpl implements MemberAddressService {

    /**
     * 单用户地址上限：超过则提示清理，防止无限增长
     */
    private static final long ADDRESS_COUNT_LIMIT = 20;

    @Resource
    private MemberAddressMapper memberAddressMapper;

    @Override
    public Long createAddress(Long userId, MemberAddressVO.SaveReqVO createReqVO) {
        long count = memberAddressMapper.selectCount(MemberAddressDO::getUserId, userId);
        if (count >= ADDRESS_COUNT_LIMIT) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_ADDRESS_COUNT_LIMIT);
        }
        MemberAddressDO address = MemberAddressConvert.convert(createReqVO);
        address.setId(null); // 防止前端回传 id 造成误更新
        address.setUserId(userId);
        // 第一条地址强制默认；后续显式设默认的，同样清掉其它默认
        if (count == 0 || Integer.valueOf(1).equals(address.getDefaultStatus())) {
            address.setDefaultStatus(1);
        }
        memberAddressMapper.insert(address);
        if (Integer.valueOf(1).equals(address.getDefaultStatus())) {
            clearOtherDefaults(userId, address.getId());
        }
        return address.getId();
    }

    @Override
    public void updateAddress(Long userId, MemberAddressVO.SaveReqVO updateReqVO) {
        MemberAddressDO existing = validateAddressOwnedBy(userId, updateReqVO.getId());
        MemberAddressDO updateObj = MemberAddressConvert.convert(updateReqVO);
        updateObj.setUserId(existing.getUserId()); // 归属不可被改写
        memberAddressMapper.updateById(updateObj);
        if (Integer.valueOf(1).equals(updateObj.getDefaultStatus())) {
            clearOtherDefaults(userId, updateObj.getId());
        }
    }

    @Override
    public void deleteAddress(Long userId, Long id) {
        validateAddressOwnedBy(userId, id);
        memberAddressMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long id) {
        validateAddressOwnedBy(userId, id);
        clearOtherDefaults(userId, id);
        MemberAddressDO update = new MemberAddressDO();
        update.setId(id);
        update.setDefaultStatus(1);
        memberAddressMapper.updateById(update);
    }

    @Override
    public List<MemberAddressVO.RespVO> getAddressList(Long userId) {
        List<MemberAddressDO> list = memberAddressMapper.selectList(
                new LambdaQueryWrapperX<MemberAddressDO>()
                        .eq(MemberAddressDO::getUserId, userId)
                        .orderByDesc(MemberAddressDO::getDefaultStatus)
                        .orderByDesc(MemberAddressDO::getId));
        return MemberAddressConvert.convertList(list);
    }

    @Override
    public PageResult<MemberAddressVO.RespVO> getAddressPage(MemberAddressVO.PageReqVO pageReqVO) {
        PageResult<MemberAddressDO> page = memberAddressMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<MemberAddressDO>()
                        .eqIfPresent(MemberAddressDO::getUserId, pageReqVO.getUserId())
                        .orderByDesc(MemberAddressDO::getId));
        return new PageResult<>(MemberAddressConvert.convertList(page.getList()), page.getTotal());
    }

    @Override
    public void deleteAddressByAdmin(Long id) {
        validateAddressExists(id);
        memberAddressMapper.deleteById(id);
    }

    // ========== 辅助 ==========

    private void clearOtherDefaults(Long userId, Long excludeId) {
        List<MemberAddressDO> defaults = memberAddressMapper.selectList(
                new LambdaQueryWrapperX<MemberAddressDO>()
                        .eq(MemberAddressDO::getUserId, userId)
                        .eq(MemberAddressDO::getDefaultStatus, 1)
                        .ne(MemberAddressDO::getId, excludeId));
        if (defaults.isEmpty()) {
            return;
        }
        memberAddressMapper.updateById(convertList(defaults, addr -> {
            MemberAddressDO update = new MemberAddressDO();
            update.setId(addr.getId());
            update.setDefaultStatus(0);
            return update;
        }));
    }

    private MemberAddressDO validateAddressExists(Long id) {
        if (id == null) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_ADDRESS_NOT_EXISTS);
        }
        MemberAddressDO address = memberAddressMapper.selectById(id);
        if (address == null) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_ADDRESS_NOT_EXISTS);
        }
        return address;
    }

    private MemberAddressDO validateAddressOwnedBy(Long userId, Long id) {
        MemberAddressDO address = validateAddressExists(id);
        if (!address.getUserId().equals(userId)) {
            throw new ServiceException(ErrorCodeConstants.MEMBER_ADDRESS_NOT_OWNER);
        }
        return address;
    }

}
