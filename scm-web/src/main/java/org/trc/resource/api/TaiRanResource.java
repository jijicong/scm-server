package org.trc.resource.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.txframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.category.IPropertyBiz;
import org.trc.biz.goods.ISkuRelationBiz;
import org.trc.biz.impl.category.BrandBiz;
import org.trc.biz.trc.IOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.goods.ExternalItemSku;
import org.trc.domain.order.*;
import org.trc.exception.TrcException;
import org.trc.form.category.BrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.form.category.PropertyForm;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkuRelationService;
import org.trc.service.order.*;
import org.trc.util.*;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

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
    private IRequestFlowService requestFlowService;

    @Resource
    private ISkuRelationBiz skuRelationBiz;

    @Resource
    private IOrderBiz orderBiz;



    /**
     * 分页查询品牌
     *
     * @param form
     * @param page
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.BRAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Pagenation<Brand>> queryBrand(@BeanParam BrandForm form, @BeanParam Pagenation<Brand> page) {

        try {
            page = brandBiz.brandList(form, page);
            List<Brand> list = new ArrayList<Brand>();
            for (Brand brand : page.getResult()) {
                Brand brand1 = new Brand();
                brand1.setName(brand.getName());
                brand1.setBrandCode(brand.getBrandCode() == null ? "" : brand.getBrandCode());
                brand1.setAlise(brand.getAlise() == null ? "" : brand.getAlise());
                brand1.setWebUrl(brand.getWebUrl() == null ? "" : brand.getWebUrl());
                brand1.setIsValid(brand.getIsValid());
                brand1.setUpdateTime(brand.getUpdateTime());
                brand1.setSort(brand.getSort());
                list.add(brand1);
            }
            page.setResult(list);
            return ResultUtil.createSucssAppResult("查询品牌列表成功", page);
        } catch (Exception e) {
            logger.error("查询品牌列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询品牌列表报错：" + e.getMessage());
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
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Pagenation<Property>> queryProperty(@BeanParam PropertyForm form, @BeanParam Pagenation<Property> page) {
        try {
            page = propertyBiz.propertyPage(form, page);
            List<Property> list = new ArrayList<Property>();
            for (Property property : page.getResult()) {
                Property property1 = new Property();
                property1.setName(property.getName());
                property1.setSort(property.getSort());
                property1.setTypeCode(property.getTypeCode());
                property1.setValueType(property.getValueType());
                property1.setIsValid(property.getIsValid());
                property1.setUpdateTime(property.getUpdateTime());
                list.add(property1);
            }
            page.setResult(list);
            return ResultUtil.createSucssAppResult("查询属性列表成功", page);
        } catch (Exception e) {
            logger.error("查询属性列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询属性列表报错：" + e.getMessage());
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
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Pagenation<Category>> queryCategory(@BeanParam CategoryForm categoryForm, @BeanParam Pagenation<Category> page) {
        try {
            page = categoryBiz.categoryPage(categoryForm, page);
            List<Category> list = new ArrayList<Category>();
            for (Category category : page.getResult()) {
                Category category1 = new Category();
                category1.setName(category.getName());
                category1.setSort(category.getSort());
                category1.setIsValid(category.getIsValid());
                category1.setUpdateTime(category.getUpdateTime());
                if (category.getParentId() != null) {
                    category1.setParentId(category.getParentId());
                }
                category1.setClassifyDescribe(category.getClassifyDescribe() == null ? "" : category.getClassifyDescribe());
                category1.setLevel(category.getLevel());
                list.add(category1);
            }
            page.setResult(list);
            return ResultUtil.createSucssAppResult("查询分类列表成功", page);
        } catch (Exception e) {
            logger.error("查询分类列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询分类列表报错：" + e.getMessage());
        }
    }

    /**
     * 查询分类品牌列表
     *
     * @param categoryId
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.CATEGORY_BRAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<CategoryBrand>> queryCategoryBrand(@QueryParam("categoryId") Long categoryId) {
        try {
            return ResultUtil.createSucssAppResult("查询分类品牌列表成功", categoryBiz.queryBrands(categoryId));
        } catch (Exception e) {
            logger.error("查询分类品牌列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询分类品牌列表报错：" + e.getMessage());
        }
    }

    /**
     * 查询分类属性列表
     *
     * @param categoryId
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.CATEGORY_PROPERTY_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<CategoryProperty>> queryCategoryProperty(@QueryParam("categoryId") Long categoryId) {
        try {
            return ResultUtil.createSucssAppResult("查询分类属性列表成功", categoryBiz.queryProperties(categoryId));
        } catch (Exception e) {
            logger.error("查询分类属性列表报错：" + e.getMessage());
            return ResultUtil.createFailAppResult("查询分类属性列表报错：" + e.getMessage());
        }
    }

    /**
     * 查询单个sku信息
     *
     * @param skuCode 传递供应链skuCode
     * @return
     */
    @GET
    @Path(SupplyConstants.TaiRan.SKU_INFORMATION)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<String> getSpuInformation(@QueryParam("skuCode") String skuCode) {
        try {
            return ResultUtil.createSucssAppResult("查询sku信息成功", skuRelationBiz.getSkuInformation(skuCode));
        } catch (Exception e) {
            logger.error("查询sku信息报错: " + e.getMessage());
            return ResultUtil.createFailAppResult("查询sku信息报错：" + e.getMessage());
        }
    }


    /**
     * 订单拆分，以仓库级订单传参
     *
     * @return
     */
    @POST
    @Path(SupplyConstants.TaiRan.ORDER_PROCESSING)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public AppResult<String> getOrderList(JSONObject information) {
        //获取平台订单信息X
        PlatformOrder platformOrder = JSONObject.parseObject(information.getJSONObject("platformOrder").toJSONString(), PlatformOrder.class);
        JSONArray shopOrders = information.getJSONArray("shopOrders");
        try{
            orderBiz.splitOrder(shopOrders,platformOrder);
        }catch (TrcException e){
            logger.error(e.getMessage());
            return ResultUtil.createFailAppResult("平台订单" + platformOrder.getPlatformOrderCode() + e.getMessage());
        }catch (Exception e){
            logger.error("订单处理报错: " + e.getMessage());
            return ResultUtil.createFailAppResult("平台订单" + platformOrder.getPlatformOrderCode() + " 订单处理报错：" + e.getMessage());
        }
        logger.info("平台订单推送成功");
        return ResultUtil.createSucssAppResult("平台订单" + platformOrder.getPlatformOrderCode() + " 订单推送成功，请等待后续通知", "");

    }


    public AppResult<String> getSkuRelation(){
        return null;
    }


    public AppResult<String> getSkuRelationBatch(){
        return null;
    }

}

