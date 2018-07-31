package org.trc.resource;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseOutboundOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.enums.purchase.PurchaseOutboundOrderStatusEnum;
import org.trc.form.purchase.PurchaseOutboundItemForm;
import org.trc.form.purchase.PurchaseOutboundOrderForm;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Description〈采购退货单〉
 *
 * @author hzliuwei
 * @create 2018/7/24
 */
@Api(value = "采购退货单管理")
@Component
@Path("/purchaseOutboundOrder")
public class PurchaseOutboundOrderResource {

    @Autowired
    private IPurchaseOutboundOrderBiz purchaseOutboundOrderBiz;

    /**
     * 查询采购退货单列表
     */
    @GET
    @Path("/pagelist")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("查询采购退货单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "purchaseOutboundOrderCode", value = "采购退货单编号", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "supplierName", value = "供应商名称", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "warehouseName", value = "退货仓库", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "returnOrderType", value = "退货类型", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "status", value = "单据状态", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "outboundStatus", value = "出库状态", required = false)
    })
    public Response getPurchaseOutboundOrderList(@BeanParam PurchaseOutboundOrderForm form, @BeanParam Pagenation<PurchaseOutboundOrder> page, @Context ContainerRequestContext requestContext) {
        Object obj = requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        AssertUtil.notNull(obj, "查询订单分页中,获得授权信息失败");
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) obj;
        String channelCode = aclUserAccreditInfo.getChannelCode(); //获得渠道的编码
        //采购订单分页查询列表
        return ResultUtil.createSuccessPageResult(purchaseOutboundOrderBiz.purchaseOutboundOrderPageList(form, page, channelCode));
    }


    /**
     * 保存采购退货单
     */
    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("保存采购退货单")
    //public Response savePurchaseOutboundOrder(@BeanParam PurchaseOutboundOrderDataForm form, @Context ContainerRequestContext requestContext) {
    public Response savePurchaseOutboundOrder(PurchaseOutboundOrder form, @Context ContainerRequestContext requestContext) {
        purchaseOutboundOrderBiz.savePurchaseOutboundOrder(form, PurchaseOutboundOrderStatusEnum.HOLD.getCode(), (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("保存采购退货单成功!", "");
    }

    /**
     * 根据采购退货单Id查询采购退货单
     */
    @GET
    @Path("getOrder/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("根据采购退货单Id查询采购退货单")
    public Response getPurchaseOutboundOrder(@ApiParam(name = "采购退货单Id") @PathParam("id") Long id) {
        return ResultUtil.createSuccessResult("根据采购退货单Id查询采购退货单信息成功", purchaseOutboundOrderBiz.getPurchaseOutboundOrderById(id));
    }

    /**
     * 修改采购退货单
     */
    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("修改采购退货单")
    public Response updatePurchaseOutboundOrder(PurchaseOutboundOrder form, @Context ContainerRequestContext requestContext) {
        purchaseOutboundOrderBiz.updatePurchaseOutboundOrder(form, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("修改采购退货单成功!", "");
    }

    /**
     * 提交采购退货单
     */
    @POST
    @Path("/commit")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("提交审核采购退货单")
    public Response commitAuditPurchaseOutboundOrder(PurchaseOutboundOrder form, @Context ContainerRequestContext requestContext) {
        purchaseOutboundOrderBiz.savePurchaseOutboundOrder(form, PurchaseOutboundOrderStatusEnum.AUDIT.getCode(), (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("提交审核采购退货单成功!", "");
    }

    /**
     * 获取采购退货单商品详情
     */
    @GET
    @Path("/getDetail")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("获取采购退货单商品详情")
    public Response getPurchaseOutboundOrderDetail(@BeanParam PurchaseOutboundItemForm form, @BeanParam Pagenation<PurchaseOutboundDetail> page, @QueryParam("skus") String skus) {
        return ResultUtil.createSuccessResult("根据供应商编码查询所有的有效商品成功", purchaseOutboundOrderBiz.getPurchaseOutboundOrderDetail(form, page, skus));
    }

    /**
     * 采购退货单获取采购历史详情
     */
    @GET
    @Path("/getPurchaseHistory")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("采购退货单获取采购历史详情")
    public Response getPurchaseHistory(@BeanParam PurchaseOutboundItemForm form, @BeanParam Pagenation<WarehouseNoticeDetails> page) {
        return ResultUtil.createSuccessResult("获取采购历史详情成功", purchaseOutboundOrderBiz.getPurchaseHistory(form, page));
    }

    /**
     * 更新采购退货单状态或出库通知作废操作
     */
    @PUT
    @Path("updateStatus/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("更新采购退货单状态或出库通知作废操作")
    public Response updatePurchaseState(PurchaseOutboundOrder form, @Context ContainerRequestContext requestContext) {
        AssertUtil.notNull(form, "采购退货单的信息为空");
        AssertUtil.notNull(form.getStatus(), "采购退货单的状态为空");
        if (StringUtils.equals(form.getStatus(), PurchaseOutboundOrderStatusEnum.WAREHOUSE_NOTICE.getCode())) {
            purchaseOutboundOrderBiz.cancelWarahouseAdvice(form, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
            return ResultUtil.createSuccessResult("出库通知作废成功！", "");
        } else {
            String msg = purchaseOutboundOrderBiz.updateStatus(form, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
            return ResultUtil.createSuccessResult(msg, "");
        }
    }

    /**
     * 采购退货单出库通知
     */
    @PUT
    @Path("warahouseAdvice/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("采购退货单出库通知")
    public Response saveWarahouseAdvice(PurchaseOutboundOrder form, @Context ContainerRequestContext requestContext) {
        purchaseOutboundOrderBiz.warehouseAdvice(form, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("采购退货单出库通知成功!", "");
    }


    ///**
    // * 审核采购退货单，根据采购退货编号查询采购退货单详情
    // */
    //@GET
    //@Path("getOrder/{purchaseOutboundOrderCode}")
    //@Produces(MediaType.APPLICATION_JSON)
    //@ApiOperation("审核采购退货单，根据采购退货编号查询采购退货单详情")
    //public Response getPurchaseOutboundOrderByCode(@ApiParam(name = "purchaseOutboundOrderCode", value = "采购退货单编号")
    //                                               @PathParam("purchaseOutboundOrderCode") String purchaseOutboundOrderCode) {
    //    return ResultUtil.createSuccessResult("根据采购退货单编号查询采购退货单信息成功", purchaseOutboundOrderBiz.getPurchaseOutboundOrderByCode(purchaseOutboundOrderCode));
    //}


    @POST
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(@BeanParam PurchaseOutboundOrder form) {
        System.out.println(JSON.toJSONString(form));
        return ResultUtil.createSuccessResult("success", JSON.toJSONString(form));
    }
}
