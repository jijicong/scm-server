package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.afterSale.IAfterSaleOrderTabBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.form.afterSale.AfterSaleDetailTabVO;
import org.trc.util.Pagenation;
import org.trc.util.QueryModel;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "售后单卡片")
@Component
@Path(SupplyConstants.AfterSaleOrder.AFTER_SALE_ORDER)
public class AfterSaleOrderTabResource {

    @Autowired
    IAfterSaleOrderTabBiz afterSaleOrderTabBiz;

    /**
     * 根据订单号 查询售后单信息
     *
     * @param scmShopOrderCode
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.AfterSaleOrder.AFTER_SALE_TAB + "/{scmShopOrderCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据店铺订单号,查询售后Tab",response = AfterSaleDetailTabVO.class)
    public Response selectAfterSaleInfo(@ApiParam(value = "主系统订单号") @PathParam("scmShopOrderCode") String scmShopOrderCode, @BeanParam QueryModel form, @BeanParam Pagenation<AfterSaleOrder> page) throws Exception {
        return ResultUtil.createSuccessPageResult(afterSaleOrderTabBiz.queryAfterSaleOrderTabPage(scmShopOrderCode,form,page));
    }

}
