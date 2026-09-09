package cn.iocoder.yudao.module.restaurant.dal.dataobject.notify;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订阅消息模板 DO（M-12 微信订阅消息）
 * <p>
 * 关键设计：芋道 {@code SocialClientApi#sendWxaSubscribeMessage} 是按「模板标题」匹配
 * 微信公众平台的模板 ID（priTmplId），因此这里存 <b>templateTitle（模板标题）</b> 而非模板 ID——
 * 模板 ID 由芋道 social 模块实时从微信拉取，避免我们这边硬编码失效。
 * <p>
 * storeId = 0 表示平台级默认模板（所有门店共用），门店可配置自己的覆盖模板。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_notify_template")
@Data
@EqualsAndHashCode(callSuper = true)
public class RestaurantNotifyTemplateDO extends TenantBaseDO {

    /**
     * 模板编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店编号（0 = 平台默认模板）
     */
    private Long storeId;
    /**
     * 场景码：pay_success 支付成功 / meal_ready 出餐完成 / refund_success 退款成功
     */
    private String scene;
    /**
     * 微信订阅消息模板标题（与微信公众平台「订阅消息-我的模板」中的标题完全一致）
     */
    private String templateTitle;
    /**
     * 关键词映射 JSON：{"character_string1":"订单号","thing2":"门店名称","amount3":"金额","time4":"时间"}
     * key 为模板字段名，value 为该字段的业务含义（仅备注用，实际发送值由代码按场景组装）
     */
    private String keywordConfig;
    /**
     * 点击卡片跳转的小程序页面（如 pages/order/detail?id=1，留空则不跳转）
     */
    private String page;
    /**
     * 状态：1启用 0停用
     */
    private Integer status;

}
