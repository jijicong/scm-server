package org.trc.resource;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.warehouseInfo.IWarehouseInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.form.warehouseInfo.WarehouseInfoForm;
import org.trc.form.warehouseInfo.WarehouseInfoResult;
import org.trc.util.Pagenation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by wangyz on 2017/11/15.
 */
@Component
@Path(SupplyConstants.WarehouseInfo.ROOT)
public class WarehouseInfoResource {
    private Logger logger = LoggerFactory.getLogger("WarehouseInfoResource");
    @Autowired
    IWarehouseInfoBiz warehouseInfoBiz;

    @POST
    @Path(SupplyConstants.WarehouseInfo.SAVE_WAREHOUSE_INFO)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveWarehouse(@FormParam("qimenWarehouseCode") String qimenWarehouseCode) throws Exception{
        logger.info("开始保存仓库信息到数据库===》"+"奇门仓库编号为："+qimenWarehouseCode);
        return warehouseInfoBiz.saveWarehouse(qimenWarehouseCode);
    }

    @GET
    @Path(SupplyConstants.WarehouseInfo.SELECT_WAREHOUSE_NAME_NOT_LOCATION)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryWarehouseNameNotLocation() throws Exception{
        logger.info("开始查询未添加的仓库名称=========》");
        return warehouseInfoBiz.selectWarehouseNotInLocation();
    }

    @GET
    @Path(SupplyConstants.WarehouseInfo.SELECT_WAREHOUSE_NAME)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryWarehouseName() throws Exception{
        logger.info("开始查询仓库名称=========》");
        return warehouseInfoBiz.selectWarehouse();
    }

    @GET
    @Path(SupplyConstants.WarehouseInfo.WAREHOUSE_INFO_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<WarehouseInfoResult> queryWarehouseName(@BeanParam WarehouseInfoForm query, @BeanParam Pagenation<WarehouseInfo> page) throws Exception{
        logger.info("开始分页查询仓库信息，请求参数分别为：query="+ JSON.toJSONString(query)+",page="+JSON.toJSONString(page));
        return warehouseInfoBiz.selectWarehouseInfoByPage(query,page);
    }
}
