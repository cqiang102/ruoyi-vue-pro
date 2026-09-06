package cn.iocoder.yudao.module.restaurant.service.delivery.client;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 达达快送客户端抽象（M-11）
 *
 * @author 餐饮 SaaS
 */
public interface DadaClient {

    /**
     * 发单（直接下单）
     *
     * @param shopNo         达达门店编号
     * @param originId       第三方幂等单号（= 餐饮订单号）
     * @param cityCode       城市 code
     * @param cargoPrice     订单金额（元）
     * @param isPrepay       是否垫付 1/0
     * @param receiverName   收货人
     * @param receiverPhone  收货人手机号
     * @param receiverAddress 收货地址
     * @param receiverLat    纬度（高德）
     * @param receiverLng    经度（高德）
     * @param callbackUrl    状态回调 URL
     * @param info           备注（可空）
     * @return 达达响应 body（含 fee 运费、distance 距离等）；失败抛 RuntimeException
     */
    Map<String, Object> addOrder(String shopNo, String originId, String cityCode, BigDecimal cargoPrice,
                                 Integer isPrepay, String receiverName, String receiverPhone,
                                 String receiverAddress, BigDecimal receiverLat, BigDecimal receiverLng,
                                 String callbackUrl, String info);

    /**
     * 商家取消运单（原因固定为商家主动取消）
     *
     * @param originId 第三方幂等单号
     * @return 达达响应 body；失败抛 RuntimeException
     */
    Map<String, Object> formalCancel(String originId);

    /**
     * 查询运单状态（补回调丢失）
     *
     * @param originId 第三方幂等单号
     * @return 达达响应 body；失败抛 RuntimeException
     */
    Map<String, Object> queryStatus(String originId);

}
