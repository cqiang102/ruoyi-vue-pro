package cn.iocoder.yudao.module.restaurant.service.ticket;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 工单 VO（P-07 平台消息·工单）
 *
 * @author 餐饮 SaaS
 */
public class TicketVO {

    @Schema(description = "提交工单 ReqVO（商户端）")
    @Data
    public static class CreateReqVO {

        @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "收银台打印不出小票")
        @NotBlank(message = "标题不能为空")
        private String title;

        @Schema(description = "正文", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "描述不能为空")
        private String content;

        @Schema(description = "类型：1-功能建议 2-故障报修 3-结算咨询 4-其他", example = "2")
        private Integer type;

    }

    @Schema(description = "工单分页 ReqVO")
    @Data
    public static class PageReqVO extends PageParam {

        @Schema(description = "状态：0-待处理 1-已回复 2-已关闭")
        private Integer status;

        @Schema(description = "类型：1-功能建议 2-故障报修 3-结算咨询 4-其他")
        private Integer type;

        @Schema(description = "门店编号（平台端筛选用；商户端忽略）")
        private Long storeId;

    }

    @Schema(description = "回复工单 ReqVO（平台端）")
    @Data
    public static class ReplyReqVO {

        @Schema(description = "工单编号", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "工单编号不能为空")
        private Long id;

        @Schema(description = "回复内容", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "回复内容不能为空")
        private String reply;

    }

}
