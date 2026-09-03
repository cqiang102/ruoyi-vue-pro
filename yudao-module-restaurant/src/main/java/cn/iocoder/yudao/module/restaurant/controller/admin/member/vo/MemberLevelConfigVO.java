package cn.iocoder.yudao.module.restaurant.controller.admin.member.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会员等级配置 VO
 *
 * @author 餐饮 SaaS
 */
@Schema(description = "会员等级配置")
public class MemberLevelConfigVO {

    @Schema(description = "等级配置分页 Request VO")
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PageReqVO extends PageParam {
        @Schema(description = "等级名称，模糊匹配")
        private String name;
        @Schema(description = "状态：0-停用 1-启用")
        private Integer status;
    }

    @Schema(description = "创建/更新 Request VO")
    @Data
    public static class SaveReqVO {
        @Schema(description = "等级序号，从 1 开始", example = "1")
        private Integer level;
        @Schema(description = "等级名称", example = "银卡会员")
        private String name;
        @Schema(description = "升级所需成长值门槛", example = "0")
        private Integer growthThreshold;
        @Schema(description = "折扣率，100 表示不打折，95 表示 95 折", example = "100")
        private Integer discountRate;
        @Schema(description = "权益描述")
        private String benefits;
        @Schema(description = "状态：0-停用 1-启用", example = "1")
        private Integer status;
    }

    @Schema(description = "等级配置 Response VO")
    @Data
    public static class RespVO {
        private Long id;
        private Integer level;
        private String name;
        private Integer growthThreshold;
        private Integer discountRate;
        private String benefits;
        private Integer status;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

}
