package cn.iocoder.yudao.module.restaurant.service.pay;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.module.pay.api.wallet.PayWalletApi;
import cn.iocoder.yudao.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import cn.iocoder.yudao.module.pay.api.wallet.dto.PayWalletRespDTO;
import cn.iocoder.yudao.module.pay.enums.wallet.PayWalletBizTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 餐饮余额支付服务实现
 *
 * @author 餐饮 SaaS
 */
@Service
public class WalletPayServiceImpl implements WalletPayService {

    @Resource
    private PayWalletApi payWalletApi;

    @Override
    public PayWalletRespDTO getWallet(Long userId, Integer userType) {
        return payWalletApi.getOrCreateWallet(userId, userType);
    }

    @Override
    public void recharge(Long userId, Integer userType, String bizId, Integer price) {
        addBalance(userId, userType, bizId, PayWalletBizTypeEnum.RECHARGE, price);
    }

    @Override
    public void consume(Long userId, Integer userType, String bizId, Integer price) {
        // 余额扣减：price 入参为正数，内部以负数写入钱包
        addBalance(userId, userType, bizId, PayWalletBizTypeEnum.PAYMENT, -price);
    }

    @Override
    public void refund(Long userId, Integer userType, String bizId, Integer price) {
        // 退款回滚余额：price 入参为正数
        addBalance(userId, userType, bizId, PayWalletBizTypeEnum.PAYMENT_REFUND, price);
    }

    private void addBalance(Long userId, Integer userType, String bizId,
                            PayWalletBizTypeEnum bizType, Integer price) {
        PayWalletAddBalanceReqDTO reqDTO = new PayWalletAddBalanceReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setUserType(userType);
        reqDTO.setBizType(bizType.getType());
        reqDTO.setBizId(bizId);
        reqDTO.setPrice(price);
        payWalletApi.addWalletBalance(reqDTO);
    }

}
