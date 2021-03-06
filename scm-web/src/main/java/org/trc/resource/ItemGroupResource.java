package org.trc.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.trc.biz.goods.IitemGroupBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.ItemGroup;
import org.trc.domain.goods.ItemGroupUser;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.goods.ItemGroupForm;
import org.trc.form.goods.ItemGroupFormEdit;
import org.trc.form.goods.ItemGroupQuery;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hzgjl on 2018/7/26.
 */
@Component
@Api(value = "商品组管理")
@Path(SupplyConstants.ItemGroupConstants.ROOT)
public class ItemGroupResource {

    @Resource
    private IitemGroupBiz itemGroupBiz;

    @GET
    @Path(SupplyConstants.ItemGroupConstants.ITEM_GROUP_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "商品组分页查询")
    @ApiImplicitParam(paramType = "query", dataType = "String", name = "itemGroupName", value = "商品组编号", required = false)
    public  Response itemGroupPage(@BeanParam ItemGroupQuery itemGroupQuery, @BeanParam Pagenation<ItemGroup> page, @Context ContainerRequestContext requestContext){
        Pagenation<ItemGroup> pagenation = itemGroupBiz.itemGroupPage(itemGroupQuery, page, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessPageResult(pagenation);
    }

    @POST
    @Path(SupplyConstants.ItemGroupConstants.ITEM_GROUP_SAVE)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "新增商品组")
    public Response itemGroupSave(@Valid ItemGroupForm form, @Context ContainerRequestContext requestContext){
            itemGroupBiz.itemGroupSave(form,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("商品组新增成功","");

    }


    @GET
    @Path(SupplyConstants.ItemGroupConstants.ITEM_GROUP_DETAIL_QUERY)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据商品组编码查询详情")
    @ApiImplicitParam(paramType = "query", dataType = "String", name = "itemGroupCode", value = "商品组编号", required = true)
    public Response queryDetailByCode(@QueryParam("itemGroupCode") String itemGroupCode){
        ItemGroup itemGroup = itemGroupBiz.queryDetailByCode(itemGroupCode);
        return ResultUtil.createSuccessResult("根据商品组编码查询详情成功",itemGroup);

    }

    //编辑和查询商品组详情时均调用该接口
    @GET
    @Path(SupplyConstants.ItemGroupConstants.ITEM_GROUP_USERS_QUERY)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据商品组编码查询组员信息")
    @ApiImplicitParam(paramType = "query", dataType = "String", name = "itemGroupCode", value = "商品组编号", required = true)
    public Response queryItemGroupUserListByCode(@QueryParam("itemGroupCode") String itemGroupCode){
        List<ItemGroupUser> list = itemGroupBiz.queryItemGroupUserListByCode(itemGroupCode);
        return ResultUtil.createSuccessResult("根据商品组编码查询组员信息成功",list);

    }

    @PUT
    @Path(SupplyConstants.ItemGroupConstants.ITEM_GROUP_EDIT)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据商品组编码编辑详情")
    public Response editDetail(@Valid ItemGroupForm form , @Context ContainerRequestContext requestContext){
            itemGroupBiz.editDetail(form, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("商品组编辑操作成功", "");
    }


    @PUT
    @Path(SupplyConstants.ItemGroupConstants.ITEM_GROUP_ISVALID)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "启停用")
    public Response updateStatus(@BeanParam ItemGroupFormEdit formEdit, @Context ContainerRequestContext requestContext){
        String itemGroupCode=formEdit.getItemGroupCode();
        String isValid = formEdit.getIsValid();
        itemGroupBiz.updateStatus(isValid,itemGroupCode,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("商品组停用操作成功","");
    }

}
