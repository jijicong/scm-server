package org.trc.resource;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.goods.IitemGroupBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.ItemGroup;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.goods.ItemGroupForm;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzgjl on 2018/7/26.
 */
@Component
@Path(SupplyConstants.ItemGroupConstants.ROOT)
public class ItemGroupResource {

    private Logger log = LoggerFactory.getLogger(ItemGroupResource.class);

    @Autowired
    private IitemGroupBiz itemGroupBiz;

    @GET
    @Path(SupplyConstants.ItemGroupConstants.ROOT)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "商品组分页查询")
    public Response itemGroupPage(@BeanParam ItemGroupForm form, @BeanParam Pagenation<ItemGroup> page, @Context ContainerRequestContext requestContext){
        return ResultUtil.createSuccessPageResult(itemGroupBiz.itemGroupPage(form,page,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @GET
    @Path(SupplyConstants.ItemGroupConstants.ITEM_GROUP_DETAIL_QUERY)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据商品组编码查询详情")
    public Response queryDetailByCode(@QueryParam("itemGroupCode") String itemGroupCode){
        return ResultUtil.createSuccessResult("商品组查询成功",itemGroupBiz.queryDetailByCode(itemGroupCode));

    }

    @PUT
    @Path(SupplyConstants.ItemGroupConstants.ITEM_GROUP_EDIT)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据商品组编码编辑详情")
    public Response editDetail(@BeanParam ItemGroup itemGroup){
        itemGroupBiz.editDetail(itemGroup);
        return ResultUtil.createSuccessResult("商品组编辑成功","");

    }

    @POST
    @Path(SupplyConstants.ItemGroupConstants.ITEM_GROUP_SAVE)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "新增商品组")
    public Response itemGroupSave(@BeanParam ItemGroup itemGroup){
        itemGroupBiz.itemGroupSave(itemGroup);
        return ResultUtil.createSuccessResult("商品组新增成功","");

    }
}
