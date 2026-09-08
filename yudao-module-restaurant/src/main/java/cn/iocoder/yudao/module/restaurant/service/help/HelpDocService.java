package cn.iocoder.yudao.module.restaurant.service.help;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.help.HelpDocDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.help.HelpDocMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.HELP_DOC_NOT_EXISTS;

/**
 * 帮助/关于文档 Service（C-12）
 *
 * @author 餐饮 SaaS
 */
@Service
public class HelpDocService {

    @Resource
    private HelpDocMapper helpDocMapper;

    public Long createHelpDoc(HelpDocVO reqVO) {
        HelpDocDO doc = BeanUtils.toBean(reqVO, HelpDocDO.class);
        if (doc.getStatus() == null) {
            doc.setStatus(0);
        }
        if (doc.getSort() == null) {
            doc.setSort(0);
        }
        helpDocMapper.insert(doc);
        return doc.getId();
    }

    public void updateHelpDoc(HelpDocVO reqVO) {
        validateExists(reqVO.getId());
        helpDocMapper.updateById(BeanUtils.toBean(reqVO, HelpDocDO.class));
    }

    public void deleteHelpDoc(Long id) {
        validateExists(id);
        helpDocMapper.deleteById(id);
    }

    public HelpDocVO getHelpDoc(Long id) {
        return BeanUtils.toBean(helpDocMapper.selectById(id), HelpDocVO.class);
    }

    /**
     * 后台列表：type 为空则全部
     */
    public List<HelpDocVO> getHelpDocList(Integer type) {
        List<HelpDocDO> list;
        if (type == null) {
            list = helpDocMapper.selectList();
        } else {
            list = helpDocMapper.selectList(
                    new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<HelpDocDO>()
                            .eq(HelpDocDO::getType, type)
                            .orderByAsc(HelpDocDO::getSort));
        }
        return BeanUtils.toBean(list, HelpDocVO.class);
    }

    /**
     * 会员端：只返回启用项
     */
    public List<HelpDocVO> getHelpDocListForMember(Integer type) {
        return BeanUtils.toBean(helpDocMapper.selectEnabledByType(type), HelpDocVO.class);
    }

    private void validateExists(Long id) {
        if (id == null || helpDocMapper.selectById(id) == null) {
            throw exception(HELP_DOC_NOT_EXISTS);
        }
    }

}
