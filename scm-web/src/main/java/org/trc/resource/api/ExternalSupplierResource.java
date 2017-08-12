package org.trc.resource.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.constants.SupplyConstants;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hzwdx on 2017/6/22.
 */
@Component
@Path(SupplyConstants.ExternalSupplier.ROOT)
public class ExternalSupplierResource {

    @Autowired
    private IGoodsBiz goodsBiz;

    @POST
    @Path(SupplyConstants.ExternalSupplier.SUPPLIER_SKU_UPDATE_NOTICE)
    @Produces("application/json;charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public AppResult supplierSkuUpdateNotice(@FormParam("updateSupplierSkus") String updateSupplierSkus) throws Exception {
        goodsBiz.supplierSkuUpdateNotice(updateSupplierSkus);
        return ResultUtil.createSucssAppResult("供应商sku更新通知成功", "");
    }

}
