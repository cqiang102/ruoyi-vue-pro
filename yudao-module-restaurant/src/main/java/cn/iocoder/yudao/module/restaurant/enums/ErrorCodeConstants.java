package cn.iocoder.yudao.module.restaurant.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Restaurant 错误码枚举类
 * <p>
 * restaurant 系统，使用 2-000-001-000 段
 */
public interface ErrorCodeConstants {

    // ========== 门店认证 2-000-001-000 ==========
    ErrorCode STORE_USER_NOT_BOUND = new ErrorCode(2_000_001_000,
            "该微信未绑定门店账号，请先在商户后台绑定，或在小程序内使用账号密码绑定");

    // ========== 菜品分类 2-000-002-000 ==========
    ErrorCode DISH_CATEGORY_NOT_EXISTS = new ErrorCode(2_000_002_000, "菜品分类不存在");
    ErrorCode DISH_CATEGORY_NAME_DUPLICATE = new ErrorCode(2_000_002_001, "菜品分类名称已存在");

    // ========== 菜品 2-000-003-000 ==========
    ErrorCode DISH_NOT_EXISTS = new ErrorCode(2_000_003_000, "菜品不存在");
    ErrorCode DISH_CATEGORY_NOT_EXISTS_FOR_DISH = new ErrorCode(2_000_003_001, "菜品所属分类不存在");
    ErrorCode DISH_STATUS_CAN_NOT_OFF_SALE = new ErrorCode(2_000_003_002, "菜品已沽清，无法上架，请先取消沽清");

    // ========== 门店 2-000-004-000 ==========
    ErrorCode STORE_NOT_EXISTS = new ErrorCode(2_000_004_000, "门店不存在");
    ErrorCode STORE_STAFF_NOT_BOUND = new ErrorCode(2_000_004_001, "当前账号未绑定门店，无法操作门店端功能");
    ErrorCode STORE_STAFF_STORE_MISMATCH = new ErrorCode(2_000_004_002, "无权操作他店订单/数据");
    ErrorCode STORE_STAFF_NOT_EXISTS = new ErrorCode(2_000_004_003, "门店店员映射不存在");
    ErrorCode STORE_STAFF_DUPLICATE = new ErrorCode(2_000_004_004, "该后台账号已绑定门店，不可重复绑定（一人一店）");

    // ========== 桌台 2-000-005-000 ==========
    ErrorCode TABLE_NOT_EXISTS = new ErrorCode(2_000_005_000, "桌台不存在");
    ErrorCode TABLE_NO_DUPLICATE = new ErrorCode(2_000_005_001, "同一门店下桌号已存在");

    // ========== 订单 2-000-006-000 ==========
    ErrorCode ORDER_NOT_EXISTS = new ErrorCode(2_000_006_000, "订单不存在");
    ErrorCode ORDER_ITEM_NOT_EXISTS = new ErrorCode(2_000_006_001, "订单明细不存在");
    ErrorCode ORDER_STATUS_INVALID = new ErrorCode(2_000_006_002, "当前订单状态不允许该操作");
    ErrorCode ORDER_TABLE_REQUIRED = new ErrorCode(2_000_006_003, "堂食订单必须选择桌台");
    ErrorCode ORDER_EMPTY_ITEMS = new ErrorCode(2_000_006_004, "订单至少需要一个菜品");
    ErrorCode ORDER_NOT_UNPAID = new ErrorCode(2_000_006_005, "订单非待支付状态，无法发起支付");
    ErrorCode ORDER_ITEM_DISH_NOT_EXISTS = new ErrorCode(2_000_006_006, "下单菜品不存在");
    ErrorCode ORDER_REFUND_STATUS_INVALID = new ErrorCode(2_000_006_007, "当前订单状态不可退款（仅已支付/制作中/已完成的订单可退）");
    ErrorCode ORDER_REFUND_APPKEY_MISSING = new ErrorCode(2_000_006_008, "微信支付订单缺少 PayApp 标识，无法发起退款");
    ErrorCode ORDER_DELIVERY_ADDRESS_REQUIRED = new ErrorCode(2_000_006_009, "外卖订单必须填写收货人姓名、电话与地址");
    ErrorCode ORDER_DELIVERY_MIN_AMOUNT = new ErrorCode(2_000_006_010, "外卖订单商品金额未达到门店起送价");
    ErrorCode ORDER_VERIFY_CODE_NOT_FOUND = new ErrorCode(2_000_006_011, "核销码不存在或已失效");
    ErrorCode ORDER_VERIFY_STATUS_INVALID = new ErrorCode(2_000_006_012, "核销失败：订单尚未支付，无法核销");
    ErrorCode ORDER_NOT_OWNER = new ErrorCode(2_000_006_013, "无权操作该订单");
    ErrorCode ORDER_PAY_CALLBACK_INVALID = new ErrorCode(2_000_006_014, "支付回调校验失败：支付单不存在或未支付成功");
    ErrorCode ORDER_PAY_ORDER_MISMATCH = new ErrorCode(2_000_006_015, "回调支付单号与订单不匹配");
    ErrorCode ORDER_ITEM_QUANTITY_INVALID = new ErrorCode(2_000_006_016, "购买数量必须在 1-999 之间");
    ErrorCode ORDER_ADD_ITEMS_STATUS_INVALID = new ErrorCode(2_000_006_017, "仅待支付订单可加菜（请先完成加菜再支付）");
    ErrorCode ORDER_TABLE_NOT_EXISTS = new ErrorCode(2_000_006_018, "桌台不存在");
    ErrorCode ORDER_TABLE_STORE_MISMATCH = new ErrorCode(2_000_006_019, "桌台不属于当前门店");
    ErrorCode ORDER_TABLE_OCCUPIED = new ErrorCode(2_000_006_020, "桌台已被占用，请先结账或更换桌台");
    ErrorCode ORDER_ITEM_DISH_OFF_SHELF = new ErrorCode(2_000_006_021, "菜品已下架，无法下单");
    ErrorCode ORDER_ITEM_DISH_SOLD_OUT = new ErrorCode(2_000_006_022, "菜品已售罄，无法下单");
    ErrorCode ORDER_PRICE_OVERFLOW = new ErrorCode(2_000_006_023, "订单金额超出支付渠道上限");
    ErrorCode ORDER_REFUND_CALLBACK_INVALID = new ErrorCode(2_000_006_024, "退款回调校验失败：退款单不存在或未退款成功");
    ErrorCode ORDER_REFUND_ORDER_MISMATCH = new ErrorCode(2_000_006_025, "回调退款单号与订单不匹配");

    // ========== 会员储值充值 2-000-010-xxx ==========
    // 注意：本段原先误占 2-000-007-001 / 2-000-007-002，与下方「会员档案」段重复，
    //      已迁移至独立的 2-000-010 段。全库均为常量名引用（无数字硬编码），改动安全。
    ErrorCode MEMBER_RECHARGE_AMOUNT_INVALID = new ErrorCode(2_000_010_001, "充值金额非法");
    ErrorCode MEMBER_RECHARGE_NOT_FOUND = new ErrorCode(2_000_010_002, "充值单不存在");

    // ========== 会员档案 2-000-007-000 ==========
    ErrorCode MEMBER_NOT_EXISTS = new ErrorCode(2_000_007_000, "会员不存在");
    ErrorCode MEMBER_LEVEL_NOT_EXISTS = new ErrorCode(2_000_007_001, "会员等级不存在");
    ErrorCode MEMBER_POINT_NOT_ENOUGH = new ErrorCode(2_000_007_002, "会员积分余额不足");
    ErrorCode MEMBER_POINT_CHANGE_FAILED = new ErrorCode(2_000_007_003, "积分变动失败，请重试");

    // ========== 优惠券 2-000-008-000 ==========
    ErrorCode COUPON_TEMPLATE_NOT_EXISTS = new ErrorCode(2_000_008_000, "优惠券模板不存在");
    ErrorCode COUPON_TEMPLATE_SOLD_OUT = new ErrorCode(2_000_008_001, "优惠券已领完");
    ErrorCode COUPON_TEMPLATE_DISABLED = new ErrorCode(2_000_008_002, "优惠券已停用");
    ErrorCode COUPON_LIMIT_REACHED = new ErrorCode(2_000_008_003, "已达到每人限领数量");
    ErrorCode COUPON_NOT_EXISTS = new ErrorCode(2_000_008_004, "优惠券不存在");
    ErrorCode COUPON_STATUS_INVALID = new ErrorCode(2_000_008_005, "优惠券状态不可核销");
    ErrorCode COUPON_THRESHOLD_NOT_MET = new ErrorCode(2_000_008_006, "订单金额未满足优惠券使用门槛");
    ErrorCode COUPON_EXPIRED = new ErrorCode(2_000_008_007, "优惠券已过期");
    ErrorCode COUPON_USER_MISMATCH = new ErrorCode(2_000_008_008, "优惠券不属于当前用户");

    // ========== 租户套餐 / 订阅 2-000-009-000 ==========
    ErrorCode PACKAGE_NOT_EXISTS = new ErrorCode(2_000_009_000, "套餐不存在");
    ErrorCode SUBSCRIPTION_NOT_EXISTS = new ErrorCode(2_000_009_001, "租户订阅不存在");
    ErrorCode SUBSCRIPTION_EXPIRED = new ErrorCode(2_000_009_002, "租户订阅已过期，请续费后使用");
    ErrorCode SUBSCRIPTION_NOT_ACTIVE = new ErrorCode(2_000_009_003, "当前租户无有效订阅，无法开通");
    ErrorCode PACKAGE_STATUS_INVALID = new ErrorCode(2_000_009_004, "套餐已停用，不可订阅");
    ErrorCode PACKAGE_IN_USE = new ErrorCode(2_000_009_005, "该套餐已被租户订阅，不可删除；请先停用");

    // ========== 轮播图 2-000-011-000 ==========
    ErrorCode BANNER_NOT_EXISTS = new ErrorCode(2_000_011_000, "轮播图不存在");

    // ========== 会员收货地址 2-000-012-000（M-23） ==========
    ErrorCode MEMBER_ADDRESS_NOT_EXISTS = new ErrorCode(2_000_012_000, "收货地址不存在");
    ErrorCode MEMBER_ADDRESS_NOT_OWNER = new ErrorCode(2_000_012_001, "无权操作该收货地址");
    ErrorCode MEMBER_ADDRESS_COUNT_LIMIT = new ErrorCode(2_000_012_002, "地址数量已达上限（20 条），请先删除不用的地址");

    // ========== 会员卡 2-000-013-000（M-26） ==========
    ErrorCode MEMBER_CARD_NOT_EXISTS = new ErrorCode(2_000_013_000, "会员卡不存在");
    ErrorCode MEMBER_CARD_NOT_ON_SALE = new ErrorCode(2_000_013_001, "会员卡已下架或价格异常，无法购买");
    ErrorCode MEMBER_CARD_BUY_CONFLICT = new ErrorCode(2_000_013_002, "购卡高峰，请重试");
    ErrorCode MEMBER_CARD_ORDER_NOT_EXISTS = new ErrorCode(2_000_013_003, "购卡记录不存在");

}
