package cn.iocoder.yudao.module.restaurant.controller.admin.printer.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 云打印机 VO（M-10）
 *
 * @author 餐饮 SaaS
 */
@Data
public class PrinterVO {

    /**
     * ========== 打印机 ==========
     */

    @Data
    public static class PageReqVO extends PageParam {
        /**
         * 门店编号（服务端按登录店员强制注入，前端无需传）
         */
        private Long storeId;
        /**
         * 状态：1启用 0停用
         */
        private Integer status;
    }

    @Data
    public static class SaveReqVO {
        private Long id;
        /**
         * 打印机名称（如"后厨单"）
         */
        @NotBlank(message = "打印机名称不能为空")
        private String name;
        /**
         * 易联云终端号（纯数字）
         */
        @NotBlank(message = "终端号不能为空")
        @Pattern(regexp = "^\\d{6,20}$", message = "终端号须为 6-20 位数字")
        private String machineCode;
        /**
         * 打印联类型：1 客用单 2 后厨单
         */
        @NotNull(message = "打印联类型不能为空")
        private Integer printType;
        /**
         * 状态：1启用 0停用
         */
        private Integer status;
        private Integer sort;
    }

    @Data
    public static class RespVO {
        private Long id;
        private Long storeId;
        private String name;
        private String machineCode;
        private Integer printType;
        private Integer status;
        private Integer sort;
        private LocalDateTime createTime;
    }

    /**
     * ========== 打印任务 ==========
     */

    @Data
    public static class TaskRespVO {
        private Long id;
        private Long orderId;
        private Long printerId;
        /**
         * 打印机名称（冗余展示）
         */
        private String printerName;
        /**
         * 状态：0待打印 1成功 2失败
         */
        private Integer status;
        private String errorMsg;
        private Integer retryCount;
        private LocalDateTime sendTime;
    }

}
