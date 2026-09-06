package cn.iocoder.yudao.module.restaurant.service.print.client;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 易联云云打印客户端（自有型应用，client_credentials 模式）
 * <p>
 * 接口事实（易联云开放平台）：
 * 1. token：POST https://open-api.10ss.net/oauth/oauth
 *    参数 client_id / grant_type=client_credentials / sign / scope=all / timestamp(10位) / id(UUID4)
 *    sign = md5(client_id + timestamp + client_secret) 小写；多台打印机共用一个 access_token，
 *    token 无失效时间但 20 次/日上限，必须缓存复用。
 * 2. 打印：POST https://open-api.10ss.net/print/index
 *    参数 client_id / access_token / machine_code / content(urlencode) / origin_id / sign / id / timestamp
 *    响应 {"error":"0","error_description":"success","body":{...}}；error!=0 视为失败。
 * 3. origin_id：≤32 位字母数字，同一 client_id 下唯一（天然幂等：重复提交不重复出纸）。
 * <p>
 * 配置（application-*.yaml）：yudao.restaurant.yly.client-id / client-secret，
 * 平台级配置（自有型应用），所有租户的打印机共用同一应用。
 *
 * @author 餐饮 SaaS
 */
@Component
@Slf4j
public class YilianyunPrintClient implements PrintClient {

    private static final String OAUTH_URL = "https://open-api.10ss.net/oauth/oauth";
    private static final String PRINT_URL = "https://open-api.10ss.net/print/index";

    @Value("${yudao.restaurant.yly.client-id:}")
    private String clientId;

    @Value("${yudao.restaurant.yly.client-secret:}")
    private String clientSecret;

    /**
     * token 内存缓存（易联云 access_token 无失效时间，20 次/日刷新上限，必须复用；
     * MVP 单机内存缓存，多实例部署时改入 Redis）
     */
    private volatile String cachedAccessToken;

    @Override
    public String print(String machineCode, String content, String originId) {
        if (StrUtil.isBlank(clientId) || StrUtil.isBlank(clientSecret)) {
            return "易联云未配置（yudao.restaurant.yly.client-id / client-secret）";
        }
        try {
            JSONObject body = new JSONObject()
                    .set("client_id", clientId)
                    .set("access_token", getAccessToken())
                    .set("machine_code", machineCode)
                    .set("content", content)
                    .set("origin_id", originId)
                    .set("sign", sign())
                    .set("id", UUID4())
                    .set("timestamp", nowSeconds());
            String resp = HttpRequest.post(PRINT_URL).form(body).timeout(8000).execute().body();
            JSONObject json = JSONUtil.parseObj(resp);
            if (!"0".equals(json.getStr("error"))) {
                String msg = json.getStr("error_description", "unknown");
                log.warn("[print][易联云打印失败 machineCode({}) originId({}) error={}]", machineCode, originId, resp);
                return msg;
            }
            return null;
        } catch (Exception e) {
            log.error("[print][易联云打印异常 machineCode({}) originId({})]", machineCode, originId, e);
            return "网络异常：" + e.getMessage();
        }
    }

    /**
     * 获取 access_token（应用级，多台打印机共用；带内存缓存，20 次/日上限）
     */
    private String getAccessToken() {
        if (cachedAccessToken != null) {
            return cachedAccessToken;
        }
        synchronized (this) {
            if (cachedAccessToken != null) {
                return cachedAccessToken;
            }
            JSONObject body = new JSONObject()
                    .set("client_id", clientId)
                    .set("grant_type", "client_credentials")
                    .set("sign", sign())
                    .set("scope", "all")
                    .set("id", UUID4())
                    .set("timestamp", nowSeconds());
            String resp = HttpRequest.post(OAUTH_URL).form(body).timeout(8000).execute().body();
            JSONObject json = JSONUtil.parseObj(resp);
            if (!"0".equals(json.getStr("error"))) {
                throw new IllegalStateException("易联云获取 access_token 失败：" + resp);
            }
            cachedAccessToken = json.getJSONObject("body").getStr("access_token");
            return cachedAccessToken;
        }
    }

    /**
     * 签名：md5(client_id + timestamp + client_secret) 小写
     */
    private String sign() {
        return SecureUtil.md5(clientId + nowSeconds() + clientSecret);
    }

    private static String UUID4() {
        return UUID.randomUUID().toString();
    }

    private static long nowSeconds() {
        return System.currentTimeMillis() / 1000;
    }

}
