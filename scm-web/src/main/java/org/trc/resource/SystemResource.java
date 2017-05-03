package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.ISystemBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.form.ChannelForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by sone on 2017/5/2.
 */
@Component
@Path(SupplyConstants.System.ROOT)
public class SystemResource {

    @Resource
    private ISystemBiz systemBiz;

    //渠道分页查询
    @GET
    @Path(SupplyConstants.System.Channel.CHANNEL_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Channel> dictTypePage(@BeanParam ChannelForm form, @BeanParam Pagenation<Channel> page) throws Exception{
        return systemBiz.channelPage(form,page);
    }

    //根据用户名查询渠道
    @GET
    @Path(SupplyConstants.System.Channel.CHANNEL)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Channel> findChannelByName(@QueryParam("name") String name) throws Exception{
        return  ResultUtil.createSucssAppResult("查询渠道成功", systemBiz.findChannelByName(name));
    }

    //保存渠道
    @POST
    @Path(SupplyConstants.System.Channel.CHANNEL)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveChannel(@BeanParam Channel channel) throws Exception{
        return  ResultUtil.createSucssAppResult("保存成功",systemBiz.saveChannel(channel));
    }

    //根据id查询
    @GET
    @Path(SupplyConstants.System.Channel.CHANNEL+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Channel> findDictTypeById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("查询渠道成功", systemBiz.findChannelById(id));
    }

    //渠道修改
    @PUT
    @Path(SupplyConstants.System.Channel.CHANNEL+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateChannel(@BeanParam  Channel channel, @PathParam("id") Long id) throws  Exception{
        return  ResultUtil.createSucssAppResult("修改渠道信息成功",systemBiz.updateChannel(channel,id));
    }
    //状态的修改
    @POST
    @Path(SupplyConstants.System.Channel.UPDATE_STATE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateState(@BeanParam Channel channel) throws Exception{
       return ResultUtil.createSucssAppResult("状态修改成功",systemBiz.updateState(channel));
    }
}
