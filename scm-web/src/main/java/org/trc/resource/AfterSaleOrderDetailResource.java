package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.constants.SupplyConstants;
import org.trc.util.ResultUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path(SupplyConstants.AfterSaleOrder.AFTER_SALE_ORDER_DETAIL)
public class AfterSaleOrderDetailResource {

    @GET
    @Path(SupplyConstants.AfterSaleOrder.AFTER_SALE_ORDER_DETAIL_QUERY+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderDetail(@PathParam("id") Long id){

        return ResultUtil.createSuccessResult("售后单详情查询成功", null);
    }


}
