package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.system.IWarehouseBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.purchase.WarehouseNotice;
import org.trc.form.system.WarehouseForm;
import org.trc.form.warehouse.WarehouseNoticeForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
    public Pagenation<WarehouseNotice> warehouseNoticePage(@BeanParam WarehouseNoticeForm form, @BeanParam Pagenation<WarehouseNotice> page){

        return warehouseNoticeBiz.warehouseNoticePage(form,page);

    }

}
