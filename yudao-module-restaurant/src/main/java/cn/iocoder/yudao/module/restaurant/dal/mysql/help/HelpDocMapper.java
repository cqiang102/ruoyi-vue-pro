package cn.iocoder.yudao.module.restaurant.dal.mysql.help;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.help.HelpDocDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 帮助/关于文档 Mapper
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface HelpDocMapper extends BaseMapperX<HelpDocDO> {

    default List<HelpDocDO> selectEnabledByType(Integer type) {
        return selectList(new LambdaQueryWrapperX<HelpDocDO>()
                .eq(HelpDocDO::getType, type)
                .eq(HelpDocDO::getStatus, 0)
                .orderByAsc(HelpDocDO::getSort));
    }

}
