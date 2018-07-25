package org.trc.resource;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.trc.biz.purchase.IPurchaseBoxInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseBoxInfo;
import org.trc.domain.purchase.PurchaseBoxInfoVO;
import org.trc.enums.purchase.PurchaseBoxInfoStatusEnum;
import org.trc.util.ResultUtil;

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
    public Response savePurchaseBoxInfo(@BeanParam @Validated PurchaseBoxInfoVO purchaseBoxInfoVO,
                                        @Context ContainerRequestContext requestContext) {
        purchaseBoxInfoBiz.savePurchaseBoxInfo(purchaseBoxInfoVO, PurchaseBoxInfoStatusEnum.UNFINISH.getCode(),
                (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("暂存装箱信息成功","");
    }
}
