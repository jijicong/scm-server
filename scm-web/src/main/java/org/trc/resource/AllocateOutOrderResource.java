package org.trc.resource;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.allocateOrder.IAllocateOutOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.DistributeLockEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.AllocateOutOrderException;
import org.trc.form.AllocateOrder.AllocateOutOrderForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;
import org.trc.util.lock.RedisLock;

/**
 * Created by hzcyn on 2018/5/4.
 */
@Component
@Path(SupplyConstants.AllocateOutOrder.ROOT)
public class AllocateOutOrderResource {

    private Logger logger = LoggerFactory.getLogger(AllocateOutOrderResource.class);

    @Resource
    private IAllocateOutOrderBiz allocateOutOrderBiz;
    @Autowired
    private RedisLock redisLock;

    @GET
    @Path(SupplyConstants.AllocateOutOrder.ALLOCATE_OUT_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderPage(@BeanParam AllocateOutOrderForm form,
                                      @BeanParam Pagenation<AllocateOutOrder> page){
        return ResultUtil.createSuccessPageResult(allocateOutOrderBiz.allocateOutOrderPage(form, page));
    }

    @GET
    @Path(SupplyConstants.AllocateOutOrder.ALLOCATE_OUT_ORDER + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderDetail(@PathParam("id") Long id){
        return ResultUtil.createSuccessResult("查询调拨入库单详情成功", allocateOutOrderBiz.queryDetail(id));
    }

    /**
     * 关闭
     */
    @PUT
    @Path(SupplyConstants.AllocateOutOrder.CLOSE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response close(@PathParam("id") Long id, @FormParam("remark") String remark, @Context ContainerRequestContext requestContext){
        return allocateOutOrderBiz.closeOrCancel(id, remark, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO), true);
    }

    /**
     * 取消关闭
     */
    @PUT
    @Path(SupplyConstants.AllocateOutOrder.CANCEL_CLOSE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelClose(@PathParam("id") Long id,@Context ContainerRequestContext requestContext) {
        return allocateOutOrderBiz.cancelClose(id, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    }
    
    /**
     * 出库通知，重新出库
     */
    @PUT
    @Path(SupplyConstants.AllocateOutOrder.ALLOCATE_ORDER_OUT_NOTICE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderOutNotice (@PathParam("id") Long id,@Context ContainerRequestContext requestContext) {
        String identifier = "";
        identifier = redisLock.Lock(DistributeLockEnum.ALLOCATE_OUT_ORDER.getCode() + "allocateOrderOutNotice" +
                id, 0, 10000);
        if (StringUtils.isBlank(identifier)) {
            throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, "重复操作！");
        }
        Response res = null;
        try {
            res = allocateOutOrderBiz.allocateOrderOutNotice(id, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        } finally {
            try {
                if (redisLock.releaseLock(DistributeLockEnum.ALLOCATE_OUT_ORDER.getCode() + "allocateOrderOutNotice" +
                        id, identifier)) {
                    logger.info("allocateOutId:{} 调拨出库通知，解锁成功，identifier:{}", id, identifier);
                } else {
                    logger.error("allocateOutId:{} 调拨出库通知，解锁失败，identifier:{}", id, identifier);
                }

            } catch (Exception e) {
                logger.error("allocateOutId:{} 入库通知，解锁失败，identifier:{}, err:{}",
                        id, identifier, e.getMessage());
                e.printStackTrace();
            }
        }
    	return res;
    }
    
    /**
     * 取消出库
     */
    @PUT
    @Path(SupplyConstants.AllocateOutOrder.ALLOCATE_ORDER_OUT_CANCEL + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderCancel(@PathParam("id") Long id, @FormParam("remark") String remark, @Context ContainerRequestContext requestContext){
        String identifier = "";
        identifier = redisLock.Lock(DistributeLockEnum.ALLOCATE_OUT_ORDER.getCode() + "orderCancel" +
                id, 0, 10000);
        if (StringUtils.isBlank(identifier)) {
            throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, "重复操作！");
        }
        Response res = null;
        try {
            res = allocateOutOrderBiz.closeOrCancel(id, remark, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO), false);
        } finally {
            try {
                if (redisLock.releaseLock(DistributeLockEnum.ALLOCATE_OUT_ORDER.getCode() + "orderCancel" +
                        id, identifier)) {
                    logger.info("allocateOutId:{} 取消出库通知，解锁成功，identifier:{}", id, identifier);
                } else {
                    logger.error("allocateOutId:{} 取消出库通知，解锁失败，identifier:{}", id, identifier);
                }

            } catch (Exception e) {
                logger.error("allocateOutId:{} 入库通知，解锁失败，identifier:{}, err:{}",
                        id, identifier, e.getMessage());
                e.printStackTrace();
            }
        }
        return res;
    }

}
