package org.trc.resource.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.afterSale.IAfterSaleOrderBiz;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.impl.trc.model.Skus2;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.order.WarehouseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.OrderTypeEnum;
import org.trc.form.AfterSaleOrderStatusResponse;
import org.trc.form.afterSale.AfterSaleWaybillForm;
import org.trc.form.afterSale.TaiRanAfterSaleOrderDetail;
import org.trc.form.afterSale.TairanAfterSaleOrderDO;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.SkusForm;
import org.trc.form.order.SkuWarehouseDO;
import org.trc.form.supplier.SupplierForm;
import org.trc.form.trc.BrandForm2;
import org.trc.form.trc.CategoryForm2;
import org.trc.form.trc.ItemsForm2;
import org.trc.form.trcForm.PropertyFormForTrc;
import org.trc.util.AssertUtil;
import org.trc.util.ExceptionUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResponseAck;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 对泰然城开放接口
 * Created by hzdzf on 2017/5/26.
 */
@Component
@Path(SupplyConstants.TaiRan.ROOT)
public class TaiRanResource {

    private Logger logger = LoggerFactory.getLogger(TaiRanResource.class);

    @Resource
    private ICategoryBiz categoryBiz;
    @Resource
    private ITrcBiz trcBiz;
    @Resource
    private IScmOrderBiz scmOrderBiz;
    @Autowired
    private IAfterSaleOrderBiz afterSaleOrderBiz;


    /**
     * 分页查询品牌
     *
     * @param form
     * @param page
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.BRAND_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Pagenation<Brand>> queryBrand(@BeanParam BrandForm2 form, @BeanParam Pagenation<Brand> page,@QueryParam("channelCode") String channelCode, @QueryParam("sellCode") String sellCode) throws Exception{
        trcBiz.checkChannelCode(channelCode);
        trcBiz.checkSellCode(sellCode);
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "品牌查询成功", trcBiz.brandList(form, page));
    }

    /**
     * 供应商分页查询
     * @param page
     * @param requestContext
     * @param form
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.TaiRan.SUPPLIER_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseAck<Pagenation<Supplier>> supplierPage(@BeanParam Pagenation<Supplier> page, @Context ContainerRequestContext requestContext,
                                                          @BeanParam SupplierForm form,@QueryParam("channelCode") String channelCode,@QueryParam("sellCode") String sellCode) throws Exception {
        trcBiz.checkChannelCode(channelCode);
        trcBiz.checkSellCode(sellCode);
        try {
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "供应商分页查询成功", trcBiz.supplierPage(form,page,channelCode));
        } catch (Exception e) {
            logger.error("供应商分页查询报错", e);
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("供应商分页查询报错,%s", e.getMessage()), "");
        }
    }
    /**
     * 分页查询属性
     *
     * @param form
     * @param page
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.PROPERTY_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Object> queryProperty(@BeanParam PropertyFormForTrc form, @BeanParam Pagenation<Property> page,@QueryParam("channelCode") String channelCode,
                                             @QueryParam("sellCode") String sellCode){
        try {
            trcBiz.checkChannelCode(channelCode);
            trcBiz.checkSellCode(sellCode);
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "属性查询成功", trcBiz.propertyPage(form, page));
        } catch (Exception e) {
            logger.error("查询列表信息报错", e);
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("查询列表信息报错,%s", e.getMessage()), "");
        }

    }

    /**
     * 分页查询分类
     *
     * @param categoryForm
     * @param page
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.CATEGORY_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Pagenation<Category>> queryCategory(@BeanParam CategoryForm2 categoryForm, @BeanParam Pagenation<Category> page,@QueryParam("channelCode") String channelCode,
                                                           @QueryParam("sellCode") String sellCode) throws Exception {
        trcBiz.checkChannelCode(channelCode);
        trcBiz.checkSellCode(sellCode);
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "分类查询成功", trcBiz.categoryPage(categoryForm, page));
    }

    /**
     * 查询分类品牌列表
     *
     * @param categoryId
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.CATEGORY_BRAND_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<List<CategoryBrand>> queryCategoryBrand(@QueryParam("categoryId") @Length(max = 20, message = "分类ID长度不能超过20个") Long categoryId,@QueryParam("channelCode") String channelCode,
                                                               @QueryParam("sellCode") String sellCode) throws Exception {
        trcBiz.checkChannelCode(channelCode);
        trcBiz.checkSellCode(sellCode);
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "分类品牌查询成功", categoryBiz.queryBrands(categoryId));
    }

    /**
     * 查询分类属性列表
     *
     * @param categoryId
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.CATEGORY_PROPERTY_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<List<CategoryProperty>> queryCategoryProperty(@QueryParam("categoryId") Long categoryId,@QueryParam("channelCode") String channelCode,
                                                                     @QueryParam("sellCode") String sellCode) throws Exception {
        trcBiz.checkChannelCode(channelCode);
        trcBiz.checkSellCode(sellCode);
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "分类属性查询成功", categoryBiz.queryProperties(categoryId));
    }

    /**
     * 查询自采sku信息
     *
     * @param skuCode 传递供应链skuCode
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.SKU_INFORMATION)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Object> getSpuInformations(@QueryParam("skuCode") String skuCode,@QueryParam("channelCode") String channelCode,
                                                  @QueryParam("sellCode") String sellCode) throws Exception{
        trcBiz.checkChannelCode(channelCode);
        trcBiz.checkSellCode(sellCode);
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "sku信息查询成功", trcBiz.getSkuInformation(skuCode, channelCode));
    }

    /**
     * 查询代发sku信息
     *
     * @param skuCode 传递供应链skuCode
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.EXTERNAL_SKU_INFORMATION)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Object> getExternalSkuInformations(@QueryParam("skuCode") String skuCode,@QueryParam("channelCode") String channelCode,
                                                          @QueryParam("sellCode") String sellCode) throws Exception {
        trcBiz.checkChannelCode(channelCode);
        trcBiz.checkSellCode(sellCode);
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "sku信息查询成功", trcBiz.getExternalSkuInformation(skuCode,channelCode));
    }


    @POST
    @Path(SupplyConstants.TaiRan.ORDER_PROCESSING)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<String> reciveChannelOrder(String orderInfo) {
        ResponseAck responseAck = null;
        try{
            responseAck = scmOrderBiz.reciveChannelOrder(orderInfo);
            Map<String, Object> map = (Map<String, Object>)responseAck.getData();
            List<WarehouseOrder> warehouseOrderList = (List<WarehouseOrder>)map.get("warehouseOrderList");
            Map<String, List<SkuWarehouseDO>> skuWarehouseMap = (Map<String, List<SkuWarehouseDO>>)map.get("skuWarehouseMap");
            //获取粮油或者自采仓库订单
            List<WarehouseOrder> lyWarehouseOrders = new ArrayList<WarehouseOrder>();
            List<WarehouseOrder> selfPurchaseOrders = new ArrayList<WarehouseOrder>();
            for(WarehouseOrder warehouseOrder: warehouseOrderList){
                if(StringUtils.equals(SupplyConstants.Order.SUPPLIER_LY_CODE, warehouseOrder.getSupplierCode())){
                    lyWarehouseOrders.add(warehouseOrder);
                }
                if(StringUtils.equals(OrderTypeEnum.SELF_PURCHARSE.getCode(), warehouseOrder.getOrderType())){
                    selfPurchaseOrders.add(warehouseOrder);
                }
            }
            if(lyWarehouseOrders.size() > 0){
                //粮油下单
                scmOrderBiz.submitLiangYouOrders(lyWarehouseOrders);
            }
            if(selfPurchaseOrders.size() > 0){
                //自采下单
                scmOrderBiz.submitSelfPurchaseOrder(selfPurchaseOrders, skuWarehouseMap);
            }
        }catch (Exception e){
            String code = ExceptionUtil.getErrorInfo(e);
            responseAck = new ResponseAck(code, e.getMessage(), "");
            logger.error(String.format("接收渠道同步订单%s异常", orderInfo), e);
        }finally {
            scmOrderBiz.saveChannelOrderRequestFlow(orderInfo, responseAck);
        }
        responseAck.setData(null);
        return responseAck;
    }


    //批量新增关联关系（单个也调用此方法），待修改
    @POST
    @Path(SupplyConstants.TaiRan.SKURELATION_UPDATE)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<String> addSkuRelationBatch(String information) {
        AssertUtil.notBlank(information, "请求参数不能为空");
        JSONObject orderObj = null;
        try {
            orderObj = JSONObject.parseObject(information);
        } catch (JSONException e) {
            logger.error("参数转json格式错误", e);
            return new ResponseAck(CommonExceptionEnum.PARAM_CHECK_EXCEPTION.getCode(), String.format("请求参数%s不是json格式", information), "");
        }
        AssertUtil.isTrue(orderObj.containsKey("action"), "参数action不能为空");
        AssertUtil.isTrue(orderObj.containsKey("relations"), "参数relations不能为空");
        String action = orderObj.getString("action");
        JSONArray relations = orderObj.getJSONArray("relations");
        try {
            trcBiz.updateRelation(action, relations);
        } catch (Exception e) {
            logger.error("关联信息更新失败", e);
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("关联信息更新失败,%s", e.getMessage()), "");
        }
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "关联信息更新成功", "");
    }

    //自采商品信息查询
    @GET
    @Path(SupplyConstants.TaiRan.ITEM_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Pagenation<Items>> itemList(@BeanParam ItemsForm2 form, @BeanParam Pagenation<Items> page,@QueryParam("channelCode") String channelCode,
                                                   @QueryParam("sellCode") String sellCode) throws Exception {
        try {
            trcBiz.checkChannelCode(channelCode);
            trcBiz.checkSellCode(sellCode);
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "自采商品查询成功", trcBiz.itemsPage(form, page, channelCode));
        } catch (Exception e) {
            logger.error("自采商品查询报错", e);
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("自采商品查询报错,%s", e.getMessage()), "");
        }
    }


    //自采商品SKU分页信息查询
    @GET
    @Path(SupplyConstants.TaiRan.SKUS_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Pagenation<Skus2>> skusList(@BeanParam SkusForm skusForm, @BeanParam Pagenation<Skus> pagenation,@QueryParam("channelCode") String channelCode,
                                                   @QueryParam("sellCode") String sellCode) {
        try {
            trcBiz.checkChannelCode(channelCode);
            trcBiz.checkSellCode(sellCode);
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "sku列表查询信息成功", trcBiz.skusPage(skusForm, pagenation, channelCode));
        } catch (Exception e) {
            logger.error("查询sku列表信息报错", e);
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("查询sku列表信息报错,%s", e.getMessage()), "");
        }
    }


    //一件代发商品分页信息查询
    @GET
    @Path(SupplyConstants.TaiRan.EXTERNALITEMSKU_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Pagenation<ExternalItemSku>> externalItemSkus(@BeanParam ExternalItemSkuForm form, @BeanParam Pagenation<ExternalItemSku> page,@QueryParam("channelCode") String channelCode,
                                                                     @QueryParam("sellCode") String sellCode) {
        try {
            trcBiz.checkChannelCode(channelCode);
            trcBiz.checkSellCode(sellCode);
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "代发sku列表信息查询成功", trcBiz.externalItemSkuPage(form, page,channelCode));
        } catch (Exception e) {
            logger.error("查询externalItemSku列表信息报错", e);
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("代发sku列表信息查询失败,%s", e.getMessage()), "");
        }
    }

    //查询店铺下的京东物流
    @GET
    @Path(SupplyConstants.TaiRan.JD_LOGISTICS)
    @Produces("application/json;charset=utf-8")
    public ResponseAck JDLogistics(@QueryParam("channelCode")String channelCode, @QueryParam("shopOrderCode")String shopOrderCode) throws Exception{
        return scmOrderBiz.getJDLogistics(channelCode, shopOrderCode);
    }

    @POST
    @Path(SupplyConstants.TaiRan.JD_SKUSTOCK)
    @Produces("application/json;charset=utf-8")
    public ResponseAck getSkuStockQuery(String jsonObject) throws Exception{
        return scmOrderBiz.getSkuStockQuery(jsonObject);
    }


    /**
     * 查询退货仓库列表
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.TaiRan.RETURN_WAREHOUSE)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<List<SupplyConstants.WarehouseInfo>> returnWarehouseQuery() throws Exception {
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "查询退货仓库成功", trcBiz.returnWarehouseQuery());
    }
    
    /**
     * 创建售后单接口
     */
    @POST
    @Path(SupplyConstants.TaiRan.AFTER_SALE_CREATE)
    @Produces("application/json;charset=utf-8")
    public ResponseAck afterSaleCreate(String afterSaleOrder) throws Exception{
        AssertUtil.notBlank(afterSaleOrder, "请求参数不能为空");
        TairanAfterSaleOrderDO afterSaleOrderDO=null;
        try{
             afterSaleOrderDO=JSONObject.parseObject(afterSaleOrder,TairanAfterSaleOrderDO.class);
        }catch(Exception e){
            logger.error("参数转json格式错误", e);
            return new ResponseAck(CommonExceptionEnum.PARAM_CHECK_EXCEPTION.getCode(), String.format("请求参数%s不是json格式", afterSaleOrder), "");
        }
    	return trcBiz.afterSaleCreate(afterSaleOrderDO);


        //测试数据
//        TairanAfterSaleOrderDO as=new TairanAfterSaleOrderDO();
//        as.setRequestNo(new Date().getTime()+"");
//        as.setShopOrderCode("7774561469");
//        as.setReturnScene(1);
//        as.setAfterSaleType(1);
//        as.setReturnWarehouseCode("CK00273");
//
//        List<TaiRanAfterSaleOrderDetail> list=new ArrayList<>();
//        TaiRanAfterSaleOrderDetail detail=new TaiRanAfterSaleOrderDetail();
//        detail.setSkuCode("SP0201808070000833");
//        detail.setRefundAmont(new BigDecimal(1));
//        list.add(detail);
//
//        as.setAfterSaleOrderDetailList(list);
//
//        trcBiz.afterSaleCreate(as);
//        return new ResponseAck("200","24","234");

    }

    /**
     * 取消售后单接口
     */
    @POST
    @Path(SupplyConstants.TaiRan.CANCEL_AFTER_SALE_ORDER)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Map<String, Object>> cancelAfterSaleOrder(String afterSaleCode) {
        Map map=null;
        try{
             map=JSONObject.parseObject(afterSaleCode,Map.class);
        }catch(Exception e){
            logger.error("参数转json格式错误", e);
            return new ResponseAck(CommonExceptionEnum.PARAM_CHECK_EXCEPTION.getCode(), String.format("请求参数%s不是json格式", afterSaleCode), "");
        }

    	 return new ResponseAck(ResponseAck.SUCCESS_CODE, "取消售后单接收成功", trcBiz.cancelAfterSaleOrder((String) map.get("afterSaleCode")));
    }

    /**
     * 提交物流单号接口
     */
    @POST
    @Path(SupplyConstants.TaiRan.SUBMIT_WAYBILL)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<String> submitWaybill(String waybillMessage)  throws Exception{
        AssertUtil.notBlank(waybillMessage, "请求参数不能为空");
        try {
            AfterSaleWaybillForm afterSaleWaybillForm = JSONObject.parseObject(waybillMessage,AfterSaleWaybillForm.class);
            trcBiz.submitWaybill(afterSaleWaybillForm);
        } catch (JSONException e) {
            logger.error("参数转json格式错误", e);
            return new ResponseAck(CommonExceptionEnum.PARAM_CHECK_EXCEPTION.getCode(), String.format("请求参数%s不是json格式", waybillMessage), "");
        }
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "物流信息接收成功", "");
    }


    @GET
    @Path(SupplyConstants.TaiRan.AFTER_SALE_ORDER_STATUS)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<AfterSaleOrderStatusResponse> afterSaleOrderStatus(@QueryParam("afterSaleCode") String afterSaleCode){
        try {
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "售后单状态查询成功", afterSaleOrderBiz.afterSaleOrderStatus(afterSaleCode));
        } catch (Exception e) {
            logger.error("售后单状态查询异常", e);
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("售后单%s状态查询异常,%s", afterSaleCode, e.getMessage()), "");
        }

    }

}

