package org.trc.resource;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.trc.biz.purchase.IPurchaseBoxInfoBiz;
import org.trc.domain.purchase.PurchaseBoxInfo;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzcyn on 2018/7/25.
 */
@Component
@Path("purchaseBoxInfo")
@Api(value = "装箱薪资")
public class PurchaseBoxInfoResource {

    @Resource
    private IPurchaseBoxInfoBiz purchaseBoxInfoBiz;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response savePurchaseOrder(@BeanParam @Validated PurchaseBoxInfo purchaseBoxInfo, @Context ContainerRequestContext requestContext) {
        return null;
    }
}
