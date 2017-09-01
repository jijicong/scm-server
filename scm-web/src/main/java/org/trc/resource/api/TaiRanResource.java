package org.trc.resource.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.category.IPropertyBiz;
import org.trc.biz.goods.ISkuRelationBiz;
import org.trc.biz.impl.category.BrandBiz;
import org.trc.biz.impl.trc.model.Skus2;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.OrderException;
import org.trc.form.OrderSubmitResult;
import org.trc.form.category.CategoryForm;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.SkusForm;
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
import java.util.List;

/**
 * 对泰然城开放接口
 * Created by hzdzf on 2017/5/26.
 */
@Component
@Path(SupplyConstants.TaiRan.ROOT)
public class TaiRanResource {

    private Logger logger = LoggerFactory.getLogger(TaiRanResource.class);

    @Resource
    private BrandBiz brandBiz;
    @Resource
    private IPropertyBiz propertyBiz;
    @Resource
    private ICategoryBiz categoryBiz;
    @Resource
    private ITrcBiz trcBiz;
    @Resource
    private ISkuRelationBiz skuRelationBiz;
    @Resource
    private IScmOrderBiz scmOrderBiz;


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
    public ResponseAck<Pagenation<Brand>> queryBrand(@BeanParam BrandForm2 form, @BeanParam Pagenation<Brand> page) throws Exception{
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
    public ResponseAck<Pagenation<Supplier>> supplierPage(@BeanParam Pagenation<Supplier> page, @Context ContainerRequestContext requestContext, @BeanParam SupplierForm form) throws Exception {
        try {
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "供应商分页查询成功", trcBiz.supplierPage(form,page));
        } catch (Exception e) {
            logger.error("供应商分页查询报错: " + e.getMessage());
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
    public ResponseAck<Object> queryProperty(@BeanParam PropertyFormForTrc form, @BeanParam Pagenation<Property> page){
        try {//Pagenation<Property>
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "属性查询成功", trcBiz.propertyPage(form, page));
        } catch (Exception e) {
            logger.error("查询列表信息报错: " + e.getMessage());
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
    public ResponseAck<Pagenation<Category>> queryCategory(@BeanParam CategoryForm2 categoryForm, @BeanParam Pagenation<Category> page) throws Exception {
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
    public ResponseAck<List<CategoryBrand>> queryCategoryBrand(@QueryParam("categoryId") Long categoryId) throws Exception {
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
    public ResponseAck<List<CategoryProperty>> queryCategoryProperty(@QueryParam("categoryId") Long categoryId) throws Exception {
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
    public ResponseAck<Object> getSpuInformations(@QueryParam("skuCode") String skuCode) {
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "sku信息查询成功", skuRelationBiz.getSkuInformation(skuCode));
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
    public ResponseAck<Object> getExternalSkuInformations(@QueryParam("skuCode") String skuCode) {
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "sku信息查询成功", skuRelationBiz.getExternalSkuInformation(skuCode));
    }


    @POST
    @Path(SupplyConstants.TaiRan.ORDER_PROCESSING)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<String> reciveChannelOrder(String orderInfo) {
        ResponseAck responseAck = null;
        try{
            responseAck = scmOrderBiz.reciveChannelOrder(orderInfo);
        }catch (Exception e){
            String code = ExceptionUtil.getErrorInfo(e);
            responseAck = new ResponseAck(code, e.getMessage(), "");
            logger.error(String.format("接收渠道同步订单%s异常", orderInfo), e);
        }finally {
            scmOrderBiz.saveChannelOrderRequestFlow(orderInfo, responseAck);
        }
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
    public ResponseAck<Pagenation<Items>> itemList(@BeanParam ItemsForm2 form, @BeanParam Pagenation<Items> page) throws Exception {
        try {
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "自采商品查询成功", trcBiz.itemsPage(form, page));
        } catch (Exception e) {
            logger.error("自采商品查询报错: " + e.getMessage());
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("自采商品查询报错,%s", e.getMessage()), "");
        }
    }


    //自采商品SKU信息查询
    @GET
    @Path(SupplyConstants.TaiRan.SKUS_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Pagenation<Skus2>> skusList(@BeanParam SkusForm skusForm, @BeanParam Pagenation<Skus> pagenation) {
        try {
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "sku列表查询信息成功", trcBiz.skusPage(skusForm, pagenation));
        } catch (Exception e) {
            logger.error("查询sku列表信息报错: " + e.getMessage());
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("查询sku列表信息报错,%s", e.getMessage()), "");
        }
    }


    //一件代发商品信息查询
    @GET
    @Path(SupplyConstants.TaiRan.EXTERNALITEMSKU_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Pagenation<ExternalItemSku>> externalItemSkus(@BeanParam ExternalItemSkuForm form, @BeanParam Pagenation<ExternalItemSku> page) {
        try {
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "代发sku列表信息查询成功", trcBiz.externalItemSkuPage(form, page));
        } catch (Exception e) {
            logger.error("查询externalItemSku列表信息报错: " + e.getMessage());
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


}

