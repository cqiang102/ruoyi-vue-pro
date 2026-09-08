package cn.iocoder.yudao.module.restaurant.controller.admin.help;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.service.help.HelpDocService;
import cn.iocoder.yudao.module.restaurant.service.help.HelpDocVO;
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
 * 管理后台 - 帮助/关于文档（C-12）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 帮助文档")
@RestController
@RequestMapping("/store/help-doc")
@Validated
public class HelpDocController {

    @Resource
    private HelpDocService helpDocService;

    @PostMapping("/create")
    @Operation(summary = "新增文档")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:help-doc:create')")
    public CommonResult<Long> createHelpDoc(@Valid @RequestBody HelpDocVO reqVO) {
        return success(helpDocService.createHelpDoc(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新文档")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:help-doc:update')")
    public CommonResult<Boolean> updateHelpDoc(@Valid @RequestBody HelpDocVO reqVO) {
        helpDocService.updateHelpDoc(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文档")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:help-doc:delete')")
    public CommonResult<Boolean> deleteHelpDoc(@RequestParam("id") Long id) {
        helpDocService.deleteHelpDoc(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "文档列表（type 为空则全部）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:help-doc:query')")
    public CommonResult<List<HelpDocVO>> getHelpDocList(@RequestParam(value = "type", required = false) Integer type) {
        return success(helpDocService.getHelpDocList(type));
    }

}
