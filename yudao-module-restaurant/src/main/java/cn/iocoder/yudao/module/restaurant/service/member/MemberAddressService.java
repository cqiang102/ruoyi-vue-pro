package cn.iocoder.yudao.module.restaurant.service.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberAddressVO;

import java.util.List;

/**
 * 会员收货地址 Service 接口（M-23）
 *
 * @author 餐饮 SaaS
 */
public interface MemberAddressService {

    /**
     * 创建地址（消费者端）
     *
     * @param userId      登录用户编号（取登录态，不信任前端）
     * @param createReqVO 地址信息
     * @return 地址编号
     */
    Long createAddress(Long userId, MemberAddressVO.SaveReqVO createReqVO);

    /**
     * 更新地址（消费者端，校验归属）
     *
     * @param userId      登录用户编号
     * @param updateReqVO 地址信息
     */
    void updateAddress(Long userId, MemberAddressVO.SaveReqVO updateReqVO);

    /**
     * 删除地址（消费者端，校验归属）
     *
     * @param userId 登录用户编号
     * @param id     地址编号
     */
    void deleteAddress(Long userId, Long id);

    /**
     * 设为默认地址（事务内先清后设，保证唯一默认）
     *
     * @param userId 登录用户编号
     * @param id     地址编号
     */
    void setDefaultAddress(Long userId, Long id);

    /**
     * 获得用户地址列表（默认地址在前）
     *
     * @param userId 登录用户编号
     * @return 地址列表
     */
    List<MemberAddressVO.RespVO> getAddressList(Long userId);

    // ========== 管理后台 ==========

    /**
     * 获得会员地址分页（管理后台，按 userId 过滤）
     *
     * @param pageReqVO 分页查询
     * @return 地址分页
     */
    PageResult<MemberAddressVO.RespVO> getAddressPage(MemberAddressVO.PageReqVO pageReqVO);

    /**
     * 删除地址（管理后台，不做归属校验，依赖租户隔离）
     *
     * @param id 地址编号
     */
    void deleteAddressByAdmin(Long id);

}
