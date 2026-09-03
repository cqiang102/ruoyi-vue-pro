package cn.iocoder.yudao.module.restaurant.service.pay;

import cn.iocoder.yudao.module.pay.api.order.PayOrderApi;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.api.refund.PayRefundApi;
import cn.iocoder.yudao.module.pay.api.refund.dto.PayRefundCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.refund.dto.PayRefundRespDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 餐饮微信支付 / 退款服务实现
 *
 * @author 餐饮 SaaS
 */
@Service
public class OrderPayServiceImpl implements OrderPayService {

    @Resource
    private PayOrderApi payOrderApi;
    @Resource
    private PayRefundApi payRefundApi;

    @Override
    public Long createWeixinPayOrder(String appKey, String userIp, Long userId, Integer userType,
                                     String merchantOrderId, String subject, String body,
                                     Integer price, LocalDateTime expireTime) {
        PayOrderCreateReqDTO reqDTO = new PayOrderCreateReqDTO();
        reqDTO.setAppKey(appKey);
        reqDTO.setUserIp(userIp);
        reqDTO.setUserId(userId);
        reqDTO.setUserType(userType);
        reqDTO.setMerchantOrderId(merchantOrderId);
        reqDTO.setSubject(subject);
        reqDTO.setBody(body);
        reqDTO.setPrice(price);
        reqDTO.setExpireTime(expireTime);
        return payOrderApi.createOrder(reqDTO);
    }

    @Override
    public PayOrderRespDTO getOrder(Long id) {
        return payOrderApi.getOrder(id);
    }

    @Override
    public Long createRefund(String appKey, String userIp, Long userId, Integer userType,
                             String merchantOrderId, String merchantRefundId, String reason, Integer price) {
        PayRefundCreateReqDTO reqDTO = new PayRefundCreateReqDTO();
        reqDTO.setAppKey(appKey);
        reqDTO.setUserIp(userIp);
        reqDTO.setUserId(userId);
        reqDTO.setUserType(userType);
        reqDTO.setMerchantOrderId(merchantOrderId);
        reqDTO.setMerchantRefundId(merchantRefundId);
        reqDTO.setReason(reason);
        reqDTO.setPrice(price);
        return payRefundApi.createRefund(reqDTO);
    }

    @Override
    public PayRefundRespDTO getRefund(Long id) {
        return payRefundApi.getRefund(id);
    }

}
