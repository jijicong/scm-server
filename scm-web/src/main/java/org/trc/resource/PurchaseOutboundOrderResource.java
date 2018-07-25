package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.purchase.IPurchaseOutboundOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.domain.purchase.PurchaseOutboundOrderDataForm;
import org.trc.enums.purchase.PurchaseOutboundOrderStatusEnum;
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
    @ApiImplicitParams({
            @ApiImplicitParam()
    })
    public Response savePurchaseOutboundOrder(@BeanParam PurchaseOutboundOrderDataForm form, @Context ContainerRequestContext requestContext) {
        purchaseOutboundOrderBiz.savePurchaseOutboundOrder(form, PurchaseOutboundOrderStatusEnum.HOLD.getCode(), (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("保存采购退货单成功!", "");
    }
}
