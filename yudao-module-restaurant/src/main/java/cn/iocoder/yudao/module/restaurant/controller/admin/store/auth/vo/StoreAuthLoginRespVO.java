package cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoreAuthLoginRespVO {

    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户昵称
     */
    private String nickname;

}
