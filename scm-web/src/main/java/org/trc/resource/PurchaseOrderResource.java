package org.trc.resource;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
@Component
@Path(SupplyConstants.PurchaseOrder.ROOT)
public class PurchaseOrderResource {

    @Resource
    private IPurchaseOrderBiz purchaseOrderBiz;

    @GET
    @Path(SupplyConstants.PurchaseOrder.PURCHASE_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<PurchaseOrder> purchaseOrderPagenation(){
        return  null;
    }

    @GET
    @Path(SupplyConstants.PurchaseOrder.SUPPLIERS)
    public AppResult<List<Supplier>> findSuppliers(@QueryParam("userId") String userId) throws Exception {
        return ResultUtil.createSucssAppResult("根据用户id查询对应的供应商",purchaseOrderBiz.findSuppliersByUserId(userId));
    }


}
