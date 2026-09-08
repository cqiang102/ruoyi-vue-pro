package cn.iocoder.yudao.module.restaurant.controller.admin.portal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 我的服务菜单项 VO（M-24）
 *
 * @author 餐饮 SaaS
 */
@Data
public class PortalMenuVO {

    @Schema(description = "菜单项编号", example = "1")
    private Long id;

    @Schema(description = "门店编号（0 = 平台默认）", example = "1")
    private Long storeId;

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "会员储值")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 20, message = "菜单名称不能超过 20 字")
    private String name;

    @Schema(description = "图标（emoji）", example = "💳")
    @Size(max = 16, message = "图标过长")
    private String icon;

    @Schema(description = "跳转路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "/pages/restaurant/recharge")
    @NotBlank(message = "跳转路径不能为空")
    @Size(max = 255, message = "跳转路径过长")
    private String path;

    @Schema(description = "排序（越小越靠前）", example = "1")
    private Integer sort;

    @Schema(description = "状态：0-启用 1-停用", example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

}
