package cn.iocoder.yudao.module.restaurant.controller.admin.store.auth;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthBindReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthLoginRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthWeixinMiniAppLoginReqVO;
import cn.iocoder.yudao.module.restaurant.service.store.auth.StoreAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "门店 APP - 认证（复用后台 ADMIN 账号 + 门店角色）")
@RestController
@RequestMapping("/store-auth")
@Validated
public class StoreAuthController {

    @Resource
    private StoreAuthService authService;

    @PostMapping("/weixin-mini-app-login")
    @Operation(summary = "微信小程序登录（店员/收银）")
    @PermitAll
    public CommonResult<StoreAuthLoginRespVO> weixinMiniAppLogin(@RequestBody @Valid StoreAuthWeixinMiniAppLoginReqVO reqVO) {
        return success(authService.weixinMiniAppLogin(reqVO));
    }

    @PostMapping("/bind")
    @Operation(summary = "绑定微信到后台账号（店员首次使用小程序时）")
    @PermitAll
    public CommonResult<StoreAuthLoginRespVO> bind(@RequestBody @Valid StoreAuthBindReqVO reqVO) {
        return success(authService.bind(reqVO));
    }

}
