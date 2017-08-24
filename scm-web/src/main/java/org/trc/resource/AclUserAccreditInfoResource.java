package org.trc.resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impower.IAclUserAccreditInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.impower.AclUserAddPageDate;
import org.trc.enums.ValidEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by sone on 2017/5/11.
 */
@Component
@Path(SupplyConstants.UserAccreditInfo.ROOT)
public class AclUserAccreditInfoResource {

    @Autowired
    private IAclUserAccreditInfoBiz userAccreditInfoBiz;

    //授权信息分页查询
    @GET
    @Path(SupplyConstants.UserAccreditInfo.ACCREDIT_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response UserAccreditInfoPage(@BeanParam UserAccreditInfoForm form, @BeanParam Pagenation<AclUserAddPageDate> page) {
        return ResultUtil.createSuccessPageResult(userAccreditInfoBiz.userAccreditInfoPage(form, page));
    }

    //授权里面的采购员列表
    @GET
    @Path(SupplyConstants.UserAccreditInfo.PURCHASE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPurchase(@Context ContainerRequestContext requestContext) {
        return ResultUtil.createSuccessResult("查询采购员成功", userAccreditInfoBiz.findPurchase((AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO)));
    }

    @PUT
    @Path(SupplyConstants.UserAccreditInfo.UPDATE_STATE + "/{id}")
    public Response updateUserAccreditInfoStatus(@BeanParam AclUserAccreditInfo aclUserAccreditInfo, @Context ContainerRequestContext requestContext) {
        userAccreditInfoBiz.updateUserAccreditInfoStatus(aclUserAccreditInfo, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        String _valid = ZeroToNineEnum.ZERO.getCode();
        if (StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), aclUserAccreditInfo.getIsValid())) {
            _valid = ZeroToNineEnum.ONE.getCode();
        }
        return ResultUtil.createSuccessResult(String.format("%s成功！", ValidEnum.getValidEnumByCode(_valid).getName()), "");
    }

   /* *//**
     * 编辑时采购组校验
     *
     * @param id
     * @param name
     * @return
     *//*
    @GET
    @Path(SupplyConstants.AclUserAccreditInfo.CHECK + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserAccreditInfoByName(@QueryParam("id") Long id, @QueryParam("name") String name) throws Exception {
        userAccreditInfoBiz.checkUserByName(id, name);
        return null;
    }*/
  /*  *//**
     * 新增时用户名是否存在
     * @param name
     * @return
     *//*
    @GET
    @Path(SupplyConstants.AclUserAccreditInfo.CHECK)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkName(@QueryParam("name") String name) throws Exception {
        if (userAccreditInfoBiz.checkName( name) >0) {
            return ResultUtil.createSuccessResult("查询成功", "查询授权用户已存在");
        } else {
            return ResultUtil.createSuccessResult("查询成功", "查询授权用户可用");
        }
    }*/

    /**
     * 查询已启用的渠道
     */
    @GET
    @Path(SupplyConstants.UserAccreditInfo.CHANNEL)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findChannel() {
        return ResultUtil.createSuccessResult("查询已启用的渠道成功", userAccreditInfoBiz.findChannel());

    }

    /**
     * 查询全局&渠道&混用角色
     */
    @GET
    @Path(SupplyConstants.UserAccreditInfo.ROLE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findChaAndWhole(@QueryParam("roleType") String roleType) {

        return ResultUtil.createSuccessResult("查询对应角色成功", userAccreditInfoBiz.findChannelOrWholeJur(roleType));

    }

    /**
     * 新增授权
     */
    @POST
    @Path(SupplyConstants.UserAccreditInfo.SAVE_ACCREDIT)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveUserAccredit(@BeanParam AclUserAddPageDate userAddPageDate, @Context ContainerRequestContext requestContext) {

        userAccreditInfoBiz.saveUserAccreditInfo(userAddPageDate, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("新增授权成功", "");
    }

    /**
     * 根据ID查询用户
     */
    @GET
    @Path(SupplyConstants.UserAccreditInfo.ACCREDIT + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserAccreditInfoById(@QueryParam("id") Long id) {
        return ResultUtil.createSuccessResult("查询用户成功", userAccreditInfoBiz.findUserAccreditInfoById(id));
    }

    /**
     * 修改用户
     *
     * @param userAddPageDate
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.UserAccreditInfo.UPDATE_ACCREDIT + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserAccreditInfo(@BeanParam AclUserAddPageDate userAddPageDate, @Context ContainerRequestContext requestContext) {
        userAccreditInfoBiz.updateUserAccredit(userAddPageDate, (AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return ResultUtil.createSuccessResult("修改用户成功", "");
    }

    @GET
    @Path(SupplyConstants.UserAccreditInfo.CHECK_PHONE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkPhone(@QueryParam("phone") String phone) {
        AssertUtil.notBlank(phone, "校验手机号时输入参数phone为空");
        return ResultUtil.createSuccessResult("查询成功", userAccreditInfoBiz.checkPhone(phone));
    }

    @GET
    @Path(SupplyConstants.UserAccreditInfo.NAME_PHONE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNameByPhone(@QueryParam("phone") String phone) {
        AssertUtil.notBlank(phone, "手机号时输入参数phone为空");
        return ResultUtil.createSuccessResult("查询成功", userAccreditInfoBiz.getNameByPhone(phone));
    }

    @GET
    @Path(SupplyConstants.UserAccreditInfo.CHECK_PURCHASE + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkPurchase(@PathParam("id") Long id) {
        return ResultUtil.createSuccessResult("查询成功", userAccreditInfoBiz.purchaseRole(id));
    }

    /**
     * 查询用户关联的角色起停用状态
     */
    @GET
    @Path(SupplyConstants.UserAccreditInfo.ROLE_VALID + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkRoleValid(@PathParam("id") Long id) {
        return ResultUtil.createSuccessResult("查询成功", userAccreditInfoBiz.checkRoleValid(id));
    }
}
