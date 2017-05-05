package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.IWarehouseBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.form.WarehouseForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    public Pagenation<Warehouse> warehousePage(@BeanParam WarehouseForm form, @BeanParam Pagenation<Warehouse> page) throws Exception{
        return warehouseBiz.warehousePage(form,page);
    }
    //根据仓库名查询渠道
    @GET
    @Path(SupplyConstants.Warehouse.WAREHOUSE)
    @Produces(MediaType.APPLICATION_JSON)  //<WarehouseBiz>
    public AppResult findWarehouseByName(@QueryParam("name") String name) throws Exception{
        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据
        return  ResultUtil.createSucssAppResult("查询仓库成功", warehouseBiz.findWarehouseByName(name)==null ? null :"1");
    }
    //保存仓库
    @POST
    @Path(SupplyConstants.Warehouse.WAREHOUSE)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveChannel(@BeanParam Warehouse warehouse) throws Exception{
        return  ResultUtil.createSucssAppResult("保存成功",warehouseBiz.saveWarehouse(warehouse));
    }
    //仓库状态的修改
    @POST
    @Path(SupplyConstants.Warehouse.UPDATE_STATE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateWarehouseState(@BeanParam Warehouse warehouse) throws Exception{
        return ResultUtil.createSucssAppResult("状态修改成功",warehouseBiz.updateWarehouseState(warehouse));
    }

}
