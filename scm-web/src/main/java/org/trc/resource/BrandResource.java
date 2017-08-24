package org.trc.resource;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.category.IBrandBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ValidEnum;
import org.trc.enums.remarkEnum;
import org.trc.form.category.BrandForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzqph on 2017/5/2.
 */
@Component
@Path(SupplyConstants.Category.ROOT)
public class BrandResource {

    @Autowired
    private IBrandBiz brandBiz;

    @GET
    @Path(SupplyConstants.Category.Brand.BRAND_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response brandPage(@BeanParam BrandForm form,@BeanParam Pagenation<Brand> page) throws Exception {
        return ResultUtil.createSuccessPageResult(brandBiz.brandPage(form,page));
    }

    @GET
    @Path(SupplyConstants.SelectList.VALID_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryValidList(){
        return ResultUtil.createSuccessResult("成功", ValidEnum.toJSONArray());
    }

    @GET
    @Path(SupplyConstants.Category.Brand.ASSOCIATION_SEARCH)
    @Produces(MediaType.APPLICATION_JSON)
    public Response associationSearch(@QueryParam("queryString") String queryString) throws Exception{
        return ResultUtil.createSuccessResult("成功", brandBiz.associationSearch(queryString));
    }

    /**
     *
     * @param brand
     * @return
     * @throws Exception
     */
    @POST
    @Path(SupplyConstants.Category.Brand.BRAND)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveBrand(@BeanParam Brand brand , @Context ContainerRequestContext requestContext) throws Exception{
        brandBiz.saveBrand(brand,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("保存品牌成功", "");
    }

    @GET
    @Path(SupplyConstants.Category.Brand.BRAND +"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findBrandById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSuccessResult("查询品牌成功", brandBiz.findBrandById(id));
    }

    @GET
    @Path(SupplyConstants.Category.Brand.BRAND_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryBrands(@BeanParam BrandForm form) throws Exception{
        return ResultUtil.createSuccessResult("查询品牌列表成功", brandBiz.queryBrands(form));
    }


    @PUT
    @Path(SupplyConstants.Category.Brand.BRAND +"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBrand(@BeanParam Brand brand, @Context ContainerRequestContext requestContext) throws Exception{
        brandBiz.updateBrand(brand, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("更新品牌成功", "");
    }

    @PUT
    @Path(SupplyConstants.Category.Brand.BRAND_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBrandStatus(@BeanParam Brand brand, @Context ContainerRequestContext requestContext)throws Exception{
        brandBiz.updateBrandStatus(brand,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        ValidEnum validEnum=ValidEnum.VALID;
        if (brand.getIsValid().equals(ValidEnum.VALID.getCode())) {
            validEnum=ValidEnum.NOVALID;
        }
        String msg=validEnum.getName()+"成功!";
        return ResultUtil.createSuccessResult(msg, "");
    }
}
