package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.allocateOrder.IAllocateInOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.AllocateOrder.AllocateInOrderForm;
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
@Path(SupplyConstants.AllocateInOrder.ROOT)
public class AllocateInOrderResource {

    @Resource
    private IAllocateInOrderBiz allocateInOrderBiz;

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
    public Response allocateOrderDetail(@PathParam("allocateOrderCode") String allocateOrderCode){
        return ResultUtil.createSuccessResult("查询调拨入库单明细成功", allocateInOrderBiz.queryDetail(allocateOrderCode));
    }

    @PUT
    @Path(SupplyConstants.AllocateInOrder.ORDER_CANCEL + "/{allocateOrderCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderCancel(@PathParam("allocateOrderCode") String allocateOrderCode,@FormParam("flag") String flag, @FormParam("cancelReson") String cancelReson, @Context ContainerRequestContext requestContext){
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        allocateInOrderBiz.orderCancel(allocateOrderCode, flag, cancelReson, aclUserAccreditInfo);
        return ResultUtil.createSuccessResult("查询调拨入库单明细成功", "");
    }




}
