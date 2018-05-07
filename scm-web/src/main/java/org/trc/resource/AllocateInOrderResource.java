package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.allocateOrder.IAllocateInOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.form.AllocateOrder.AllocateInOrderForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzcyn on 2018/5/4.
 */
@Component
@Path(SupplyConstants.AllocateOutOrder.ROOT)
public class AllocateInOrderResource {

    @Resource
    private IAllocateInOrderBiz allocateInOrderBiz;

    @GET
    @Path(SupplyConstants.AllocateOutOrder.ALLOCATE_IN_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderPage(@BeanParam AllocateInOrderForm form,
                                      @BeanParam Pagenation<AllocateInOrder> page){
        return ResultUtil.createSuccessPageResult(allocateInOrderBiz.allocateInOrderPage(form, page));
    }

    /*@GET
    @Path(SupplyConstants.AllocateOutOrder.ALLOCATE_IN_ORDER_DETAIL)
    @Produces(MediaType.APPLICATION_JSON)
    public Response allocateOrderDetail(@PathParam("allocateInOrderCode") String allocateInOrderCode){
        return ResultUtil.createSuccessPageResult(allocateInOrderBiz.allocateInOrderPage(form, page));
    }*/



}
