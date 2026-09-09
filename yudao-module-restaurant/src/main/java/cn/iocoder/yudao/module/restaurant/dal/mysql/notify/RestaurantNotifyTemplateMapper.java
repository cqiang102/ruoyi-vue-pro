package cn.iocoder.yudao.module.restaurant.dal.mysql.notify;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.RestaurantNotifyTemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订阅消息模板 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface RestaurantNotifyTemplateMapper extends BaseMapperX<RestaurantNotifyTemplateDO> {

    /**
     * 查询生效模板：优先本店模板，回退平台默认（storeId=0）
     *
     * @param storeId 门店编号
     * @param scene   场景码
     * @return 模板（本店优先），无则返回 null
     */
    default RestaurantNotifyTemplateDO selectEnabled(Long storeId, String scene) {
        RestaurantNotifyTemplateDO storeTemplate = selectOne(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<RestaurantNotifyTemplateDO>()
                .eq(RestaurantNotifyTemplateDO::getStoreId, storeId)
                .eq(RestaurantNotifyTemplateDO::getScene, scene)
                .eq(RestaurantNotifyTemplateDO::getStatus, 1));
        return storeTemplate != null ? storeTemplate
                : selectOne(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<RestaurantNotifyTemplateDO>()
                .eq(RestaurantNotifyTemplateDO::getStoreId, 0L)
                .eq(RestaurantNotifyTemplateDO::getScene, scene)
                .eq(RestaurantNotifyTemplateDO::getStatus, 1));
    }

}
