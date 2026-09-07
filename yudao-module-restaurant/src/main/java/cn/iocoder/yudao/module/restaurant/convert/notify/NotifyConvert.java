package cn.iocoder.yudao.module.restaurant.convert.notify;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.notify.vo.NotifyVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.NotifyRecordDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.notify.NotifyTemplateDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 订阅消息 Convert（M-12）
 *
 * @author 餐饮 SaaS
 */
@Mapper
public interface NotifyConvert {

    NotifyConvert INSTANCE = Mappers.getMapper(NotifyConvert.class);

    NotifyVO.TemplateRespVO convert(NotifyTemplateDO bean);

    List<NotifyVO.TemplateRespVO> convertTemplateList(List<NotifyTemplateDO> list);

    default PageResult<NotifyVO.TemplateRespVO> convertTemplatePage(PageResult<NotifyTemplateDO> pageResult) {
        return new PageResult<>(convertTemplateList(pageResult.getList()), pageResult.getTotal());
    }

    NotifyVO.RecordRespVO convert(NotifyRecordDO bean);

    List<NotifyVO.RecordRespVO> convertRecordList(List<NotifyRecordDO> list);

    default PageResult<NotifyVO.RecordRespVO> convertRecordPage(PageResult<NotifyRecordDO> pageResult) {
        return new PageResult<>(convertRecordList(pageResult.getList()), pageResult.getTotal());
    }

}
