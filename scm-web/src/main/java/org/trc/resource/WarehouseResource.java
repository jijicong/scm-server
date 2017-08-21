package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.system.IWarehouseBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.system.WarehouseForm;
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
 * Created by sone on 2017/5/4.
 */
@Component
@Path(SupplyConstants.Warehouse.ROOT)
public class WarehouseResource {
    @Resource
    private IWarehouseBiz warehouseBiz;

    //仓库分页查询
    @GET
    @Path(SupplyConstants.Warehouse.WAREHOUSE_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Warehouse> warehousePage(@BeanParam WarehouseForm form, @BeanParam Pagenation<Warehouse> page){
        return warehouseBiz.warehousePage(form,page);
    }
    //根据仓库名查询仓库
    @GET
    @Path(SupplyConstants.Warehouse.WAREHOUSE)
    @Produces(MediaType.APPLICATION_JSON)  //<WarehouseBiz>
    public Response findWarehouseByName(@QueryParam("name") String name){
        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据
        return  ResultUtil.createSuccessResult("查询仓库成功", warehouseBiz.findWarehouseByName(name)==null ? null :"1");
    }
    //保存仓库
    @POST
    @Path(SupplyConstants.Warehouse.WAREHOUSE)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public Response saveChannel(@BeanParam Warehouse warehouse,@Context ContainerRequestContext requestContext){
        warehouseBiz.saveWarehouse(warehouse,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("保存成功","");
    }
    //仓库修改
    @PUT
    @Path(SupplyConstants.Warehouse.WAREHOUSE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateChannel(@BeanParam Warehouse warehouse,@Context ContainerRequestContext requestContext){
        warehouseBiz.updateWarehouse(warehouse,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("修改仓库信息成功","");
    }
    //仓库状态的修改
    @PUT
    @Path(SupplyConstants.Warehouse.UPDATE_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateWarehouseState(@BeanParam Warehouse warehouse,@Context ContainerRequestContext requestContext){
        warehouseBiz.updateWarehouseState(warehouse,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("状态修改成功","");
    }
    //根据id查询
    @GET
    @Path(SupplyConstants.Warehouse.WAREHOUSE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWarehouseById(@PathParam("id") Long id){
        return ResultUtil.createSuccessResult("查询仓库成功", warehouseBiz.findWarehouseById(id));
    }
    //
    @GET
    @Path(SupplyConstants.Warehouse.WAREHOUSE_VALID)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findWarehouseValid() {
        return ResultUtil.createSuccessResult("查询有效的仓库成功",warehouseBiz.findWarehouseValid());
    }
}
