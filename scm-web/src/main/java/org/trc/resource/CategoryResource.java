package org.trc.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.category.ICategoryBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Category;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.domain.category.CategoryProperty;
import org.trc.domain.category.Property;
import org.trc.enums.SourceEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.category.CategoryBrandForm;
import org.trc.form.category.TableDate;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hzszy on 2017/5/10.
 */
@Component
@Path(SupplyConstants.Category.ROOT)
public class CategoryResource {
    @Autowired
    private ICategoryBiz categoryBiz;

    /**
     * 查询树
     *
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.Category.Classify.CATEGORY_TREE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONArray> classifyTree(@QueryParam("parentId") Long parentId, @QueryParam("isRecursive") boolean isRecursive) throws Exception {

        return ResultUtil.createSucssAppResult("成功", categoryBiz.getNodes(parentId, isRecursive));

    }

    /**
     * 新增分类
     *
     * @param category
     * @return
     * @throws Exception
     */
    @POST
    @Path(SupplyConstants.Category.Classify.CATEGORY)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveClassify(@BeanParam Category category) throws Exception {

        AssertUtil.notBlank(category.getCategoryCode(), "添加分类时categoryCode参数不能为空");
        AssertUtil.notNull(category.getSort(), "添加分类时sort参数不能为空");
        AssertUtil.notNull(category.getIsValid(), "添加分类时isValid参数不能为空");
        category.setCreateOperator("test");
        category.setSource(SourceEnum.TRC.getCode());
        category.setIsLeaf(ZeroToNineEnum.ONE.getCode());
        category.setCreateTime(Calendar.getInstance().getTime());
        category.setUpdateTime(Calendar.getInstance().getTime());
        categoryBiz.saveClassify(category);

        if (category.getLevel() == 1) {
            category.setFullPathId(null);
        } else if (category.getLevel() == 2) {
            category.setFullPathId(category.getParentId().toString());
        } else {
            category.setFullPathId(categoryBiz.queryPathId(category.getParentId()) + SupplyConstants.Symbol.FULL_PATH_SPLIT + category.getParentId());
        }
        categoryBiz.updateCategory(category);
        if (category.getLevel() != 1 && categoryBiz.isLeaf(category.getParentId()) != 0) {
            categoryBiz.updateIsLeaf(category);
        }
        return ResultUtil.createSucssAppResult("增加分类成功", "");
    }

    /**
     * 修改分类
     *
     * @param category
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.Category.Classify.CATEGORY + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateCategory(@BeanParam Category category) throws Exception {
        //判断是否叶子节点
        if (categoryBiz.isLeaf(category.getId()) == 0) {
            category.setIsLeaf(ZeroToNineEnum.ONE.getCode());
        } else {
            category.setIsLeaf(ZeroToNineEnum.ZERO.getCode());
        }
        categoryBiz.updateCategory(category);
        return ResultUtil.createSucssAppResult("修改分类成功", "");
    }

    /**
     * 查询分类是否存在
     *
     * @param categoryCode
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.Category.Classify.CATEGORY_CHECK + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult checkCategoryCode(@QueryParam("id") Long id, @QueryParam("categoryCode") String categoryCode) throws Exception {

        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据

        if (categoryBiz.checkCategoryCode(id, categoryCode) > 0) {
            return ResultUtil.createSucssAppResult("查询分类编码已存在", "");

        } else {

            return ResultUtil.createSucssAppResult("查询分类编码可用", "");
        }

    }

    /**
     * 排序，批量修改
     *
     * @param sortDate
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.Category.Classify.CATEGORY_SORT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateSort(String sortDate) throws Exception {
        AssertUtil.notBlank(sortDate, "排序信息为空");
        List<Category> categoryList = JSON.parseArray(sortDate, Category.class);
        categoryBiz.updateSort(categoryList);
        return ResultUtil.createSucssAppResult("更新排序成功", "");
    }

    @PUT
    @Path(SupplyConstants.Category.Classify.UPDATE_STATE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateCategoryState(@BeanParam Category category) throws Exception {
        categoryBiz.updateState(category);
        return ResultUtil.createSucssAppResult("状态修改成功", "");
    }

    //分类品牌
    @GET
    @Path(SupplyConstants.Category.CategoryBrands.CATEGORY_BAAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<CategoryBrandExt>> queryCategoryBrands(@BeanParam CategoryBrandForm categoryBrandForm) throws Exception {
        return ResultUtil.createSucssAppResult("查询分类品牌列表成功", categoryBiz.queryCategoryBrands(categoryBrandForm));
    }

    /**
     * 查询分类路径
     */
    @GET
    @Path(SupplyConstants.Category.Classify.CATEGORY_QUERY + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<String>> queryCategory(@PathParam("id") Long id) throws Exception {

        return ResultUtil.createSucssAppResult("查询分类路径名称成功", categoryBiz.queryCategoryNamePath(id));
    }

    @POST
    @Path(SupplyConstants.Category.CategoryBrands.CATEGORY_BRAND_LINK + "/{id}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult linkCategoryBrands(@PathParam("id") Long id, @FormParam("brandIds") String brandIds,@FormParam("delRecord") String delRecord) throws Exception {
        categoryBiz.linkCategoryBrands(id, brandIds,delRecord);
        return ResultUtil.createSucssAppResult("分类品牌关联成功", "");
    }

    //分类属性
    @GET
    @Path(SupplyConstants.Category.CategoryProperty.CATEGORY_PROPERTY_PAGE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<CategoryProperty>> queryCategoryProperty(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSucssAppResult("查询分类关联属性", categoryBiz.queryCategoryProperty(id));
    }

    @POST
    @Path(SupplyConstants.Category.CategoryProperty.CATEGORY_PROPERTY_LINK + "/{id}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult linkCategoryProperty(@PathParam("id") Long id, @FormParam("propertyId") Long propertyId) throws Exception {
        categoryBiz.linkCategoryProperty(id, propertyId);
        return ResultUtil.createSucssAppResult("分类属性关联成功", "");
    }

    @PUT
    @Path(SupplyConstants.Category.CategoryProperty.CATEGORY_PROPERTY_UPDATE + "/{id}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateCategoryProperty(@PathParam("id") Long id, @FormParam("jsonDate") String jsonDate) throws Exception {
        categoryBiz.updateCategoryProperty(id, jsonDate);
        return ResultUtil.createSucssAppResult("分类属性保存成功", "");
    }

}
