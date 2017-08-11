package org.trc.resource;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.config.IConfigBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.util.AreaTreeNode;
import org.trc.enums.*;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.ResultUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 下拉列表资源
 * Created by hzwdx on 2017/5/6.
 */
@Component
@Path(SupplyConstants.SelectList.ROOT)
public class SelectListResource {

    @Autowired
    private IConfigBiz configBiz;

    //供应商性质字典类型编码
    public static final String SUPPLIER_NATURE = "supplierNature";
    //供应商类型字典类型编码
    public static final String SUPPLIER_TYPE = "supplierType";
    //仓库类型字典类型编码
    public static final String WAREHOUSE_TYPE="warehouseType";
    //角色类型字典类型编码
    public static final String ROLE_TYPE="roleType";
    //用户类型字典类型编码
    public static final String USER_TYPE="userType";
    //采购类型
    public static final String PURCHASE_TYPE="purchaseType";
    //付款方式
    public static final String PAY_TYPE="payType";
    //贸易类型字典类型编码
    public static final String TRADE_TYPE="tradeType";
    //币种
    public static final String CURRENCY_TYPE="currency";
    //运输费用承担方
    public static final String TRANSORT_COSTS_TAKE="transportCostsTake";
    //处理优先级
    public static final String  HANDLER_PRIORITY = "handlerPriority";


    @GET
    @Path(SupplyConstants.SelectList.PURCHASE_ORDER_AUDIT_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> purchaseOrderAuditStatus(){
        return ResultUtil.createSucssAppResult("查询采购订单的审核状态成功", PurchaseOrderAuditEnum.toJSONArray());
    }

    @GET
    @Path(SupplyConstants.SelectList.OUTBOUND_ORDER_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> outboundOrderStatus(){
        return ResultUtil.createSucssAppResult("查询发货通知单的状态成功", OutboundOrderStatusEnum.toJSONArray());
    }

    @GET
    @Path(SupplyConstants.SelectList.WAREHOSUE_NOTICE_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> queryWarehouseNoticeStatus(){
        return ResultUtil.createSucssAppResult("查询入库通知的状态成功", WarehouseNoticeStatusEnum.toJSONArray());
    }

    @GET
    @Path(SupplyConstants.SelectList.VALID_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> queryValidList(){
        return ResultUtil.createSucssAppResult("成功", ValidEnum.toJSONArray());
    }

    @GET
    @Path(SupplyConstants.SelectList.PURCHASE_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> purchaseType() throws Exception{
        return ResultUtil.createSucssAppResult("查询采购类型成功", configBiz.findDictsByTypeNo(PURCHASE_TYPE));
    }

    @GET
    @Path(SupplyConstants.SelectList.HANDLER_PRIORITY)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> handlerPriority() throws Exception{
        return ResultUtil.createSucssAppResult("查询处理优先级成功", configBiz.findDictsByTypeNo(HANDLER_PRIORITY));
    }

    @GET
    @Path(SupplyConstants.SelectList.TRANSORT_COSTS_TAKE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> transportCostsTake() throws Exception{
        return ResultUtil.createSucssAppResult("查询运费承担方成功", configBiz.findDictsByTypeNo(TRANSORT_COSTS_TAKE));
    }

    @GET
    @Path(SupplyConstants.SelectList.CURRENCY_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> currencyType() throws Exception{
        return ResultUtil.createSucssAppResult("查询币种类型成功", configBiz.findDictsByTypeNo(CURRENCY_TYPE));
    }

    @GET
    @Path(SupplyConstants.SelectList.PAY_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> payType() throws Exception{
        return ResultUtil.createSucssAppResult("查询付款方式成功", configBiz.findDictsByTypeNo(PAY_TYPE));
    }

    @GET
    @Path(SupplyConstants.SelectList.SUPPLIER_NATURE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> supplierNature() throws Exception{
        return ResultUtil.createSucssAppResult("查询供应商性质成功", configBiz.findDictsByTypeNo(SUPPLIER_NATURE));
    }

    @GET
    @Path(SupplyConstants.SelectList.SUPPLIER_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> supplierType() throws Exception{
        return ResultUtil.createSucssAppResult("查询供应商性质成功", configBiz.findDictsByTypeNo(SUPPLIER_TYPE));
    }
    //清关
    @GET
    @Path(SupplyConstants.SelectList.IS_CUSTOM_CLEARANCE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> queryisClearanceList(){
        return ResultUtil.createSucssAppResult("查询是否支持清关成功", ClearanceEnum.toJSONArray());
    }

    @GET
    @Path(SupplyConstants.SelectList.PURCHASE_ORDER_STATUS)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> querypurchaseOrderStatus(){
        return ResultUtil.createSucssAppResult("查询采购订单状态成功", PurchaseOrderStatusEnum.toJSONArray());
    }

    @GET
    @Path(SupplyConstants.SelectList.WAREHOUSE_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> warehouseType() throws Exception{
        return ResultUtil.createSucssAppResult("查询仓库类型成功", configBiz.findDictsByTypeNo(WAREHOUSE_TYPE));
    }
    @GET
    @Path(SupplyConstants.SelectList.PROVINCE_CITY)
    @Produces(MediaType.APPLICATION_JSON)
    public List<AreaTreeNode> findProvinceCity() throws Exception{

        return configBiz.findProvinceCity();

    }
    @GET
    @Path(SupplyConstants.SelectList.ROLE_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> roleType() throws Exception{
        return ResultUtil.createSucssAppResult("查询角色类型成功", configBiz.findDictsByTypeNo(ROLE_TYPE));
    }
    @GET
    @Path(SupplyConstants.SelectList.USER_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> userType() throws Exception{
        return ResultUtil.createSucssAppResult("查询用户类型成功", configBiz.findDictsByTypeNo(USER_TYPE));
    }

    @GET
    @Path(SupplyConstants.SelectList.TRADE_TYPE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> tradeType() throws Exception{
        return ResultUtil.createSucssAppResult("查询贸易类型成功", configBiz.findDictsByTypeNo(SupplyConstants.SelectList.TRADE_TYPE));
    }

    @GET
    @Path(SupplyConstants.SelectList.COUNTRY)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> country() throws Exception{
        return ResultUtil.createSucssAppResult("查询国家成功", configBiz.findDictsByTypeNo(SupplyConstants.SelectList.COUNTRY));
    }

    @GET
    @Path(SupplyConstants.SelectList.SUPPLIER)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> supplier() throws Exception{
        return ResultUtil.createSucssAppResult("查询一件代发供应商成功", configBiz.findDictsByTypeNo(SupplyConstants.SelectList.SUPPLIER));
    }

    @GET
    @Path(SupplyConstants.SelectList.SELECT_BY_TYPE_CODE+"/{typeCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Dict> selectByTypeCode(@PathParam("typeCode") String typeCode) throws Exception{
        AssertUtil.notBlank(typeCode, "根据字典类型查询字典字典类型编码不能为空");
        return ResultUtil.createSucssAppResult("查询字典成功", configBiz.findDictsByTypeNo(typeCode));
    }




}
