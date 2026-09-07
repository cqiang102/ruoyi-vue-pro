package cn.iocoder.yudao.module.restaurant.dal.dataobject.notify;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 订阅消息发送记录 DO（M-12）
 * <p>
 * 作用：① 排查「用户说没收到」的凭据（含 openid 与失败原因）；
 *      ② 芋道 sendWxaSubscribeMessage 在模板缺失/openid 缺失时只 warn 不抛异常，
 *         微信侧错误才抛异常，故成功与否必须落库自证；
 *      ③ 便于后续做「同一订单同一场景不重复推送」的幂等判断。
 *
 * @author 餐饮 SaaS
 */
@TableName("restaurant_notify_record")
@Data
@EqualsAndHashCode(callSuper = true)
public class NotifyRecordDO extends TenantBaseDO {

    /**
     * 记录编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 门店编号
     */
    private Long storeId;
    /**
     * 会员编号（MemberUserDO.id）
     */
    private Long userId;
    /**
     * 会员微信 openid（小程序）
     */
    private String openid;
    /**
     * 订单编号（可空，支付/出餐/退款场景均带订单）
     */
    private Long orderId;
    /**
     * 场景码：pay_success / meal_ready / refund_success
     */
    private String scene;
    /**
     * 使用的模板标题
     */
    private String templateTitle;
    /**
     * 跳转页面
     */
    private String page;
    /**
     * 实际发送的模板内容 JSON（含字段名与值）
     */
    private String content;
    /**
     * 状态：0待发送 1成功 2失败
     */
    private Integer status;
    /**
     * 失败原因（微信 errcode/errmsg 或本地异常信息）
     */
    private String errorMsg;
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

}
