package cn.iocoder.yudao.module.restaurant.controller.admin.store.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 门店店员映射 VO
 * <p>
 * P1-A 闭环：管理后台通过本 VO 维护 admin 账号 ↔ 门店 的归属映射，
 * 让 m14 跑完后 StoreAuthService.getLoginUserStoreId() 能正确取到门店编号。
 *
 * @author 餐饮 SaaS
 */
public class StoreStaffVO {

    @Schema(description = "门店店员分页 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class PageReqVO extends PageParam {

        @Schema(description = "后台账号编号", example = "100")
        private Long adminUserId;

        @Schema(description = "门店编号", example = "1")
        private Long storeId;

    }

    @Schema(description = "门店店员保存 Request VO", requiredMode = Schema.RequiredMode.REQUIRED)
    @Data
    public static class SaveReqVO {

        @Schema(description = "主键（更新时必填）", example = "1")
        private Long id;

        @Schema(description = "后台账号编号（system_users.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
        @NotNull(message = "后台账号编号不能为空")
        private Long adminUserId;

        @Schema(description = "门店编号（restaurant_store.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "门店编号不能为空")
        private Long storeId;

    }

    @Schema(description = "门店店员 Response VO")
    @Data
    public static class RespVO {

        @Schema(description = "主键", example = "1")
        private Long id;

        @Schema(description = "后台账号编号", example = "100")
        private Long adminUserId;

        @Schema(description = "门店编号", example = "1")
        private Long storeId;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;

        @Schema(description = "更新时间")
        private LocalDateTime updateTime;

    }

}
