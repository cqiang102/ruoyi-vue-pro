package cn.iocoder.yudao.module.restaurant.service.store.auth;

import cn.iocoder.yudao.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.monitor.TracerUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthBindReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthLoginRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthWeixinMiniAppLoginReqVO;
import cn.iocoder.yudao.module.restaurant.convert.auth.StoreAuthConvert;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.restaurant.service.packageconf.TenantSubscriptionService;
import cn.iocoder.yudao.module.system.api.logger.LoginLogApi;
import cn.iocoder.yudao.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.iocoder.yudao.module.system.api.social.SocialUserApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum;
import cn.iocoder.yudao.module.system.enums.logger.LoginResultEnum;
import cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 门店认证 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Slf4j
public class StoreAuthServiceImpl implements StoreAuthService {

    @Resource
    private SocialUserApi socialUserApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private OAuth2TokenCommonApi oauth2TokenApi;
    @Resource
    private LoginLogApi loginLogApi;
    @Resource
    private TenantSubscriptionService tenantSubscriptionService;

    @Override
    public StoreAuthLoginRespVO weixinMiniAppLogin(StoreAuthWeixinMiniAppLoginReqVO reqVO) {
        // 1. 用 code 换取社交用户（userType=ADMIN，复用后台账号）
        SocialUserRespDTO socialUser = socialUserApi.getSocialUserByCode(
                UserTypeEnum.ADMIN.getValue(), reqVO.getSocialType(), reqVO.getCode(), reqVO.getState());
        if (socialUser == null || socialUser.getUserId() == null) {
            // 未绑定：提示店员先在后台绑定，或使用账号密码在小程序内自助绑定
            throw exception(ErrorCodeConstants.STORE_USER_NOT_BOUND);
        }
        // 2. 校验账号有效（存在且未禁用）
        adminUserApi.validateUser(socialUser.getUserId());
        AdminUserRespDTO user = adminUserApi.getUser(socialUser.getUserId());
        // 2.1 校验租户订阅有效，过期/欠费则拦截门店登录
        tenantSubscriptionService.checkActive(TenantContextHolder.getTenantId());
        // 3. 签发 ADMIN 令牌（门店店员/收银即后台账号 + 对应角色）
        OAuth2AccessTokenRespDTO token = createAccessToken(user.getId());
        // 4. 登录日志
        createLoginLog(user, LoginResultEnum.SUCCESS);
        return StoreAuthConvert.INSTANCE.convert(token, user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StoreAuthLoginRespVO bind(StoreAuthBindReqVO reqVO) {
        // 1. 校验后台账号密码（复用 ADMIN 账号）
        AdminUserRespDTO user = adminUserApi.verifyPassword(reqVO.getUsername(), reqVO.getPassword());
        // 1.1 校验租户订阅有效，过期/欠费则拦截门店登录
        tenantSubscriptionService.checkActive(TenantContextHolder.getTenantId());
        // 2. 绑定微信到该 ADMIN 用户（userType=ADMIN）
        socialUserApi.bindSocialUser(new SocialUserBindReqDTO(
                user.getId(), UserTypeEnum.ADMIN.getValue(), reqVO.getSocialType(), reqVO.getCode(), reqVO.getState()));
        // 3. 签发令牌
        OAuth2AccessTokenRespDTO token = createAccessToken(user.getId());
        createLoginLog(user, LoginResultEnum.SUCCESS);
        return StoreAuthConvert.INSTANCE.convert(token, user);
    }

    private OAuth2AccessTokenRespDTO createAccessToken(Long userId) {
        return oauth2TokenApi.createAccessToken(new OAuth2AccessTokenCreateReqDTO()
                .setUserId(userId)
                .setUserType(UserTypeEnum.ADMIN.getValue())
                .setClientId(OAuth2ClientConstants.CLIENT_ID_DEFAULT));
    }

    private void createLoginLog(AdminUserRespDTO user, LoginResultEnum result) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(LoginLogTypeEnum.LOGIN_SOCIAL.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(user.getId());
        reqDTO.setUserType(UserTypeEnum.ADMIN.getValue());
        reqDTO.setUsername(user.getMobile() != null ? user.getMobile() : user.getNickname());
        reqDTO.setResult(result.getResult());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        loginLogApi.createLoginLog(reqDTO);
    }

}
