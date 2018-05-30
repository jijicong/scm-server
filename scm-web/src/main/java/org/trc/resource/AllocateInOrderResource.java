package org.trc.resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.allocateOrder.IAllocateInOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.DistributeLockEnum;
import org.trc.form.AllocateOrder.AllocateInOrderForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;
import org.trc.util.lock.RedisLock;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzcyn on 2018/5/4.
 */
@Component
@Path(SupplyConstants.AllocateInOrder.ROOT)
public class AllocateInOrderResource {

    private Logger logger = LoggerFactory.getLogger(AllocateInOrderResource.class);

    @Resource
    private IAllocateInOrderBiz allocateInOrderBiz;
    @Autowired
    private RedisLock redisLock;

    @GET
    @Path(SupplyConstants.AllocateInOrder.ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderPage(@BeanParam AllocateInOrderForm form,
                                      @BeanParam Pagenation<AllocateInOrder> page){
        return ResultUtil.createSuccessPageResult(allocateInOrderBiz.allocateInOrderPage(form, page));
    }

    @GET
    @Path(SupplyConstants.AllocateInOrder.ORDER_DETAIL + "/{allocateOrderCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderDetail(@PathParam("allocateOrderCode") String allocateOrderCode){
        return ResultUtil.createSuccessResult("查询调拨入库单明细成功", allocateInOrderBiz.queryDetail(allocateOrderCode));
    }

    @PUT
    @Path(SupplyConstants.AllocateInOrder.ORDER_CANCEL + "/{allocateOrderCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderCancel(@PathParam("allocateOrderCode") String allocateOrderCode,@FormParam("flag") String flag, @FormParam("cancelReson") String cancelReson, @Context ContainerRequestContext requestContext){
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        String identifier = "";
        identifier = redisLock.Lock(DistributeLockEnum.ALLOCATE_IN_ORDER.getCode() + "orderCancel" +
                allocateOrderCode, 0, 10000);
        if (StringUtils.isBlank(identifier)) {
            throw new RuntimeException("重复操作！");
        }
        try {
            allocateInOrderBiz.orderCancel(allocateOrderCode, flag, cancelReson, aclUserAccreditInfo);
        } finally {
            try {
                if (redisLock.releaseLock(DistributeLockEnum.ALLOCATE_IN_ORDER.getCode() + "orderCancel" +
                        allocateOrderCode, identifier)) {
                    logger.info("allocateOrderCode:{} 调拨入库通知，解锁成功，identifier:{}", allocateOrderCode, identifier);
                } else {
                    logger.error("allocateOrderCode:{} 调拨入库通知，解锁失败，identifier:{}", allocateOrderCode, identifier);
                }

            } catch (Exception e) {
                logger.error("allocateOrderCode:{} 调拨入库通知，解锁失败，identifier:{}, err:{}",
                        allocateOrderCode, identifier, e.getMessage());
                e.printStackTrace();
            }
        }
        return ResultUtil.createSuccessResult("操作成功", "");
    }

    @PUT
    @Path(SupplyConstants.AllocateInOrder.ORDER_CLOSE + "/{allocateOrderCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderClose(@PathParam("allocateOrderCode") String allocateOrderCode,@FormParam("flag") String flag, @FormParam("cancelReson") String cancelReson, @Context ContainerRequestContext requestContext){
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        allocateInOrderBiz.orderClose(allocateOrderCode, flag, cancelReson, aclUserAccreditInfo);
        return ResultUtil.createSuccessResult("操作成功", "");
    }

    @PUT
    @Path(SupplyConstants.AllocateInOrder.NOTICE_RECIVE_GOODS + "/{allocateOrderCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response noticeReciveGoods(@PathParam("allocateOrderCode") String allocateOrderCode, 
    		@FormParam("flag") String flag, @FormParam("cancelReson") String cancelReson, @Context ContainerRequestContext requestContext){
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        String identifier = "";
        identifier = redisLock.Lock(DistributeLockEnum.ALLOCATE_IN_ORDER.getCode() + "noticeReciveGoods" +
                allocateOrderCode, 0, 10000);
        if (StringUtils.isBlank(identifier)) {
            throw new RuntimeException("重复操作！");
        }
        try {
            allocateInOrderBiz.noticeReciveGoods(allocateOrderCode, aclUserAccreditInfo);
        } finally {
            try {
                if (redisLock.releaseLock(DistributeLockEnum.ALLOCATE_IN_ORDER.getCode() + "noticeReciveGoods" +
                        allocateOrderCode, identifier)) {
                    logger.info("allocateOrderCode:{} 调拨入库通知，解锁成功，identifier:{}", allocateOrderCode, identifier);
                } else {
                    logger.error("allocateOrderCode:{} 调拨入库通知，解锁失败，identifier:{}", allocateOrderCode, identifier);
                }

            } catch (Exception e) {
                logger.error("allocateOrderCode:{} 调拨入库通知，解锁失败，identifier:{}, err:{}",
                        allocateOrderCode, identifier, e.getMessage());
                e.printStackTrace();
            }
        }
        return ResultUtil.createSuccessResult("操作成功", "");
    }


}
