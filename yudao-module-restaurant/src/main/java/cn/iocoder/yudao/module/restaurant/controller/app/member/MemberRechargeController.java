package cn.iocoder.yudao.module.restaurant.controller.app.member;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.pay.api.wallet.dto.PayWalletRespDTO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.member.MemberRechargeDO;
import cn.iocoder.yudao.module.restaurant.service.member.MemberRechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MemberRechargeController {

    @Resource
    private MemberRechargeService memberRechargeService;

    @PostMapping("/create")
    @Operation(summary = "创建储值充值单，返回芋道 pay_order.id")
    // 越权修复（同 P0-3）：userId 由登录态取，不信任前端明文入参，杜绝为他人充值/盗用余额
    // userType 同理：消费端钱包固定为「会员」，服务端取值，前端传什么都不采信
    // （历史上前端传 2、会员卡购卡也写死 2，而订单支付用的是 1 → 充值的钱包与扣款的钱包不是同一个，2026-09-21 修复）
    public CommonResult<Long> create(@RequestParam(value = "userType", required = false) Integer userType,
                                     @RequestParam("appKey") String appKey,
                                     @RequestParam("payAmount") Long payAmount,
                                     @RequestParam(value = "giftAmount", required = false, defaultValue = "0") Long giftAmount) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberRechargeService.createRecharge(userId, resolveMemberUserType(userType), appKey, payAmount, giftAmount));
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
    public CommonResult<PayWalletRespDTO> wallet(@RequestParam(value = "userType", required = false) Integer userType) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(memberRechargeService.getWallet(userId, resolveMemberUserType(userType)));
    }

    /**
     * 消费端钱包的 userType 固定为「会员」= {@link UserTypeEnum#MEMBER}（值为 1）。
     *
     * 不再采信前端传入值：历史上前端传 2（注释还写反成「MEMBER=2」），
     * 导致充值写入 (userId, 2) 钱包，而订单余额支付读的是 (userId, 1) 钱包，
     * 两个钱包互不相通——用户充值后仍付不了款。2026-09-21 实测确认后由服务端统一收口。
     */
    private Integer resolveMemberUserType(Integer userType) {
        Integer member = UserTypeEnum.MEMBER.getValue();
        if (userType != null && !member.equals(userType)) {
            log.warn("[resolveMemberUserType][前端传入 userType({}) 与会员类型({}) 不一致，已按会员处理]", userType, member);
        }
        return member;
    }

}
