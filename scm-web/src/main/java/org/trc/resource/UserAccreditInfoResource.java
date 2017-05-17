package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.impower.IUserAccreditInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    public Pagenation<UserAccreditInfo> UserAccreditInfoPage(@BeanParam UserAccreditInfoForm form, @BeanParam Pagenation<UserAccreditInfo> page) throws Exception {
        return userAccreditInfoBiz.UserAccreditInfoPage(form, page);
    }

    @GET
    @Path(SupplyConstants.UserAccreditInfo.ACCREDIT)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findUserAccreditInfoByName(@QueryParam("name") String name) throws Exception {
        return ResultUtil.createSucssAppResult("查询用户成功", userAccreditInfoBiz.findUserAccreditInfoByName(name) == null ? null : "1");
    }

    /**
     * 查询渠道
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


        return ResultUtil.createSucssAppResult("查询对应角色成功",userAccreditInfoBiz.findChannelOrWholeJur(roleType));
    }
}
