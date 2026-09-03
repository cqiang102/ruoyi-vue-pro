package cn.iocoder.yudao.module.restaurant.service.store;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.TableVO;
import cn.iocoder.yudao.module.restaurant.convert.store.TableConvert;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.TableDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.StoreMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.TableMapper;
import cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 桌台 Service 实现类
 *
 * @author 餐饮 SaaS
 */
@Service
@Validated
public class TableServiceImpl implements TableService {

    @Resource
    private TableMapper tableMapper;
    @Resource
    private StoreMapper storeMapper;

    @Override
    public Long createTable(TableVO.SaveReqVO createReqVO) {
        validateStoreExists(createReqVO.getStoreId());
        validateTableNoUnique(createReqVO.getStoreId(), createReqVO.getTableNo(), null);
        TableDO table = new TableDO()
                .setStoreId(createReqVO.getStoreId())
                .setTableNo(createReqVO.getTableNo())
                .setCategory(createReqVO.getCategory())
                .setSeats(createReqVO.getSeats())
                .setStatus(0);
        tableMapper.insert(table);
        // 落座桌码：指向消费者端扫码点餐页（小程序路径 / H5 链接，由 regenerate 时可补前缀）
        table.setQrcodeContent(buildQrcodeContent(table.getStoreId(), table.getId(), null));
        tableMapper.updateById(table);
        return table.getId();
    }

    @Override
    public void updateTable(TableVO.SaveReqVO updateReqVO) {
        TableDO existing = validateTableExists(updateReqVO.getId());
        validateStoreExists(updateReqVO.getStoreId());
        validateTableNoUnique(updateReqVO.getStoreId(), updateReqVO.getTableNo(), updateReqVO.getId());
        existing.setStoreId(updateReqVO.getStoreId())
                .setTableNo(updateReqVO.getTableNo())
                .setCategory(updateReqVO.getCategory())
                .setSeats(updateReqVO.getSeats());
        tableMapper.updateById(existing);
    }

    @Override
    public void deleteTable(Long id) {
        validateTableExists(id);
        tableMapper.deleteById(id);
    }

    @Override
    public TableVO.RespVO getTable(Long id) {
        return TableConvert.convert(tableMapper.selectById(id));
    }

    @Override
    public PageResult<TableVO.RespVO> getTablePage(TableVO.PageReqVO pageReqVO) {
        PageResult<TableDO> page = tableMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<TableDO>()
                        .eqIfPresent(TableDO::getStoreId, pageReqVO.getStoreId())
                        .likeIfPresent(TableDO::getCategory, pageReqVO.getCategory())
                        .eqIfPresent(TableDO::getStatus, pageReqVO.getStatus())
                        .orderByAsc(TableDO::getTableNo));
        return new PageResult<>(convertList(page.getList(), TableConvert::convert), page.getTotal());
    }

    @Override
    public List<TableVO.RespVO> getTableSimpleList(Long storeId) {
        List<TableDO> list = storeId == null
                ? tableMapper.selectList()
                : tableMapper.selectList(TableDO::getStoreId, storeId);
        return convertList(list, TableConvert::convert);
    }

    @Override
    public void generateTables(TableVO.BatchSaveReqVO batchReqVO) {
        validateStoreExists(batchReqVO.getStoreId());
        List<TableDO> tables = new ArrayList<>();
        String prefix = batchReqVO.getPrefix() == null ? "" : batchReqVO.getPrefix();
        for (int no = batchReqVO.getStartNo(); no <= batchReqVO.getEndNo(); no++) {
            String tableNo = prefix + no;
            TableDO existing = tableMapper.selectOne(TableDO::getStoreId, batchReqVO.getStoreId(),
                    TableDO::getTableNo, tableNo);
            if (existing != null) {
                continue; // 跳过已存在的桌号，避免重复
            }
            tables.add(new TableDO()
                    .setStoreId(batchReqVO.getStoreId())
                    .setTableNo(tableNo)
                    .setCategory(batchReqVO.getCategory())
                    .setSeats(batchReqVO.getSeats())
                    .setStatus(0));
        }
        if (!tables.isEmpty()) {
            tableMapper.insertBatch(tables);
            // 批量落座桌码
            tables.forEach(t -> t.setQrcodeContent(buildQrcodeContent(t.getStoreId(), t.getId(), null)));
            tableMapper.updateBatch(tables);
        }
    }

    @Override
    public String regenerateQrcode(Long id, String baseUrl) {
        TableDO table = validateTableExists(id);
        String content = buildQrcodeContent(table.getStoreId(), table.getId(), baseUrl);
        table.setQrcodeContent(content);
        tableMapper.updateById(table);
        return content;
    }

    // ========== 辅助 ==========

    /**
     * 生成落座桌码内容。
     * 纯路径形式（如 pages/restaurant/menu?storeId=1&tableId=2）适用于生成微信小程序码；
     * 传入 baseUrl（如 https://m.xxx.com/）则拼成可直接扫码打开的 H5 链接。
     */
    private String buildQrcodeContent(Long storeId, Long tableId, String baseUrl) {
        String path = "pages/restaurant/menu?storeId=" + storeId + "&tableId=" + tableId;
        if (baseUrl == null || baseUrl.isEmpty()) {
            return path;
        }
        return (baseUrl.endsWith("/") ? baseUrl : baseUrl + "/") + path;
    }

    private TableDO validateTableExists(Long id) {
        TableDO table = tableMapper.selectById(id);
        if (table == null) {
            throw new ServiceException(ErrorCodeConstants.TABLE_NOT_EXISTS);
        }
        return table;
    }

    private void validateStoreExists(Long storeId) {
        StoreDO store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new ServiceException(ErrorCodeConstants.STORE_NOT_EXISTS);
        }
    }

    private void validateTableNoUnique(Long storeId, String tableNo, Long excludeId) {
        TableDO existing = tableMapper.selectOne(TableDO::getStoreId, storeId, TableDO::getTableNo, tableNo);
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw new ServiceException(ErrorCodeConstants.TABLE_NO_DUPLICATE);
        }
    }

}
