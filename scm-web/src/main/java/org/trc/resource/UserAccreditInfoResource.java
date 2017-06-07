package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.impower.IUserAccreditInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAddPageDate;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
@Component
@Path(SupplyConstants.UserAccreditInfo.ROOT)
public class UserAccreditInfoResource {

    @Resource
    private IUserAccreditInfoBiz userAccreditInfoBiz;

    //授权信息分页查询
    @GET
    @Path(SupplyConstants.UserAccreditInfo.ACCREDIT_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<UserAddPageDate> UserAccreditInfoPage(@BeanParam UserAccreditInfoForm form, @BeanParam Pagenation<UserAddPageDate> page) throws Exception {
        return userAccreditInfoBiz.userAccreditInfoPage(form, page);
    }

    //授权里面的采购员列表
    @GET
    @Path(SupplyConstants.UserAccreditInfo.PURCHASE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<List<UserAccreditInfo>> findPurchase() throws Exception {
        return ResultUtil.createSucssAppResult("查询采购员成功", userAccreditInfoBiz.findPurchase());
    }

    @POST
    @Path(SupplyConstants.UserAccreditInfo.UPDATE_STATE + "/{id}")
    public AppResult updateUserAccreditInfoStatus(@BeanParam UserAccreditInfo userAccreditInfo) throws Exception {
        userAccreditInfoBiz.updateUserAccreditInfoStatus(userAccreditInfo);
        return ResultUtil.createSucssAppResult("修改状态成功", "");
    }

   /* *//**
     * 编辑时采购组校验
     *
     * @param id
     * @param name
     * @return
     * @throws Exception
     *//*
    @GET
    @Path(SupplyConstants.UserAccreditInfo.CHECK + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findUserAccreditInfoByName(@QueryParam("id") Long id, @QueryParam("name") String name) throws Exception {
        userAccreditInfoBiz.checkUserByName(id, name);
        return null;
    }*/
  /*  *//**
     * 新增时用户名是否存在
     * @param name
     * @return
     * @throws Exception
     *//*
    @GET
    @Path(SupplyConstants.UserAccreditInfo.CHECK)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult checkName(@QueryParam("name") String name) throws Exception {
        if (userAccreditInfoBiz.checkName( name) >0) {
            return ResultUtil.createSucssAppResult("查询成功", "查询授权用户已存在");
        } else {
            return ResultUtil.createSucssAppResult("查询成功", "查询授权用户可用");
        }
    }*/

    /**
     * 查询已启用的渠道
     */
    @GET
    @Path(SupplyConstants.UserAccreditInfo.CHANNEL)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findChannel() throws Exception {
        return ResultUtil.createSucssAppResult("查询已启用的渠道成功", userAccreditInfoBiz.findChannel());

    }

    /**
     * 查询全局&渠道&混用角色
     */
    @GET
    @Path(SupplyConstants.UserAccreditInfo.ROLE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findChaAndWhole(@QueryParam("roleType") String roleType) throws Exception {

        return ResultUtil.createSucssAppResult("查询对应角色成功", userAccreditInfoBiz.findChannelOrWholeJur(roleType));

    }

    /**
     * 新增授权
     */
    @POST
    @Path(SupplyConstants.UserAccreditInfo.SAVE_ACCREDIT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult saveUserAccredit(@BeanParam UserAddPageDate userAddPageDate) throws Exception {
        userAccreditInfoBiz.saveUserAccreditInfo(userAddPageDate);
        return ResultUtil.createSucssAppResult("新增授权成功", "");
    }

    /**
     * 根据ID查询用户
     */
    @GET
    @Path(SupplyConstants.UserAccreditInfo.ACCREDIT + "/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findUserAccreditInfoById(@QueryParam("id") Long id) throws Exception {
        return ResultUtil.createSucssAppResult("查询用户成功", userAccreditInfoBiz.findUserAccreditInfoById(id));
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
    public AppResult updateUserAccreditInfo(@BeanParam UserAddPageDate userAddPageDate) throws Exception {
        userAccreditInfoBiz.updateUserAccredit(userAddPageDate);
        return ResultUtil.createSucssAppResult("修改用户成功", "");
    }

    @GET
    @Path(SupplyConstants.UserAccreditInfo.CHECK_PHONE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult checkPhone(@QueryParam("phone") String phone) throws Exception {
        return ResultUtil.createSucssAppResult("查询成功", userAccreditInfoBiz.checkPhone(phone));
    }
}
