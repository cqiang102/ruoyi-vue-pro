package cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class StoreAuthBindReqVO {

    @NotNull(message = "社交类型不能为空")
    @InEnum(value = SocialTypeEnum.class, message = "社交类型必须是 {value}")
    private Integer socialType;

    @NotEmpty(message = "授权码不能为空")
    private String code;

    private String state;

    @NotEmpty(message = "账号不能为空")
    private String username;

    @NotEmpty(message = "密码不能为空")
    private String password;

}
