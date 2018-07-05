package org.trc.resource.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.goods.IGoodsBiz;
import org.trc.biz.impower.IAclResourceBiz;
import org.trc.biz.impower.IAclUserAccreditInfoBiz;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.util.AppResult;
import org.trc.util.ResponseAck;
import org.trc.util.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzwdx on 2017/7/14.
 */
@Component
@Path(SupplyConstants.Api.Root)
public class ScmApiResource {
    @Autowired
    private IGoodsBiz goodsBiz;
    @Autowired
    private IScmOrderBiz scmOrderBiz;
    @Autowired
    private IAclResourceBiz jurisdictionBiz;
    @Autowired
    private IAclUserAccreditInfoBiz aclUserAccreditInfoBiz;

    @POST
    @Path(SupplyConstants.Api.EXTERNAL_ITEM_UPDATE)
    @Produces(MediaType.APPLICATION_JSON+";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded;charset=utf-8")
    public AppResult supplierSkuUpdateNotice(@FormParam("updateSupplierSkus") String updateSupplierSkus) throws Exception {
        goodsBiz.supplierSkuUpdateNotice(updateSupplierSkus);
        return ResultUtil.createSucssAppResult("供应商sku更新通知成功", "");
    }

    @POST
    @Path(SupplyConstants.Api.SUPPLIER_ORDER_CANCEL)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<String> supplierOrderCancel(String orderInfo) {
        return scmOrderBiz.supplierCancelOrder(orderInfo);
    }

    @GET
    @Path(SupplyConstants.Api.JURISDICTION_USER_CHANNEL)
    public Response getUserChannel(@Context ContainerRequestContext requestContext){
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        return ResultUtil.createSuccessResult("查询用户业务线成功!", jurisdictionBiz.queryChannelList(userId));
    }
    @GET
    @Path(SupplyConstants.Api.CONFIRM_USER_CHANNEL)
    public Response confirmUser(@QueryParam("channelCode") String channelCode,@Context ContainerRequestContext requestContext, @Context HttpServletRequest request){
        request.getSession().setAttribute("channelCode", channelCode);

        return ResultUtil.createSuccessResult("设置业务线成功!",request.getSession().getId());
    }
    @GET
    @Path(SupplyConstants.Api.CLEAR_SESSION)
    public Response clearSession(@Context ContainerRequestContext requestContext,@Context HttpServletRequest request){
        request.getSession().removeAttribute("channelCode");
        request.getSession().invalidate();
        String userId= (String) requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        aclUserAccreditInfoBiz.logOut(userId);
        return ResultUtil.createSuccessResult("设置Session失效成功!","");
    }

    @POST
    @Path(SupplyConstants.Api.JD_ORDER_SPLIT_NOTICE)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<String> jdOrderSplitNotice(String orderInfo) {
        return scmOrderBiz.jdOrderSplitNotice(orderInfo);
    }

    @POST
    @Path(SupplyConstants.Api.ORDER_SUBMIT_RESULT_NOTICE)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<String> orderSubmitResultNotice(String orderInfo) {
        return scmOrderBiz.orderSubmitResultNotice(orderInfo);
    }

}
