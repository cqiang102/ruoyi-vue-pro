package cn.iocoder.yudao.module.restaurant.controller.admin.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberCardVO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 会员卡")
@RestController
@RequestMapping("/store/card")
@Validated
public class MemberCardController {

    @Resource
    private MemberCardService memberCardService;

    @PostMapping("/create")
    @Operation(summary = "创建会员卡")
    @PreAuthorize("hasAnyAuthority('restaurant:member-card:create')")
    public CommonResult<Long> createCard(@RequestBody @Valid MemberCardVO.SaveReqVO createReqVO) {
        return success(memberCardService.createCard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会员卡")
    @PreAuthorize("hasAnyAuthority('restaurant:member-card:update')")
    public CommonResult<Boolean> updateCard(@RequestBody @Valid MemberCardVO.SaveReqVO updateReqVO) {
        memberCardService.updateCard(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会员卡")
    @PreAuthorize("hasAnyAuthority('restaurant:member-card:delete')")
    public CommonResult<Boolean> deleteCard(@RequestParam("id") Long id) {
        memberCardService.deleteCard(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得会员卡分页")
    @PreAuthorize("hasAnyAuthority('restaurant:member-card:query')")
    public CommonResult<PageResult<MemberCardVO.RespVO>> getCardPage(@Valid MemberCardVO.PageReqVO pageReqVO) {
        return success(memberCardService.getCardPage(pageReqVO));
    }

    @GetMapping("/order-page")
    @Operation(summary = "获得购卡记录分页（客服排查）")
    @PreAuthorize("hasAnyAuthority('restaurant:member-card:query')")
    public CommonResult<PageResult<MemberCardVO.OrderRespVO>> getOrderPage(
            @Valid MemberCardVO.OrderPageReqVO pageReqVO) {
        return success(memberCardService.getOrderPage(pageReqVO));
    }

}
