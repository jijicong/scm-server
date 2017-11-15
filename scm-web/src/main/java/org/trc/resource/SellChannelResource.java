package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.system.ISellChannelBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.domain.System.SellChannel;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.system.ChannelForm;
import org.trc.form.system.SellChannelFrom;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author szy
 * @date 2017/11/13
 */
@Component
@Path(SupplyConstants.SellChannel.ROOT)
public class SellChannelResource {

    @Autowired
    private ISellChannelBiz sellChannelBiz;

    /**
     * 销售渠道分页查询
     * @param form
     * @param page
     * @return
     */
    @GET
    @Path(SupplyConstants.SellChannel.SELL_CHANNEL_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sellChannelPage(@BeanParam SellChannelFrom form, @BeanParam Pagenation<SellChannel> page){
        return ResultUtil.createSuccessPageResult(sellChannelBiz.sellChannelPage(form,page));
    }

    //根据销售渠道名查询渠道
    @GET
    @Path(SupplyConstants.SellChannel.SELL_CHANNEL_NAME)
    @Produces(MediaType.APPLICATION_JSON)
    public Response selectSellChannelByName(@QueryParam("name") String name){
        //  前台接受为null则数据没问题 ，有数据则名称不能使用，"1" 为标志存在数据
        return  ResultUtil.createSuccessResult("查询销售渠道成功", sellChannelBiz.selectSellChannelByName(name)==null ? null :"1");
    }

    @POST
    @Path(SupplyConstants.SellChannel.SELL_CHANNEL_UPDATE+"/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSellChannel(@BeanParam  SellChannel sellChannel,@Context ContainerRequestContext requestContext){
        sellChannelBiz.updateSellChannel(sellChannel,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("修改销售渠道信息成功","");
    }
    @PUT
    @Path(SupplyConstants.SellChannel.SELL_CHANNEL_SAVE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveSellChannel(@BeanParam  SellChannel sellChannel,@Context ContainerRequestContext requestContext){
        sellChannelBiz.saveSellChannel(sellChannel,(AclUserAccreditInfo) requestContext.getProperty(SupplyConstants.Authorization.ACL_USER_ACCREDIT_INFO));
        return  ResultUtil.createSuccessResult("新增销售渠道成功","");
    }
}
