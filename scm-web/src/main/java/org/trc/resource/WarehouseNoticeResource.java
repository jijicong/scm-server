package org.trc.resource;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.stereotype.Component;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.warehouseNotice.WarehouseNotice;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
    public Pagenation<WarehouseNotice> warehouseNoticePage(@BeanParam WarehouseNoticeForm form, @BeanParam Pagenation<WarehouseNotice> page,@Context ContainerRequestContext requestContext){

        return warehouseNoticeBiz.warehouseNoticePage(form,page,requestContext);

    }

    @POST
    @Path(SupplyConstants.WarehouseNotice.RECEIPT_ADVICE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult receiptAdvice(@BeanParam WarehouseNotice warehouseNotice,@Context ContainerRequestContext requestContext){

        warehouseNoticeBiz.receiptAdvice(warehouseNotice,requestContext);
        return ResultUtil.createSucssAppResult("通知收货成功","");

    }

    @GET
    @Path(SupplyConstants.WarehouseNotice.WAERHOUSE_NOTICE_INFO+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<WarehouseNotice> findWarehouseNoticeInfoById(@PathParam("id") Long id){

        return ResultUtil.createSucssAppResult("查询入库通知单信息成功",warehouseNoticeBiz.findfindWarehouseNoticeById(id));

    }


}
