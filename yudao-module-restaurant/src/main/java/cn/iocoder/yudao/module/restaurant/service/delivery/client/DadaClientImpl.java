package cn.iocoder.yudao.module.restaurant.service.delivery.client;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 达达快送（京东秒送）客户端实现
 * <p>
 * 接口事实（达达/京东秒送开放平台）：
 * 1. 通用请求参数：app_key / body(业务JSON字符串) / format=json / source_id(商户编号) /
 *    timestamp(10位) / v=1.0 / signature
 * 2. 签名：参与签名字段（app_key,body,format,source_id,timestamp,v）按 key 字典序做
 *    key+value 拼接，首尾包 app_secret，MD5 后转大写。
 * 3. 发单：POST /api/order/addOrder（shop_no、origin_id、city_code、cargo_price(元)、is_prepay、
 *    receiver 系列字段、callback 等）；取消：POST /api/order/formal/cancel；查询：POST /api/order/status。
 * 4. 响应：{"status":"success","code":0,"msg":"成功","result":{...}}，code=0 成功。
 * 5. 环境：正式 https://newopen.imdada.cn；测试 https://newopen.qa.imdada.cn
 *    （测试商户 source_id=73753，测试门店 shop_no=11047059）。
 * <p>
 * 配置（application.yaml）：yudao.restaurant.dada.app-key / app-secret / source-id / base-url / callback-url。
 *
 * @author 餐饮 SaaS
 */
@Component
@Slf4j
public class DadaClientImpl implements DadaClient {

    @Value("${yudao.restaurant.dada.app-key:}")
    private String appKey;

    @Value("${yudao.restaurant.dada.app-secret:}")
    private String appSecret;

    @Value("${yudao.restaurant.dada.source-id:}")
    private String sourceId;

    /**
     * 正式环境 https://newopen.imdada.cn；联调时改为 https://newopen.qa.imdada.cn
     */
    @Value("${yudao.restaurant.dada.base-url:https://newopen.imdada.cn}")
    private String baseUrl;

    /**
     * 配送状态回调 URL（平台级，发单时传给达达；须公网可达且加入租户 ignore-urls）
     */
    @Value("${yudao.restaurant.dada.callback-url:}")
    private String callbackUrl;

    @Override
    public Map<String, Object> addOrder(String shopNo, String originId, String cityCode, BigDecimal cargoPrice,
                                        Integer isPrepay, String receiverName, String receiverPhone,
                                        String receiverAddress, BigDecimal receiverLat, BigDecimal receiverLng,
                                        String callbackUrl, String info) {
        JSONObject biz = new JSONObject()
                .set("shop_no", shopNo)
                .set("origin_id", originId)
                .set("city_code", cityCode)
                .set("cargo_price", cargoPrice)
                .set("is_prepay", isPrepay)
                .set("receiver_name", receiverName)
                .set("receiver_phone", receiverPhone)
                .set("receiver_address", receiverAddress)
                .set("callback", callbackUrl)
                .set("cargo_type", 1) // 食品小吃
                .set("info", info == null ? "" : info);
        // 收货人经纬度选填：有值才传（达达可按收货地址自行解析）
        if (receiverLat != null && receiverLng != null) {
            biz.set("receiver_lat", receiverLat).set("receiver_lng", receiverLng);
        }
        return request("/api/order/addOrder", biz);
    }

    @Override
    public Map<String, Object> formalCancel(String originId) {
        JSONObject biz = new JSONObject()
                .set("order_id", originId)
                .set("cancel_code", 10006) // 商家主动取消
                .set("cancel_reason", "商家取消订单");
        return request("/api/order/formal/cancel", biz);
    }

    @Override
    public Map<String, Object> queryStatus(String originId) {
        JSONObject biz = new JSONObject().set("order_id", originId);
        return request("/api/order/status", biz);
    }

    /**
     * 通用请求：组通用参数 + 签名 + POST + 解析（code!=0 抛异常）
     */
    private Map<String, Object> request(String path, JSONObject biz) {
        Map<String, String> common = new TreeMap<>();
        common.put("app_key", appKey);
        common.put("body", biz.toString());
        common.put("format", "json");
        common.put("source_id", sourceId);
        common.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        common.put("v", "1.0");
        common.put("signature", sign(common));
        String resp = HttpRequest.post(baseUrl + path)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(common))
                .timeout(8000)
                .execute()
                .body();
        JSONObject json = JSONUtil.parseObj(resp);
        String status = json.getStr("status");
        int code = json.getInt("code", -1);
        if (!"success".equalsIgnoreCase(status) || code != 0) {
            log.warn("[request][达达请求失败 path({}) resp({})]", path, resp);
            throw new RuntimeException("达达请求失败：" + json.getStr("msg", resp));
        }
        return json.getJSONObject("result") == null ? new HashMap<>() : json.getJSONObject("result");
    }

    /**
     * 签名：按 key 字典序 key+value 拼接（body 为业务 JSON 字符串原样参与），首尾包 app_secret，MD5 大写
     */
    private String sign(Map<String, String> params) {
        StringBuilder sb = new StringBuilder(appSecret);
        for (Map.Entry<String, String> e : params.entrySet()) {
            if ("signature".equals(e.getKey()) || e.getValue() == null) {
                continue;
            }
            sb.append(e.getKey()).append(e.getValue());
        }
        sb.append(appSecret);
        return SecureUtil.md5(sb.toString()).toUpperCase();
    }

}
