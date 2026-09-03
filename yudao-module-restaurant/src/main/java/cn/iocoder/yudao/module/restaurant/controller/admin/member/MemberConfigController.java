package cn.iocoder.yudao.module.restaurant.controller.admin.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberConfigVO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 会员营销配置
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 会员营销配置")
@RestController
@RequestMapping("/store/member-config")
@Validated
public class MemberConfigController {

    @Resource
    private MemberConfigService memberConfigService;

    @GetMapping("/get")
    @Operation(summary = "获取当前租户会员营销配置")
    @PreAuthorize("hasAnyAuthority('restaurant:member-config:query')")
    public CommonResult<MemberConfigVO.RespVO> get() {
        return success(memberConfigService.getConfig());
    }

    @PostMapping("/save")
    @Operation(summary = "保存（新增/更新）会员营销配置")
    @PreAuthorize("hasAnyAuthority('restaurant:member-config:save')")
    public CommonResult<Boolean> save(@RequestBody @Valid MemberConfigVO.SaveReqVO reqVO) {
        memberConfigService.saveConfig(reqVO);
        return success(true);
    }

}
