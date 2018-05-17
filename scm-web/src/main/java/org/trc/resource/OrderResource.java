package org.trc.resource;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.ExceptionOrder;
import org.trc.domain.order.ShopOrder;
import org.trc.domain.order.WarehouseOrder;
import org.trc.form.order.*;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Created by hzwdx on 2017/6/26.
 */
@Component
@Path(SupplyConstants.Order.ROOT)
public class OrderResource {

    @Autowired
    private IScmOrderBiz scmOrderBiz;

    @GET
    @Path(SupplyConstants.Order.SHOP_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response shopOrderPage(@BeanParam ShopOrderForm form, @BeanParam Pagenation<ShopOrder> page, @Context ContainerRequestContext requestContext){
        return ResultUtil.createSuccessPageResult(scmOrderBiz.shopOrderPage(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @GET
    @Path(SupplyConstants.Order.WAREHOUSE_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response warehouseOrderPage(@BeanParam WarehouseOrderForm form, @BeanParam Pagenation<WarehouseOrder> page, @Context ContainerRequestContext requestContext){
        //return scmOrderBiz.warehouseOrderPage(form, page);
        return ResultUtil.createSuccessPageResult(scmOrderBiz.warehouseOrderPage(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @GET
    @Path(SupplyConstants.Order.SHOP_ORDER_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryShopOrders(@BeanParam ShopOrderForm form){
        return ResultUtil.createSuccessResult("根据条件查询店铺订单成功", scmOrderBiz.queryShopOrders(form));
    }

    @GET
    @Path(SupplyConstants.Order.WAREHOUSE_ORDER_DETAIL+"/{warehouseOrderCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryWarehouseOrdersDetail(@PathParam("warehouseOrderCode") String warehouseOrderCode){
        return ResultUtil.createSuccessResult("根据仓库订单编码查询仓库订单成功", scmOrderBiz.queryWarehouseOrdersDetail(warehouseOrderCode));
    }

    @GET
    @Path(SupplyConstants.Order.PLATFORM_ORDER_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryPlatformOrders(@BeanParam PlatformOrderForm form){
        return ResultUtil.createSuccessResult("根据条件查询平台订单成功", scmOrderBiz.queryPlatformOrders(form));
    }

    @POST
    @Path(SupplyConstants.Order.JING_DONG_ORDER)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response submitJingDongOrder(@FormParam("warehouseOrderCode") String warehouseOrderCode,
            @FormParam("jdAddressCode") String jdAddressCode, @FormParam("jdAddressName") String jdAddressName, @Context ContainerRequestContext requestContext) throws Exception {
        ResponseAck responseAck = scmOrderBiz.submitJingDongOrder(warehouseOrderCode, jdAddressCode, jdAddressName, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        if(StringUtils.equals(ResponseAck.SUCCESS_CODE, responseAck.getCode())){
            return ResultUtil.createSuccessResult("操作成功", "");
        }else{
            return ResultUtil.createfailureResult(Integer.parseInt(responseAck.getCode()), responseAck.getMessage());
        }
    }

    /**
     * 供应商订单导出
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.Order.EXPORT_SUPPLIER_ORDER)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/octet-stream")
    public Response exportBalanceDetail(@BeanParam WarehouseOrderForm queryModel,@Context ContainerRequestContext requestContext) throws Exception {
        return scmOrderBiz.exportSupplierOrder( queryModel,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
    }
    @PUT
    @Path(SupplyConstants.Order.ORDER_CANCEL)
    @Produces(MediaType.APPLICATION_JSON)
    public Response orderCancel(@BeanParam SupplierOrderCancelForm form, @Context ContainerRequestContext requestContext) throws Exception {
        return ResultUtil.createSuccessResult(scmOrderBiz.cancelHandler(form, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)), "");
    }

    @GET
    @Path(SupplyConstants.Order.EXCEPTION_ORDER_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response exceptionOrderPage(@BeanParam ExceptionOrderForm form, @BeanParam Pagenation<ExceptionOrder> page, @Context ContainerRequestContext requestContext){
        return ResultUtil.createSuccessPageResult(scmOrderBiz.exceptionOrderPage(form, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @GET
    @Path(SupplyConstants.Order.EXCEPTION_ORDER_DETAIL+"/{exceptionOrderCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryExceptionOrdersDetail(@PathParam("exceptionOrderCode") String exceptionOrderCode){
        return ResultUtil.createSuccessResult("根据拆单异常订单编码查询拆单异常订单详情成功", scmOrderBiz.queryExceptionOrdersDetail(exceptionOrderCode));
    }

    @POST
    @Path(SupplyConstants.Order.ORDER_IMPORT)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/octet-stream")
    public Response orderImport(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
        return scmOrderBiz.importOrder(uploadedInputStream, fileDetail);
    }

    @GET
    @Path(SupplyConstants.Order.DOWNLOAD_ERROR_ORDER+"/{orderCode}")
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/octet-stream")
    public Response downloadErrorOrder(@PathParam("orderCode") String orderCode) throws Exception {
        return scmOrderBiz.downloadErrorOrder(orderCode);
    }
}



