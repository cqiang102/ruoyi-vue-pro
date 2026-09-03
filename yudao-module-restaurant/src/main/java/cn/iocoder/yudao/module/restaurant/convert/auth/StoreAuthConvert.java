package cn.iocoder.yudao.module.restaurant.convert.auth;

import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.auth.vo.StoreAuthLoginRespVO;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StoreAuthConvert {

    StoreAuthConvert INSTANCE = Mappers.getMapper(StoreAuthConvert.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    StoreAuthLoginRespVO convert(OAuth2AccessTokenRespDTO token, AdminUserRespDTO user);

}
