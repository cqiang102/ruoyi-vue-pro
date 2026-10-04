package cn.iocoder.yudao.module.restaurant.service.ticket;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.ticket.RestaurantTicketDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.ticket.RestaurantTicketMapper;
import cn.iocoder.yudao.module.restaurant.service.store.StoreAuthService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.TICKET_NOT_EXISTS;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.TICKET_STATUS_INVALID;

/**
 * 工单 Service（P-07 平台消息·工单）
 *
 * <p>商户端提交/查看本店工单；平台端查看全部、回复、关闭。
 * 门店归属一律取登录账号绑定的门店（同 M-10 内容模块的做法），不采信前端入参。
 *
 * @author 餐饮 SaaS
 */
@Service
public class RestaurantTicketService {

    @Resource
    private RestaurantTicketMapper ticketMapper;
    @Resource
    private StoreAuthService storeAuthService;

    /** 商户端：提交工单 */
    public Long createTicket(TicketVO.CreateReqVO reqVO) {
        RestaurantTicketDO ticket = BeanUtils.toBean(reqVO, RestaurantTicketDO.class);
        ticket.setStoreId(storeAuthService.getLoginUserStoreId());
        ticket.setStatus(0);
        if (ticket.getType() == null) {
            ticket.setType(4);
        }
        // 回复相关字段由平台端写入，创建时置空，避免前端伪造
        ticket.setReply(null);
        ticket.setReplyUserId(null);
        ticket.setReplyTime(null);
        ticketMapper.insert(ticket);
        return ticket.getId();
    }

    /** 商户端：本店工单分页 */
    public PageResult<RestaurantTicketDO> getMyTicketPage(PageParam pageParam, Integer status) {
        return ticketMapper.selectPage(pageParam, new LambdaQueryWrapperX<RestaurantTicketDO>()
                .eq(RestaurantTicketDO::getStoreId, storeAuthService.getLoginUserStoreId())
                .eqIfPresent(RestaurantTicketDO::getStatus, status)
                .orderByDesc(RestaurantTicketDO::getId));
    }

    /** 平台端：全部工单分页 */
    public PageResult<RestaurantTicketDO> getTicketPage(TicketVO.PageReqVO reqVO) {
        return ticketMapper.selectPage(reqVO, new LambdaQueryWrapperX<RestaurantTicketDO>()
                .eqIfPresent(RestaurantTicketDO::getStoreId, reqVO.getStoreId())
                .eqIfPresent(RestaurantTicketDO::getStatus, reqVO.getStatus())
                .eqIfPresent(RestaurantTicketDO::getType, reqVO.getType())
                .orderByDesc(RestaurantTicketDO::getId));
    }

    /** 平台端：回复（回复后状态置为已回复） */
    public void reply(TicketVO.ReplyReqVO reqVO) {
        RestaurantTicketDO exist = ticketMapper.selectById(reqVO.getId());
        if (exist == null) {
            throw exception(TICKET_NOT_EXISTS);
        }
        if (exist.getStatus() != null && exist.getStatus() == 2) {
            throw exception(TICKET_STATUS_INVALID);
        }
        RestaurantTicketDO updateObj = new RestaurantTicketDO();
        updateObj.setId(reqVO.getId());
        updateObj.setReply(reqVO.getReply());
        updateObj.setStatus(1);
        updateObj.setReplyUserId(SecurityFrameworkUtils.getLoginUserId());
        updateObj.setReplyTime(LocalDateTime.now());
        ticketMapper.updateById(updateObj);
    }

    /** 平台端：关闭 */
    public void close(Long id) {
        RestaurantTicketDO exist = ticketMapper.selectById(id);
        if (exist == null) {
            throw exception(TICKET_NOT_EXISTS);
        }
        RestaurantTicketDO updateObj = new RestaurantTicketDO();
        updateObj.setId(id);
        updateObj.setStatus(2);
        ticketMapper.updateById(updateObj);
    }

}
