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
 * Created by hzwdx on 2017/7/14.
 */
@Component
@Path(SupplyConstants.Api.Root)
public class ScmApiResource {
    @Autowired
    private IGoodsBiz goodsBiz;

    @POST
    @Path(SupplyConstants.Api.EXTERNAL_ITEM_UPDATE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult supplierSkuUpdateNotice(@FormParam("updateSupplierSkus") String updateSupplierSkus) throws Exception {
        goodsBiz.supplierSkuUpdateNotice(updateSupplierSkus);
        return ResultUtil.createSucssAppResult("供应商sku更新通知成功", "");
    }


}
