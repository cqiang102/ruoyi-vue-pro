package cn.iocoder.yudao.module.restaurant.controller.app.notice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.service.notice.NoticeService;
import cn.iocoder.yudao.module.restaurant.service.notice.NoticeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 消费者小程序 - 公告（P-07）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "消费者小程序 - 公告")
@RestController
@RequestMapping("/member/notice")
@Validated
public class AppNoticeController {

    @Resource
    private NoticeService noticeService;

    @GetMapping("/list")
    @Operation(summary = "公告列表（本店 + 全平台，已发布）")
    @Parameter(name = "storeId", description = "门店编号", required = true)
    public CommonResult<List<NoticeVO>> getNoticeList(@RequestParam("storeId") Long storeId) {
        // 需登录：租户上下文
        SecurityFrameworkUtils.getLoginUserId();
        return success(noticeService.getNoticeListForMember(storeId));
    }

}
