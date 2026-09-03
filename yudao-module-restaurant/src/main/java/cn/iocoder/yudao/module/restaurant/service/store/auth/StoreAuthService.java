package cn.iocoder.yudao.module.restaurant.service.store.auth;

import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthBindReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthLoginRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthWeixinMiniAppLoginReqVO;

/**
 * 门店认证 Service
 * <p>
 * 门店店员/收银复用后台 ADMIN 账号：微信 openid 通过芋道社交绑定表挂到 ADMIN 用户，
 * 登录时签发 ADMIN 类型令牌，权限由该账号被赋予的「门店店员/门店收银」角色控制。
 *
 * @author 餐饮 SaaS
 */
public interface StoreAuthService {

    /**
     * 微信小程序登录
     */
    StoreAuthLoginRespVO weixinMiniAppLogin(StoreAuthWeixinMiniAppLoginReqVO reqVO);

    /**
     * 绑定微信到后台账号（首次使用）
     */
    StoreAuthLoginRespVO bind(StoreAuthBindReqVO reqVO);

}
