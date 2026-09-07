package cn.iocoder.yudao.module.restaurant.dal.mysql.notify;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.NotifyTemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订阅消息模板 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface NotifyTemplateMapper extends BaseMapperX<NotifyTemplateDO> {

    /**
     * 查询生效模板：优先本店模板，回退平台默认（storeId=0）
     *
     * @param storeId 门店编号
     * @param scene   场景码
     * @return 模板（本店优先），无则返回 null
     */
    default NotifyTemplateDO selectEnabled(Long storeId, String scene) {
        NotifyTemplateDO storeTemplate = selectOne(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<NotifyTemplateDO>()
                .eq(NotifyTemplateDO::getStoreId, storeId)
                .eq(NotifyTemplateDO::getScene, scene)
                .eq(NotifyTemplateDO::getStatus, 1));
        return storeTemplate != null ? storeTemplate
                : selectOne(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<NotifyTemplateDO>()
                .eq(NotifyTemplateDO::getStoreId, 0L)
                .eq(NotifyTemplateDO::getScene, scene)
                .eq(NotifyTemplateDO::getStatus, 1));
    }

}
