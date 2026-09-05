package cn.iocoder.yudao.module.restaurant.controller.app.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.member.vo.MemberCardVO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "消费者小程序 - 会员卡")
@RestController
@RequestMapping("/member/card")
@Validated
public class AppMemberCardController {

    @Resource
    private MemberCardService memberCardService;

    @GetMapping("/list")
    @Operation(summary = "获得在售会员卡列表")
    public CommonResult<List<MemberCardVO.RespVO>> getOnSaleCards() {
        return success(memberCardService.getOnSaleCards());
    }

    @PostMapping("/buy")
    @Operation(summary = "余额购卡（事务：扣余额→CAS累加已售→写购买记录）")
    // 越权修复（同 P0-3）：userId 由登录态取，余额扣款归属芋道钱包按 userId 定位
    public CommonResult<Long> buyCard(@RequestBody @Valid MemberCardVO.BuyReqVO buyReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberCardService.buyCard(userId, buyReqVO.getCardId()));
    }

    @GetMapping("/my-records")
    @Operation(summary = "我的购卡记录")
    public CommonResult<PageResult<MemberCardVO.OrderRespVO>> getMyRecords(
            @Valid PageParam pageParam) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberCardService.getMyRecords(userId, pageParam));
    }

}
