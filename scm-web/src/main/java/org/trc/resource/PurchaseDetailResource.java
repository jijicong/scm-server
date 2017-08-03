package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseDetailBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.purchase.PurchaseDetail;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 采购订单明细
 * Created by sone on 2017/6/20.
 */
@Component
@Path(SupplyConstants.PurchaseDetail.ROOT)
public class PurchaseDetailResource {

    @Resource
    private IPurchaseDetailBiz iPurchaseDetailBiz;

    @GET
    @Path(SupplyConstants.PurchaseDetail.PURCHASE_DETAIL)
    @Produces(MediaType.APPLICATION_JSON)
    public List<PurchaseDetail> purchaseDetailList(@QueryParam("purchaseId") Long purchaseId)throws Exception{
        //"根据采购单的id，查询采购明细成功",
        return iPurchaseDetailBiz.purchaseDetailList(purchaseId);

    }

    @GET
    @Path(SupplyConstants.PurchaseDetail.PURCHASE_DETAILE_BY_CODE)
    @Produces(MediaType.APPLICATION_JSON)
    public List<PurchaseDetail> purchaseDetailListByPurchaseCode(@QueryParam("purchaseOrderCode") String purchaseOrderCode)throws Exception{
        //"根据采购单的id，查询采购明细成功",
        return iPurchaseDetailBiz.purchaseDetailListByPurchaseCode(purchaseOrderCode);

    }


}
