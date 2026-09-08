package cn.iocoder.yudao.module.restaurant.controller.app.decor;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.decor.HomeDecorDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.decor.HomeDecorMapper;
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
 * 消费者小程序 - 首页装修（M-02）
 *
 * @author 餐饮 SaaS
 */
@Tag(name = "消费者小程序 - 首页装修")
@RestController
@RequestMapping("/member/home-decor")
@Validated
public class AppHomeDecorController {

    @Resource
    private HomeDecorMapper homeDecorMapper;

    @GetMapping("/list")
    @Operation(summary = "门店装修条目（已启用，按 sort 升序）")
    @Parameter(name = "storeId", description = "门店编号", required = true)
    public CommonResult<List<HomeDecorDO>> getDecorList(@RequestParam("storeId") Long storeId) {
        // 需登录：租户上下文
        SecurityFrameworkUtils.getLoginUserId();
        return success(homeDecorMapper.selectEnabledByStore(storeId));
    }

}
