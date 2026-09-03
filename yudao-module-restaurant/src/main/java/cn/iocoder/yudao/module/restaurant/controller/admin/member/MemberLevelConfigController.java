package cn.iocoder.yudao.module.restaurant.controller.admin.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberLevelConfigVO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberLevelConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 会员等级配置
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 会员等级配置")
@RestController
@RequestMapping("/store/member-level")
@Validated
public class MemberLevelConfigController {

    @Resource
    private MemberLevelConfigService memberLevelConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建会员等级")
    @PreAuthorize("hasAnyAuthority('restaurant:member-level:create')")
    public CommonResult<Long> create(@RequestBody @Valid MemberLevelConfigVO.SaveReqVO reqVO) {
        return success(memberLevelConfigService.createLevel(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会员等级")
    @PreAuthorize("hasAnyAuthority('restaurant:member-level:update')")
    public CommonResult<Boolean> update(@RequestParam("id") Long id,
                                        @RequestBody @Valid MemberLevelConfigVO.SaveReqVO reqVO) {
        memberLevelConfigService.updateLevel(id, reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会员等级")
    @PreAuthorize("hasAnyAuthority('restaurant:member-level:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        memberLevelConfigService.deleteLevel(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "会员等级分页")
    @PreAuthorize("hasAnyAuthority('restaurant:member-level:query')")
    public CommonResult<PageResult<MemberLevelConfigVO.RespVO>> page(
            @Valid MemberLevelConfigVO.PageReqVO pageReqVO) {
        return success(memberLevelConfigService.getLevelPage(pageReqVO));
    }

    @GetMapping("/enabled-list")
    @Operation(summary = "已启用的会员等级列表")
    @PreAuthorize("hasAnyAuthority('restaurant:member-level:query')")
    public CommonResult<List<MemberLevelConfigVO.RespVO>> enabledList() {
        return success(memberLevelConfigService.getEnabledLevels());
    }

    @GetMapping("/get")
    @Operation(summary = "获得会员等级（编辑回填）")
    @PreAuthorize("hasAnyAuthority('restaurant:member-level:query')")
    public CommonResult<MemberLevelConfigVO.RespVO> getLevel(@RequestParam("id") Long id) {
        return success(memberLevelConfigService.getLevel(id));
    }

}
