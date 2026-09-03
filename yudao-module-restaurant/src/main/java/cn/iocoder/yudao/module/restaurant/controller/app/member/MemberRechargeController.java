package cn.iocoder.yudao.module.restaurant.controller.app.member;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.pay.api.wallet.dto.PayWalletRespDTO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberRechargeDO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberRechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 消费者端 - 会员储值充值
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "消费者 - 会员储值充值")
@RestController
@RequestMapping("/member/recharge")
@Validated
public class MemberRechargeController {

    @Resource
    private MemberRechargeService memberRechargeService;

    @PostMapping("/create")
    @Operation(summary = "创建储值充值单，返回芋道 pay_order.id")
    // 越权修复（同 P0-3）：userId 由登录态取，不信任前端明文入参，杜绝为他人充值/盗用余额
    public CommonResult<Long> create(@RequestParam("userType") Integer userType,
                                     @RequestParam("appKey") String appKey,
                                     @RequestParam("payAmount") Long payAmount,
                                     @RequestParam(value = "giftAmount", required = false, defaultValue = "0") Long giftAmount) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberRechargeService.createRecharge(userId, userType, appKey, payAmount, giftAmount));
    }

    @GetMapping("/get")
    @Operation(summary = "充值单详情")
    // P2-J：从登录态取 userId 传给服务层做归属校验，杜绝跨用户查看他人充值单
    public CommonResult<MemberRechargeDO> get(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberRechargeService.getRecharge(id, userId));
    }

    @GetMapping("/page")
    @Operation(summary = "我的充值记录分页")
    public CommonResult<PageResult<MemberRechargeDO>> page(@Validated PageParam pageReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberRechargeService.getRechargePage(userId, pageReqVO));
    }

    @GetMapping("/wallet")
    @Operation(summary = "获取会员钱包余额")
    public CommonResult<PayWalletRespDTO> wallet(@RequestParam("userType") Integer userType) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberRechargeService.getWallet(userId, userType));
    }

}
