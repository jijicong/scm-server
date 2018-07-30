package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseBoxInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseBoxInfo;
import org.trc.domain.purchase.PurchaseBoxInfoVO;
import org.trc.enums.purchase.PurchaseBoxInfoStatusEnum;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzcyn on 2018/7/25.
 */
@Component
@Path("purchaseBoxInfo")
@Api(value = "装箱信息")
public class PurchaseBoxInfoResource {

    @Resource
    private IPurchaseBoxInfoBiz purchaseBoxInfoBiz;

    @POST
    @Path("save")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "暂存装箱信息")
    public Response savePurchaseBoxInfo(@BeanParam PurchaseBoxInfoVO purchaseBoxInfoVO,
                                        @Context ContainerRequestContext requestContext) {
        purchaseBoxInfoBiz.savePurchaseBoxInfo(purchaseBoxInfoVO, PurchaseBoxInfoStatusEnum.UNFINISH.getCode(),
                (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("暂存装箱信息成功","");
    }

    @POST
    @Path("finish")
    @ApiOperation(value = "完成装箱信息")
    @Produces(MediaType.APPLICATION_JSON)
    public Response savePurchaseBoxInfoFinish(@BeanParam PurchaseBoxInfoVO purchaseBoxInfoVO,
                                        @Context ContainerRequestContext requestContext) {
        purchaseBoxInfoBiz.savePurchaseBoxInfo(purchaseBoxInfoVO, PurchaseBoxInfoStatusEnum.FINISH.getCode(),
                (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("装箱信息完成成功","");
    }

    @GET
    @Path("dict")
    @ApiOperation(value = "获取包装方式", response = Dict.class)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPackingType(){
        return ResultUtil.createSuccessResult("获取包装方式成功",purchaseBoxInfoBiz.findPackingType());
    }

    @GET
    @Path("/{code}")
    @ApiOperation(value = "获取装箱信息", response = PurchaseBoxInfo.class)
    @Produces({MediaType.APPLICATION_JSON})
    @ApiImplicitParam(name = "code", value = "采购单号", paramType = "path", dataType = "String", required = true)
    public Response findPackingBoxInfo(@PathParam("code") String code){
        return ResultUtil.createSuccessResult("获取装箱信息成功",purchaseBoxInfoBiz.findPackingBoxInfo(code));
    }
}
