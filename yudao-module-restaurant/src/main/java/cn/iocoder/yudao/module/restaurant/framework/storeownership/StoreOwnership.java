package cn.iocoder.yudao.module.restaurant.framework.storeownership;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 门店归属守卫注解（门店端 /store/** 写接口专用）
 *
 * <p>背景（2026-09-28 横向越权排查）：本模块先后在 10 个域发现同一类缺陷——
 * 写方法只做"存在性"校验、不校验门店归属，导致 A 店店员可以删/改/搬 B 店的数据
 * （桌台、我的服务、预约规则、首页装修、内容管理、公告、提现账户、发票、积分兑换、规格跨菜品）。
 *
 * <p>逐个域补校验的写法容易被后续新增接口漏掉，故抽出本注解 + 切面统一兜底：
 * 标在 Controller 的写方法上，切面会在方法执行前自动完成两件事：
 * <ol>
 *   <li>{@link #idParam()}：按该参数取对象，校验其 store_id 是否等于登录账号绑定的门店，
 *       不符则抛 {@code STORE_STAFF_STORE_MISMATCH}（2_000_004_002 无权操作他店订单/数据）</li>
 *   <li>{@link #injectStoreId()}：把入参对象里的 storeId 覆盖为登录门店（防"代建"和"跨店搬移"）</li>
 * </ol>
 *
 * <p>注意：注解只对**有 store_id 列**的实体有意义。菜品/轮播/优惠券模板等
 * 是租户级共享（表里没有 store_id），不要标。
 *
 * @author 餐饮 SaaS
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StoreOwnership {

    /**
     * 目标实体（DO 类），用于定位表名与主键，例如 {@code TableDO.class}
     */
    Class<?> entity();

    /**
     * 入参中"对象编号"的参数名；为空表示本方法按"创建"语义处理（不校验既有对象）。
     * <p>支持 {@code @RequestParam("id")} 形式与普通形参名。
     */
    String idParam() default "id";

    /**
     * 是否把入参对象（及其 storeId 属性）强制覆盖为登录账号绑定的门店。
     * <p>用于 create / 批量生成 类接口，以及 update 类接口防跨店搬移。
     */
    boolean injectStoreId() default false;

}
