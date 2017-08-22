package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by sone on 2017/7/11.
 */
@Component
@Path(SupplyConstants.WarehouseNotice.ROOT)
public class WarehouseNoticeResource {
    @Resource
    private IWarehouseNoticeBiz warehouseNoticeBiz;


    //入库通知的分页查询
    @GET
    @Path(SupplyConstants.WarehouseNotice.WAREHOUSE_NOTICE_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response warehouseNoticePage(@BeanParam WarehouseNoticeForm form, @BeanParam Pagenation<WarehouseNotice> page,@Context ContainerRequestContext requestContext){

        return ResultUtil.createSuccessPageResult(warehouseNoticeBiz.warehouseNoticePage(form,page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));

    }

    @PUT
    @Path(SupplyConstants.WarehouseNotice.RECEIPT_ADVICE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiptAdvice(@BeanParam WarehouseNotice warehouseNotice, @Context ContainerRequestContext requestContext){

        warehouseNoticeBiz.receiptAdvice(warehouseNotice,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("通知收货成功","");

    }

    @PUT
    @Path(SupplyConstants.WarehouseNotice.RECEIPT_ADVICE_INFO+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response receiptAdviceInfo(@BeanParam WarehouseNotice warehouseNotice,@Context ContainerRequestContext requestContext){
        //入库通知单详情页的入库通知操作
        warehouseNoticeBiz.receiptAdviceInfo(warehouseNotice,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("通知收货成功","");

    }

    @GET
    @Path(SupplyConstants.WarehouseNotice.WAERHOUSE_NOTICE_INFO+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWarehouseNoticeInfoById(@PathParam("id") Long id){

        return ResultUtil.createSuccessResult("查询入库通知单信息成功",warehouseNoticeBiz.findfindWarehouseNoticeById(id));

    }

    @GET
    @Path(SupplyConstants.WarehouseNotice.WAREHOUSE_NOTICE_DETAIL)
    @Produces(MediaType.APPLICATION_JSON)
    public Response warehouseNoticeDetailList(@QueryParam("warehouseNotice") Long warehouseNotice)throws Exception{
        //"根据入库通知单的id，查询入库明细成功",
        return ResultUtil.createSuccessPageResult(warehouseNoticeBiz.warehouseNoticeDetailList(warehouseNotice));
    }

}
