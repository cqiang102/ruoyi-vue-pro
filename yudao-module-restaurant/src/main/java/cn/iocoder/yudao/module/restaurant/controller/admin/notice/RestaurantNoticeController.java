package cn.iocoder.yudao.module.restaurant.controller.admin.notice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.service.notice.RestaurantNoticeService;
import cn.iocoder.yudao.module.restaurant.service.notice.NoticeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 公告（P-07）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 公告")
@RestController
@RequestMapping("/store/notice")
@Validated
public class RestaurantNoticeController {

    @Resource
    private RestaurantNoticeService noticeService;

    @PostMapping("/create")
    @Operation(summary = "发布公告")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:notice:create')")
    public CommonResult<Long> createNotice(@Valid @RequestBody NoticeVO reqVO) {
        return success(noticeService.createNotice(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新公告")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:notice:update')")
    public CommonResult<Boolean> updateNotice(@Valid @RequestBody NoticeVO reqVO) {
        noticeService.updateNotice(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除公告")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:notice:delete')")
    public CommonResult<Boolean> deleteNotice(@RequestParam("id") Long id) {
        noticeService.deleteNotice(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "公告列表（storeId 为空查全部）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:notice:query')")
    public CommonResult<List<NoticeVO>> getNoticeList(@RequestParam(value = "storeId", required = false) Long storeId) {
        return success(noticeService.getNoticeList(storeId));
    }

}
