package org.trc.resource.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.category.IPropertyBiz;
import org.trc.biz.goods.ISkuBiz;
import org.trc.biz.goods.ISkuRelationBiz;
import org.trc.biz.impl.category.BrandBiz;
import org.trc.biz.order.IScmOrderBiz;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.goods.Skus;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.BrandException;
import org.trc.exception.ParamValidException;
import org.trc.form.category.BrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.form.category.PropertyForm;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.SkusForm;
import org.trc.util.*;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
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
    private ISkuBiz skuBiz;
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
    public ResponseAck<Pagenation<Brand>> queryBrand(@BeanParam BrandForm form, @BeanParam Pagenation<Brand> page) throws Exception{
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "品牌查询成功", brandBiz.brandList(form, page));
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
    public ResponseAck<Pagenation<Property>> queryProperty(@BeanParam PropertyForm form, @BeanParam Pagenation<Property> page) throws Exception{
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "属性查询成功", propertyBiz.propertyPage(form, page));
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
    public ResponseAck<Pagenation<Category>> queryCategory(@BeanParam CategoryForm categoryForm, @BeanParam Pagenation<Category> page) throws Exception {
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "分类查询成功", categoryBiz.categoryPage(categoryForm, page));
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
     * 查询单个sku信息
     *
     * @param skuCode 传递供应链skuCode
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.SKU_INFORMATION)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Object> getSpuInformation(@QueryParam("skuCode") String skuCode) {
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "sku信息查询成功", skuRelationBiz.getSkuInformation(skuCode));
    }

    @POST
    @Path(SupplyConstants.TaiRan.ORDER_PROCESSING)
    @Produces("application/json;charset=utf-8")
    @Consumes(MediaType.TEXT_PLAIN)
    public ResponseAck<String> getOrderList(String information) {
        ResponseAck responseAck = null;
        try{
            responseAck = scmOrderBiz.reciveChannelOrder(information);
        }catch (Exception e){
            String code = ExceptionUtil.getErrorInfo(e);
            responseAck = new ResponseAck(code, e.getMessage(), "");
            logger.error(String.format("接收渠道同步订单%s异常,%s", information, e));
        }finally {
            scmOrderBiz.saveChannelOrderRequestFlow(information, responseAck);
        }
        return responseAck;
    }


    //批量新增关联关系（单个也调用此方法），待修改
    @POST
    @Path(SupplyConstants.TaiRan.SKURELATION_UPDATE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<String> addSkuRelationBatch(JSONObject information) {
        AssertUtil.isTrue(information.containsKey("action"), "参数action不能为空");
        AssertUtil.isTrue(information.containsKey("relations"), "参数relations不能为空");
        String action = information.getString("action");
        JSONArray relations = information.getJSONArray("relations");
        try {
            trcBiz.updateRelation(action, relations);
        } catch (Exception e) {
            String code = ExceptionUtil.getErrorInfo(e);
            return new ResponseAck(code, String.format("关联信息更新失败,%s", e.getMessage()), "");
        }
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "关联信息更新成功", "");
    }

    //自采商品信息查询
    @GET
    @Path(SupplyConstants.TaiRan.SKUS_LIST)
    @Produces("application/json;charset=utf-8")
    public ResponseAck<Pagenation<Skus>> getSkus(@BeanParam SkusForm skusForm, @BeanParam Pagenation<Skus> pagenation) {
        try {
            return new ResponseAck(ResponseAck.SUCCESS_CODE, "sku列表查询信息成功", skuBiz.skusPage(skusForm, pagenation));
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
    public ResponseAck<Pagenation<ExternalItemSku>> getExternalItemSkus(@BeanParam ExternalItemSkuForm form, @BeanParam Pagenation<ExternalItemSku> page) {
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
    public ResponseAck JDLogistics(@QueryParam("shopOrderCode")String shopOrderCode) throws Exception{
        return new ResponseAck(ResponseAck.SUCCESS_CODE, "京东物流信息查询成功", scmOrderBiz.getJDLogistics(shopOrderCode));
    }


}

