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
import java.util.Objects;

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
    @Resource
    private StoreAuthService storeAuthService;

    @Override
    public Long createTable(TableVO.SaveReqVO createReqVO) {
        // 门店端接口：门店归属一律取登录账号绑定的门店，不采信入参 storeId
        // （2026-09-28 实测：原先直接用入参，A 店店员可以在 B 店建桌台）
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateStoreExists(storeId);
        validateTableNoUnique(storeId, createReqVO.getTableNo(), null);
        TableDO table = new TableDO()
                .setStoreId(storeId)
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
        Long storeId = storeAuthService.getLoginUserStoreId();
        // 归属校验 + 不再允许改 storeId（防把桌台搬到别的门店）
        TableDO existing = validateTableInStore(updateReqVO.getId(), storeId);
        validateTableNoUnique(storeId, updateReqVO.getTableNo(), updateReqVO.getId());
        existing.setTableNo(updateReqVO.getTableNo())
                .setCategory(updateReqVO.getCategory())
                .setSeats(updateReqVO.getSeats());
        tableMapper.updateById(existing);
    }

    @Override
    public void deleteTable(Long id) {
        // 2026-09-28 实测：原先只校验"桌台存在"，A 店店员可删除 B 店桌台（已复现并落库）
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateTableInStore(id, storeId);
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
        // 批量生成同样收口到登录门店，不采信入参
        Long storeId = storeAuthService.getLoginUserStoreId();
        validateStoreExists(storeId);
        List<TableDO> tables = new ArrayList<>();
        String prefix = batchReqVO.getPrefix() == null ? "" : batchReqVO.getPrefix();
        for (int no = batchReqVO.getStartNo(); no <= batchReqVO.getEndNo(); no++) {
            String tableNo = prefix + no;
            TableDO existing = tableMapper.selectOne(TableDO::getStoreId, storeId,
                    TableDO::getTableNo, tableNo);
            if (existing != null) {
                continue; // 跳过已存在的桌号，避免重复
            }
            tables.add(new TableDO()
                    .setStoreId(storeId)
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
        Long storeId = storeAuthService.getLoginUserStoreId();
        TableDO table = validateTableInStore(id, storeId);
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

    /**
     * 校验桌台归属：桌台必须存在，且属于当前登录账号绑定的门店。
     * <p>
     * 2026-09-28 实测补充：桌台此前只校验"存在性"，A 店店员可删/改/刷 B 店桌台。
     */
    private TableDO validateTableInStore(Long id, Long storeId) {
        TableDO table = validateTableExists(id);
        if (!Objects.equals(table.getStoreId(), storeId)) {
            throw new ServiceException(ErrorCodeConstants.STORE_STAFF_STORE_MISMATCH);
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
