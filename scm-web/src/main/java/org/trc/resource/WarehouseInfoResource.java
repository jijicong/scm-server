package org.trc.resource;

import com.alibaba.fastjson.JSON;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.warehouseInfo.IWarehouseInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.form.UploadResponse;
import org.trc.form.warehouseInfo.*;
import org.trc.util.AssertUtil;
import org.trc.util.ImportExcel;
import org.trc.form.warehouseInfo.WarehouseInfoForm;
import org.trc.form.warehouseInfo.WarehouseInfoResult;
import org.trc.form.warehouseInfo.WarehouseItemInfoForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyz on 2017/11/15.
 */
@Component
@Path(SupplyConstants.WarehouseInfo.ROOT)
public class WarehouseInfoResource {
    @Autowired
    IWarehouseInfoBiz warehouseInfoBiz;
    private Logger logger = LoggerFactory.getLogger("WarehouseInfoResource");

    @POST
    @Path(SupplyConstants.WarehouseInfo.SAVE_WAREHOUSE_INFO)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveWarehouse(@FormParam("code") String code, @Context ContainerRequestContext requestContext) throws Exception{
        logger.info("开始保存仓库信息到数据库===》"+"奇门仓库编号为："+code);
        return warehouseInfoBiz.saveWarehouse(code,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
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

    @PUT
    @Path(SupplyConstants.WarehouseInfo.OWNER_INFO+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveOwnerInfo(@BeanParam WarehouseInfo warehouseInfo) throws Exception{
        logger.info("开始保存货主信息=========》");
        return warehouseInfoBiz.saveOwnerInfo(warehouseInfo);
    }

    @PUT
    @Path(SupplyConstants.WarehouseInfo.DELETE_WAREHOUSE_INFO+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteWarehouse(@PathParam("id") String id) throws Exception{
        logger.info("开始删除仓库=========》");
        return warehouseInfoBiz.deleteWarehouse(id);
    }



    @GET
    @Path(SupplyConstants.WarehouseInfo.WAREHOUSE_ITEM_INFO_PAGE + "/{warehouseInfoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryWarehouseItemInfoPage(
            @PathParam("warehouseInfoId") Long warehouseInfoId, @BeanParam WarehouseItemInfoForm form,
            @BeanParam Pagenation<WarehouseItemInfo> page) {
        logger.info("开始分页查询仓库商品信息，请求参数分别为：query=" + JSON.toJSONString(form) + ",page=" + JSON.toJSONString(page) + ",warehouseInfoId=" + warehouseInfoId);
        return ResultUtil.createSuccessPageResult(warehouseInfoBiz.queryWarehouseItemInfoPage(form, warehouseInfoId, page));
    }

    @DELETE
    @Path(SupplyConstants.WarehouseInfo.WAREHOUSE_ITEM_INFO + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDictById(@PathParam("id") Long id) {
        logger.info("开始删除仓库商品信息，请求参数分别为：id=" + id);
        warehouseInfoBiz.deleteWarehouseItemInfoById(id);
        return ResultUtil.createSuccessResult("删除仓库商品信息成功", "");
    }

    @PUT
    @Path(SupplyConstants.WarehouseInfo.WAREHOUSE_ITEM_INFO + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDict(@BeanParam WarehouseItemInfo warehouseItemInfo) throws Exception {
        logger.info("开始修改仓库商品信息，请求参数分别为：warehouseItemInfo=" + JSON.toJSONString(warehouseItemInfo));
        warehouseInfoBiz.updateWarehouseItemInfo(warehouseItemInfo);
        return ResultUtil.createSuccessResult("修改仓库商品信息成功", "");
    }

    @GET
    @Path(SupplyConstants.WarehouseInfo.ITEMS_EXPORT+"/{warehouseInfoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response exportOrderDetail(@BeanParam WarehouseItemInfoForm form, @PathParam("warehouseInfoId") Long warehouseInfoId) throws Exception {
        logger.info("进入商品信息导出接口======>"+ "传入参数为：form："+JSON.toJSONString(form)+",warehouseInfoId:"+warehouseInfoId);
        return warehouseInfoBiz.exportWarehouseItems(form,warehouseInfoId);
    }

    @POST
    @Path(SupplyConstants.WarehouseInfo.SAVE_ITEMS+"/{warehouseInfoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveWarehouseItemsSku(@FormParam("itemsList") String itemsList, @PathParam("warehouseInfoId") Long warehouseInfoId) throws Exception {
        logger.info("进入添加新商品接口======>"+ "传入参数为：form："+JSON.toJSONString(itemsList)+",warehouseInfoId:"+warehouseInfoId);
        return warehouseInfoBiz.saveWarehouseItemsSku(itemsList,warehouseInfoId);
    }

    @GET
    @Path(SupplyConstants.WarehouseInfo.ITEMS_PAGE+"/{warehouseInfoId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<ItemsResult> queryWarehouseItemInfoPage(@BeanParam SkusForm form, @BeanParam Pagenation<Skus> page, @PathParam("warehouseInfoId") Long warehouseInfoId) throws Exception {
        logger.info("进入商品分页查询接口======>"+ "传入参数为：form："+JSON.toJSONString(form)+",warehouseInfoId:"+warehouseInfoId);
        return warehouseInfoBiz.queryWarehouseItemsSku(form, page,warehouseInfoId);
    }

    @POST
    @Path(SupplyConstants.WarehouseInfo.NOTICE_STATUS)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadNoticeStatus(@FormDataParam("file") InputStream uploadedInputStream,
                                       @FormDataParam("file") FormDataContentDisposition fileDetail,
                                       @FormDataParam("warehouseInfoId") String warehouseInfoId) {
        logger.info("开始导入仓库商品信息，请求参数分别为：warehouseInfoId=" + warehouseInfoId);
        return warehouseInfoBiz.uploadNoticeStatus(uploadedInputStream, fileDetail, warehouseInfoId);
    }
}
