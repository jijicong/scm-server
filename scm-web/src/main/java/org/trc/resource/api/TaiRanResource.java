package org.trc.resource.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.category.IPropertyBiz;
import org.trc.biz.impl.category.BrandBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.category.Category;
import org.trc.domain.category.Property;
import org.trc.form.category.BrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.form.category.PropertyForm;
import org.trc.util.Pagenation;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * 对泰然城开放接口
 * Created by hzdzf on 2017/5/26.
 */
@Path(SupplyConstants.TaiRan.ROOT)
public class TaiRanResource {

    private static final Logger logger = LoggerFactory.getLogger(TaiRanResource.class);

    @Resource
    private BrandBiz brandBiz;

    @Resource
    private IPropertyBiz propertyBiz;

    @Resource
    private ICategoryBiz categoryBiz;

    @GET
    @Path(SupplyConstants.TaiRan.BRAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryBrand(@BeanParam BrandForm form, @BeanParam Pagenation<Brand> page) {

        try {
            logger.info("查询条件：   " + JSON.toJSONString(form));
            page = brandBiz.brandPage(form, page);
            JSONObject jsonObject = new JSONObject();
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
            jsonObject.put("pageNo", page.getPageNo());
            jsonObject.put("pageSize", page.getPageSize());
            jsonObject.put("totalCount", page.getTotalCount());
            jsonObject.put("list", list);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            logger.error("查询品牌列表报错---" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SupplyConstants.Comment.STATUS, 0);
            jsonObject.put(SupplyConstants.Comment.MSG, "查询品牌列表报错");
            return jsonObject.toJSONString();
        }
    }

    @GET
    @Path(SupplyConstants.TaiRan.PROPERTY_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryProperty(@BeanParam PropertyForm form, @BeanParam Pagenation<Property> page) {
        try {
            logger.info("查询条件：   " + JSON.toJSONString(form));
            page = propertyBiz.propertyPage(form, page);
            JSONObject jsonObject = new JSONObject();
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
            jsonObject.put("pageNo", page.getPageNo());
            jsonObject.put("pageSize", page.getPageSize());
            jsonObject.put("totalCount", page.getTotalCount());
            jsonObject.put("list", list);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            logger.error("查询属性列表报错---" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SupplyConstants.Comment.STATUS, 0);
            jsonObject.put(SupplyConstants.Comment.MSG, "查询属性列表报错");
            return jsonObject.toJSONString();
        }
    }

    @GET
    @Path(SupplyConstants.TaiRan.CATEGORY_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public String queryCategory(@BeanParam CategoryForm categoryForm, @BeanParam Pagenation<Category> page) {
        try {
            logger.info("查询条件：   " + JSON.toJSONString(categoryForm));
            page = categoryBiz.CategoryPage(categoryForm, page);
            JSONObject jsonObject = new JSONObject();
            List<Category> list = new ArrayList<Category>();
            for (Category category : page.getResult()) {
                Category category1 = new Category();
                category1.setName(category.getName());
                category1.setSort(category.getSort());
                category1.setIsValid(category.getIsValid());
                category1.setUpdateTime(category.getUpdateTime());
                if (!(category.getParentId() == null)) {
                    category1.setParentId(category.getParentId());
                }
                category1.setClassifyDescribe(category.getClassifyDescribe() == null ? "" : category.getClassifyDescribe());
                category1.setLevel(category.getLevel());
                list.add(category1);
            }
            jsonObject.put("pageNo", page.getPageNo());
            jsonObject.put("pageSize", page.getPageSize());
            jsonObject.put("totalCount", page.getTotalCount());
            jsonObject.put("list", list);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            logger.error("查询分类列表报错---" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(SupplyConstants.Comment.STATUS, 0);
            jsonObject.put(SupplyConstants.Comment.MSG, "查询分类列表报错");
            return jsonObject.toJSONString();
        }
    }
}
