package org.trc.resource;

import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.category.ICategoryBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.category.Category;
import org.trc.domain.category.CategoryBrandExt;
import org.trc.domain.category.CategoryProperty;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.SuccessFailureEnum;
import org.trc.form.category.BrandForm;
import org.trc.form.category.CategoryBrandForm;
import org.trc.form.category.CategoryForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hzszy on 2017/5/10.
 */
@Component
@Path(SupplyConstants.Category.ROOT)
public class CategoryResource {
    private Logger log = LoggerFactory.getLogger(CategoryResource.class);


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
    public Response classifyTree(@QueryParam("parentId") Long parentId, @QueryParam("isRecursive") boolean isRecursive) throws Exception {
        return ResultUtil.createSuccessResult("成功", categoryBiz.getNodes(parentId, isRecursive));

    }

    @GET
    @Path(SupplyConstants.Category.CategoryBrands.BAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Brand> brandPage(@BeanParam BrandForm form, @BeanParam Pagenation<Brand> page) throws Exception {
        return categoryBiz.brandListCategory(form, page);
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
    public Response saveClassify(@BeanParam Category category, @Context ContainerRequestContext requestContext) throws Exception {
        categoryBiz.saveCategory(category, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("增加分类成功", "");
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
    public Response updateCategory(@BeanParam Category category, @Context ContainerRequestContext requestContext) throws Exception {
        categoryBiz.updateCategory(category, false, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("修改分类成功", "");
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
    public Response checkCategoryCode(@QueryParam("id") Long id, @QueryParam("categoryCode") String categoryCode) throws Exception {

        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据
        if (categoryBiz.checkCategoryCode(id, categoryCode) > 0) {
            return ResultUtil.createSuccessResult("查询分类编码已存在", "");
        } else {
            return ResultUtil.createSuccessResult("查询分类编码可用", "");
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
    public Response updateSort(String sortDate) throws Exception {
        categoryBiz.updateSort(sortDate);
        return ResultUtil.createSuccessResult("更新排序成功", "");
    }

    @PUT
    @Path(SupplyConstants.Category.Classify.UPDATE_STATE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategoryState(@BeanParam Category category, @Context ContainerRequestContext requestContext) throws Exception {
        categoryBiz.updateState(category, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("状态修改成功", "");
    }

    //分类品牌
    @GET
    @Path(SupplyConstants.Category.CategoryBrands.CATEGORY_BAAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryCategoryBrands(@BeanParam CategoryBrandForm categoryBrandForm) throws Exception {
        return ResultUtil.createSuccessResult("查询分类品牌列表成功", categoryBiz.queryCategoryBrands(categoryBrandForm));
    }

    /**
     * 查询分类路径
     */
    @GET
    @Path(SupplyConstants.Category.Classify.CATEGORY_QUERY + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryCategoryPathName(@PathParam("id") Long id) throws Exception {

        return ResultUtil.createSuccessResult("查询分类路径名称成功", categoryBiz.getCategoryName(id));
    }

    /**
     * 查询分类列表
     */
    @GET
    @Path(SupplyConstants.Category.Classify.CATEGORY_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryCategorys(@BeanParam CategoryForm categoryForm) throws Exception {
        return ResultUtil.createSuccessResult("查询分类列表成功", categoryBiz.queryCategorys(categoryForm));
    }


    @POST
    @Path(SupplyConstants.Category.CategoryBrands.CATEGORY_BRAND_LINK + "/{id}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response linkCategoryBrands(@PathParam("id") Long id, @FormParam("brandIds") String brandIds, @FormParam("delRecord") String delRecord, @Context ContainerRequestContext requestContext) throws Exception {

        Response response = ResultUtil.createSuccessResult("分类品牌关联成功", "");
        categoryBiz.linkCategoryBrands(id, brandIds, delRecord, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));

        return response;
    }

    //分类属性
    @GET
    @Path(SupplyConstants.Category.CategoryProperty.CATEGORY_PROPERTY_PAGE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryCategoryProperty(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSuccessResult("查询分类关联属性", categoryBiz.queryCategoryProperty(id));
    }

  /*  @POST
    @Path(SupplyConstants.Category.CategoryProperty.CATEGORY_PROPERTY_LINK + "/{id}")
    @Consumes("application/x-www-trc-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response linkCategoryProperty(@PathParam("id") Long id, @FormParam("propertyId") Long propertyId) throws Exception {
        categoryBiz.linkCategoryProperty(id, propertyId);
        return ResultUtil.createSuccessResult("分类属性关联成功", "");
    }*/

    @PUT
    @Path(SupplyConstants.Category.CategoryProperty.CATEGORY_PROPERTY_UPDATE + "/{id}")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response linkCategoryProperties(@PathParam("id") Long id, @FormParam("jsonDate") String jsonDate, @Context ContainerRequestContext requestContext) throws Exception {
        Response response = ResultUtil.createSuccessResult("分类属性关联成功", "");

        categoryBiz.linkCategoryProperties(id, jsonDate, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));

        return response;
    }

    /**
     * 启停校验
     */
    @GET
    @Path(SupplyConstants.Category.Classify.CATEGORY_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkCategoryIsValid(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSuccessResult("状态查询成功", categoryBiz.checkCategoryIsValid(id));
    }
}
