package cn.iocoder.yudao.module.restaurant.service.store;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.TableVO;

import java.util.List;

/**
 * 桌台 Service 接口
 *
 * @author 餐饮 SaaS
 */
public interface TableService {

    /**
     * 创建桌台
     */
    Long createTable(TableVO.SaveReqVO createReqVO);

    /**
     * 更新桌台
     */
    void updateTable(TableVO.SaveReqVO updateReqVO);

    /**
     * 删除桌台
     */
    void deleteTable(Long id);

    /**
     * 获得桌台
     */
    TableVO.RespVO getTable(Long id);

    /**
     * 分页查询桌台
     */
    PageResult<TableVO.RespVO> getTablePage(TableVO.PageReqVO pageReqVO);

    /**
     * 获得门店下全部桌台（点餐/下单用）
     */
    List<TableVO.RespVO> getTableSimpleList(Long storeId);

    /**
     * 批量生成桌台
     */
    void generateTables(TableVO.BatchSaveReqVO batchReqVO);

    /**
     * 重新生成落座桌码
     *
     * @param id       桌台编号
     * @param baseUrl  可选；传入则拼成可扫码打开的 H5 链接（如 https://m.xxx.com/）
     * @return 桌码内容
     */
    String regenerateQrcode(Long id, String baseUrl);

}
