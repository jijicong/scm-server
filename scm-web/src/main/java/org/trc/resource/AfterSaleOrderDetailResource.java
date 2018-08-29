package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.form.afterSale.AfterSaleDetailVO;
import org.trc.util.ResultUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "售后单详情")
@Component
@Path(SupplyConstants.AfterSaleOrder.AFTER_SALE_ORDER_DETAIL)
public class AfterSaleOrderDetailResource {

    @Autowired
    private IAfterSaleOrderBiz afterSaleOrderBiz;

    @GET
    @Path(SupplyConstants.AfterSaleOrder.AFTER_SALE_ORDER_DETAIL_QUERY+"/{id}")
    @ApiOperation(value = "获取售后单详情", response = AfterSaleDetailVO.class)
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderDetail(@ApiParam(value = "售后单主键ID") @PathParam("id") String id){
        return ResultUtil.createSuccessResult("售后单详情查询成功", afterSaleOrderBiz.queryAfterSaleOrderDetail(id));
    }


}
