package cn.iocoder.yudao.module.restaurant.service.invoice;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.invoice.InvoiceDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.invoice.InvoiceTitleDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.order.OrderDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.invoice.InvoiceMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.invoice.InvoiceTitleMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.order.OrderMapper;
import cn.iocoder.yudao.module.restaurant.enums.order.OrderStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;

/**
 * 电子发票 Service（M-35 MVP 壳）
 *
 * <p>线下开具思路（同 M-30 提现）：申请落库快照 → 管理端人工标记已开票/驳回。
 * 申请校验：订单归属本人 + 已支付 + 非取消/退款/退款中 + 未申请过（uk_order_id 兜底）。
 *
 * @author 餐饮 SaaS
 */
@Service
public class InvoiceService {

    @Resource
    private InvoiceMapper invoiceMapper;

    @Resource
    private InvoiceTitleMapper invoiceTitleMapper;

    @Resource
    private OrderMapper orderMapper;

    // ========== 抬头 ==========

    public Long createTitle(Long userId, InvoiceVO.TitleSaveReqVO reqVO) {
        validateTitle(reqVO);
        InvoiceTitleDO title = BeanUtils.toBean(reqVO, InvoiceTitleDO.class);
        title.setId(null);
        title.setUserId(userId);
        if (Integer.valueOf(1).equals(title.getIsDefault())) {
            clearDefault(userId);
        } else {
            title.setIsDefault(0);
        }
        invoiceTitleMapper.insert(title);
        return title.getId();
    }

    public void updateTitle(Long userId, InvoiceVO.TitleSaveReqVO reqVO) {
        if (reqVO.getId() == null || invoiceTitleMapper.selectOwned(reqVO.getId(), userId) == null) {
            throw exception(INVOICE_TITLE_NOT_EXISTS);
        }
        validateTitle(reqVO);
        InvoiceTitleDO title = BeanUtils.toBean(reqVO, InvoiceTitleDO.class);
        title.setUserId(userId); // 归属不可篡改
        if (Integer.valueOf(1).equals(title.getIsDefault())) {
            clearDefault(userId);
        }
        invoiceTitleMapper.updateById(title);
    }

    public void deleteTitle(Long userId, Long id) {
        if (id == null || invoiceTitleMapper.selectOwned(id, userId) == null) {
            throw exception(INVOICE_TITLE_NOT_EXISTS);
        }
        invoiceTitleMapper.deleteById(id);
    }

    public List<InvoiceVO.TitleRespVO> getTitleList(Long userId) {
        return BeanUtils.toBean(invoiceTitleMapper.selectListByUserId(userId), InvoiceVO.TitleRespVO.class);
    }

    private void validateTitle(InvoiceVO.TitleSaveReqVO reqVO) {
        if (Integer.valueOf(1).equals(reqVO.getType()) && (reqVO.getTaxNo() == null || reqVO.getTaxNo().isEmpty())) {
            throw exception(INVOICE_TAX_NO_REQUIRED);
        }
    }

    private void clearDefault(Long userId) {
        InvoiceTitleDO old = invoiceTitleMapper.selectDefaultByUserId(userId);
        if (old != null) {
            InvoiceTitleDO update = new InvoiceTitleDO();
            update.setId(old.getId());
            update.setIsDefault(0);
            invoiceTitleMapper.updateById(update);
        }
    }

    // ========== 开票申请 ==========

    @Transactional(rollbackFor = Exception.class)
    public Long apply(Long userId, InvoiceVO.ApplyReqVO reqVO) {
        // 1. 订单校验：归属本人 + 已支付 + 非取消/退款/退款中
        OrderDO order = orderMapper.selectById(reqVO.getOrderId());
        if (order == null || !Objects.equals(order.getUserId(), userId)
                || !Integer.valueOf(1).equals(order.getPayStatus())
                || OrderStatusEnum.UNPAID.getStatus().equals(order.getStatus())
                || OrderStatusEnum.CANCELED.getStatus().equals(order.getStatus())
                || OrderStatusEnum.REFUNDING.getStatus().equals(order.getStatus())
                || OrderStatusEnum.REFUNDED.getStatus().equals(order.getStatus())) {
            throw exception(INVOICE_ORDER_INVALID);
        }
        // 2. 一单一票（先查后插，uk_order_id 唯一键并发兜底）
        if (invoiceMapper.selectByOrderId(reqVO.getOrderId()) != null) {
            throw exception(INVOICE_ALREADY_APPLIED);
        }
        // 3. 抬头：引用抬头（校验归属）或直接填写
        Integer type = reqVO.getType();
        String title = reqVO.getTitle();
        String taxNo = reqVO.getTaxNo();
        if (reqVO.getTitleId() != null) {
            InvoiceTitleDO t = invoiceTitleMapper.selectOwned(reqVO.getTitleId(), userId);
            if (t == null) {
                throw exception(INVOICE_TITLE_NOT_EXISTS);
            }
            type = t.getType();
            title = t.getTitle();
            taxNo = t.getTaxNo();
        }
        if (type == null || title == null || title.isEmpty()) {
            throw exception(INVOICE_TITLE_NOT_EXISTS);
        }
        // 4. 快照落库
        InvoiceDO invoice = new InvoiceDO();
        invoice.setUserId(userId);
        invoice.setStoreId(order.getStoreId());
        invoice.setOrderId(order.getId());
        invoice.setOrderNo(order.getOrderNo());
        invoice.setAmount(order.getPayPrice());
        invoice.setType(type);
        invoice.setTitle(title);
        invoice.setTaxNo(taxNo);
        invoice.setStatus(0);
        invoiceMapper.insert(invoice);
        return invoice.getId();
    }

    /**
     * 管理端审核：通过 = 线下已开具并标记；驳回 = 必填原因
     */
    public void audit(InvoiceVO.AuditReqVO reqVO) {
        InvoiceDO invoice = invoiceMapper.selectById(reqVO.getId());
        if (invoice == null) {
            throw exception(INVOICE_NOT_EXISTS);
        }
        if (!Integer.valueOf(0).equals(invoice.getStatus())) {
            throw exception(INVOICE_STATUS_INVALID);
        }
        if (Boolean.TRUE.equals(reqVO.getApproved())) {
            invoice.setStatus(1);
            invoice.setInvoiceTime(LocalDateTime.now());
            invoice.setRejectReason(null);
        } else {
            if (reqVO.getRejectReason() == null || reqVO.getRejectReason().isEmpty()) {
                throw exception(INVOICE_REJECT_REASON_REQUIRED);
            }
            invoice.setStatus(2);
            invoice.setRejectReason(reqVO.getRejectReason());
        }
        invoiceMapper.updateById(invoice);
    }

    /**
     * 管理端删除申请记录（清理脏数据，不影响订单）
     */
    public void deleteInvoice(Long id) {
        if (id == null || invoiceMapper.selectById(id) == null) {
            throw exception(INVOICE_NOT_EXISTS);
        }
        invoiceMapper.deleteById(id);
    }

    // ========== 查询 ==========

    public List<InvoiceVO.RespVO> getInvoiceListByUser(Long userId) {
        List<InvoiceDO> list = invoiceMapper.selectList(new LambdaQueryWrapperX<InvoiceDO>()
                .eq(InvoiceDO::getUserId, userId)
                .orderByDesc(InvoiceDO::getId));
        return BeanUtils.toBean(list, InvoiceVO.RespVO.class);
    }

    public List<InvoiceVO.RespVO> getInvoiceList(Long storeId, Integer status) {
        List<InvoiceDO> list = invoiceMapper.selectList(new LambdaQueryWrapperX<InvoiceDO>()
                .eqIfPresent(InvoiceDO::getStoreId, storeId)
                .eqIfPresent(InvoiceDO::getStatus, status)
                .orderByDesc(InvoiceDO::getId));
        return BeanUtils.toBean(list, InvoiceVO.RespVO.class);
    }

}
