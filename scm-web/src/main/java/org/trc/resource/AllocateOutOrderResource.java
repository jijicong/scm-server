package org.trc.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.biz.allocateOrder.IAllocateOutOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.AllocateOrder.AllocateOrderForm;
import org.trc.form.AllocateOrder.AllocateOutOrderForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

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
@Path(SupplyConstants.AllocateOutOrder.ROOT)
public class AllocateOutOrderResource {

    private Logger logger = LoggerFactory.getLogger(AllocateOutOrderResource.class);

    @Resource
    private IAllocateOutOrderBiz allocateOutOrderBiz;

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
     * 出库通知
     */
    @PUT
    @Path(SupplyConstants.AllocateOutOrder.ALLOCATE_ORDER_OUT_NOTICE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderOutNotice (@PathParam("id") Long id,@Context ContainerRequestContext requestContext) {
    	return allocateOutOrderBiz.allocateOrderOutNotice(id, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    }
    
    /**
     * 取消出库
     */
    @PUT
    @Path(SupplyConstants.AllocateOutOrder.ALLOCATE_ORDER_OUT_CANCEL + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderCancel(@PathParam("id") Long id, @FormParam("remark") String remark, @Context ContainerRequestContext requestContext){
        return allocateOutOrderBiz.closeOrCancel(id, remark, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO), false);
    }
}
