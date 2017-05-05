package org.trc.resource;

import org.springframework.stereotype.Component;
import org.trc.biz.IChannelBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.form.system.ChannelForm;
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
@Path(SupplyConstants.Channel.ROOT)
public class ChannelResource {

    @Resource
    private IChannelBiz channelBiz;

    //渠道分页查询
    @GET
    @Path(SupplyConstants.Channel.CHANNEL_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<Channel> channelPage(@BeanParam ChannelForm form, @BeanParam Pagenation<Channel> page) throws Exception{
        return channelBiz.channelPage(form,page);
    }

    //根据渠道名查询渠道
    @GET
    @Path(SupplyConstants.Channel.CHANNEL)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult findChannelByName(@QueryParam("name") String name) throws Exception{
        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据
        return  ResultUtil.createSucssAppResult("查询渠道成功", channelBiz.findChannelByName(name)==null ? null :"1");
    }

    //保存渠道
    @POST
    @Path(SupplyConstants.Channel.CHANNEL)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/x-www-form-urlencoded")
    public AppResult saveChannel(@BeanParam Channel channel) throws Exception{
        return  ResultUtil.createSucssAppResult("保存成功",channelBiz.saveChannel(channel));
    }

    //根据id查询
    @GET
    @Path(SupplyConstants.Channel.CHANNEL+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Channel> findDictTypeById(@PathParam("id") Long id) throws Exception{
        return ResultUtil.createSucssAppResult("查询渠道成功", channelBiz.findChannelById(id));
    }

    //渠道修改
    @PUT
    @Path(SupplyConstants.Channel.CHANNEL+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateChannel(@BeanParam  Channel channel, @PathParam("id") Long id) throws  Exception{
        return  ResultUtil.createSucssAppResult("修改渠道信息成功",channelBiz.updateChannel(channel,id));
    }
    //渠道状态的修改
    @POST
    @Path(SupplyConstants.Channel.UPDATE_STATE)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult updateChannelState(@BeanParam Channel channel) throws Exception{
       return ResultUtil.createSucssAppResult("状态修改成功",channelBiz.updateChannelState(channel));
    }
}
