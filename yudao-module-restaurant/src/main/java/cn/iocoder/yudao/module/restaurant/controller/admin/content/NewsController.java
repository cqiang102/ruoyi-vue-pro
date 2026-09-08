package cn.iocoder.yudao.module.restaurant.controller.admin.content;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.restaurant.service.content.NewsService;
import cn.iocoder.yudao.module.restaurant.service.content.NewsVO;
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
 * 管理后台 - 资讯（M-10 内容管理）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "管理后台 - 资讯")
@RestController
@RequestMapping("/store/news")
@Validated
public class NewsController {

    @Resource
    private NewsService newsService;

    @PostMapping("/create")
    @Operation(summary = "发布资讯")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:news:create')")
    public CommonResult<Long> createNews(@Valid @RequestBody NewsVO reqVO) {
        return success(newsService.createNews(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新资讯")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:news:update')")
    public CommonResult<Boolean> updateNews(@Valid @RequestBody NewsVO reqVO) {
        newsService.updateNews(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除资讯")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:news:delete')")
    public CommonResult<Boolean> deleteNews(@RequestParam("id") Long id) {
        newsService.deleteNews(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "资讯详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:news:query')")
    public CommonResult<NewsVO> getNews(@RequestParam("id") Long id) {
        return success(newsService.getNews(id));
    }

    @GetMapping("/list")
    @Operation(summary = "资讯列表（storeId 为空查全部）")
    @PreAuthorize("@ss.hasAnyPermissions('restaurant:news:query')")
    public CommonResult<List<NewsVO>> getNewsList(@RequestParam(value = "storeId", required = false) Long storeId) {
        return success(newsService.getNewsList(storeId));
    }

}
