package cn.iocoder.yudao.module.restaurant.controller.admin.member.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会员档案 VO（管理后台）
 *
 * @author 餐饮 SaaS
 */
@Schema(description = "会员档案")
public class MemberVO {

    @Schema(description = "会员分页 Request VO")
    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PageReqVO extends PageParam {
        @Schema(description = "会员编号（精确匹配）")
        private Long userId;
    }

    @Schema(description = "会员 Response VO")
    @Data
    public static class RespVO {
        private Long id;
        /**
         * 会员用户编号
         */
        private Long userId;
        /**
         * 当前等级编号
         */
        private Long levelId;
        /**
         * 当前等级名称
         */
        private String levelName;
        /**
         * 当前成长值
         */
        private Integer growthValue;
        /**
         * 积分余额
         */
        private Integer pointBalance;
        /**
         * 累计消费（单位：分）
         */
        private Long totalConsume;
        /**
         * 会员标签（JSON 数组字符串，M-22）
         */
        private String tags;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }

    @Schema(description = "会员标签更新 Request VO")
    @Data
    public static class TagUpdateReqVO {
        /**
         * 会员档案编号（restaurant_member.id）
         */
        @javax.validation.constraints.NotNull(message = "会员编号不能为空")
        private Long id;
        /**
         * 标签（JSON 数组字符串，空串 = 清空全部标签）
         */
        private String tags;
    }

}
