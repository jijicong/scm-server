package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.category.IPropertyBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.category.PropertyForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hzqph on 2017/5/6.
 */
@Component
@Path(SupplyConstants.Category.ROOT)
public class PropertyResource {
    @Autowired
    private IPropertyBiz propertyBiz;

    @GET
    @Path(SupplyConstants.Category.Property.PROPERTY_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response propertyPage(@BeanParam PropertyForm form, @BeanParam Pagenation<Property> page) throws Exception {
        return ResultUtil.createSuccessPageResult(propertyBiz.propertyPage(form,page));
    }

    @POST
    @Path(SupplyConstants.Category.Property.PROPERTY)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveProperty(@BeanParam Property property, @Context ContainerRequestContext requestContext)throws Exception{
        propertyBiz.saveProperty(property, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("属性保存成功","");
    }

    @PUT
    @Path(SupplyConstants.Category.Property.PROPERTY +"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProperty(@BeanParam Property property, @Context ContainerRequestContext requestContext) throws Exception{
        propertyBiz.updateProperty(property,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("属性更改成功","");
    }

    @GET
    @Path(SupplyConstants.Category.PropertyValue.PROPERTY_VALUE_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryPropertyValueList(@QueryParam("propertyId") Long propertyId) throws Exception {
        return ResultUtil.createSuccessResult("属性值列表查询成功",propertyBiz.queryListByPropertyId(propertyId));
    }

    @GET
    @Path(SupplyConstants.Category.Property.PROPERTY+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPropertyById(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSuccessResult("属性查询成功",propertyBiz.findPropertyById(id));
    }

    @PUT
    @Path(SupplyConstants.Category.Property.PROPERTY_STATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBrandStatus(@BeanParam Property Property, @Context ContainerRequestContext requestContext)throws Exception{
        propertyBiz.updatePropertyStatus(Property,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("更新属性状态成功", "");
    }

    @GET
    @Path(SupplyConstants.Category.Property.PROPERTY_ALL)
    @Produces(MediaType.APPLICATION_JSON)
    public Response propertySearch(@QueryParam("queryString") String queryString) throws Exception {
        return ResultUtil.createSuccessResult("查询所有属性成功",propertyBiz.searchProperty(queryString));
    }

    @GET
    @Path(SupplyConstants.Category.PropertyValue.MULTI_PROPERTY_ID_SEARCH_PROPERTY_VALUE_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryPropertyValueByPropertyIds(@QueryParam("propertyIds") String propertyIds) throws Exception {
        return ResultUtil.createSuccessResult("查询所有属性成功",propertyBiz.queryPropertyValueByPropertyIds(propertyIds));
    }


}
