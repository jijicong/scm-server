package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.warehouseInfo.IWarehousePriorityBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by wangyz on 2017/11/15.
 */
@Component
@Path(SupplyConstants.WarehousePriority.ROOT)
public class WarehousePriorityResource {
    @Autowired
    IWarehousePriorityBiz warehousePriorityBiz;

    @GET
    @Path(SupplyConstants.WarehousePriority.WAREHOUSE_PRIORITY_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response warehousePriorityList(){
        return ResultUtil.createSuccessPageResult(warehousePriorityBiz.warehousePriorityList());
    }

    @GET
    @Path(SupplyConstants.WarehousePriority.WAREHOUSE_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response warehouseList(){
        return ResultUtil.createSuccessPageResult(warehousePriorityBiz.queryWarehouseInfoList());
    }

    @POST
    @Path(SupplyConstants.WarehousePriority.WAREHOUSE_PRIORITY)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveWarehouse(@FormParam("warehousePriorityInfo") String warehousePriorityInfo, @Context ContainerRequestContext requestContext) throws Exception{
        warehousePriorityBiz.saveWarehousePriority(warehousePriorityInfo, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("保存仓库优先级成功", "");
    }
}
