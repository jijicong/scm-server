package org.trc.resource;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.warehouseNotice.IPurchaseOutboundNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseNotice.PurchaseOutboundNotice;
import org.trc.form.warehouse.PurchaseOutboundNoticeForm;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "退货出库通知单管理")
@Component
@Path("/purchaseOutboundNotice")
public class PurchaseOutboundNoticeResource {

    @Autowired
    private IPurchaseOutboundNoticeBiz noticeBiz;

    /**
     * 查询退货出库通知单列表
     */
    @GET
    @Path("/pageList")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("查询退货出库通知单列表")
    public Response pageList(@BeanParam PurchaseOutboundNoticeForm form, @BeanParam Pagenation<PurchaseOutboundNotice> page, 
    		@Context ContainerRequestContext requestContext) {
        Object obj = requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO);
        AssertUtil.notNull(obj, "查询订单分页中,获得授权信息失败");
        AclUserAccreditInfo aclUserAccreditInfo = (AclUserAccreditInfo) obj;
        String channelCode = aclUserAccreditInfo.getChannelCode(); //获得渠道的编码
        return ResultUtil.createSuccessPageResult(noticeBiz.pageList(form, page, channelCode));
    }

}
