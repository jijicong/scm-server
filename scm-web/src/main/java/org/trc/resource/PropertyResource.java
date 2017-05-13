package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.category.IPropertyBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.category.Property;
import org.trc.domain.category.PropertyValue;
import org.trc.form.category.PropertyForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
    public Pagenation<Property> propertyPage(@BeanParam PropertyForm form, @BeanParam Pagenation<Property> page) throws Exception {
        return propertyBiz.propertyPage(form,page);
    }

    @POST
    @Path(SupplyConstants.Category.Property.PROPERTY)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveProperty(@BeanParam Property property)throws Exception{
        propertyBiz.saveProperty(property);
        return ResultUtil.createSucssAppResult("属性保存成功","");
    }

    @PUT
    @Path(SupplyConstants.Category.Property.PROPERTY +"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateProperty(@BeanParam Property property) throws Exception{
        propertyBiz.updateProperty(property);
        return ResultUtil.createSucssAppResult("属性更改成功","");
    }

    @GET
    @Path(SupplyConstants.Category.PropertyValue.PROPERTY_VALUE_LIST)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<PropertyValue>> queryPropertyValueList(@QueryParam("propertyId") Long propertyId) throws Exception {
        return ResultUtil.createSucssAppResult("属性值列表查询成功",propertyBiz.queryListByPropertyId(propertyId));
    }

    @GET
    @Path(SupplyConstants.Category.Property.PROPERTY+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Property> findPropertyById(@PathParam("id") Long id) throws Exception {
        return ResultUtil.createSucssAppResult("属性查询成功",propertyBiz.findPropertyById(id));
    }

    @POST
    @Path(SupplyConstants.Category.Property.PROPERTY_STATE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateBrandStatus(@BeanParam Property Property)throws Exception{
        propertyBiz.updatePropertyStatus(Property);
        return ResultUtil.createSucssAppResult("更新属性状态成功", "");
    }
}
