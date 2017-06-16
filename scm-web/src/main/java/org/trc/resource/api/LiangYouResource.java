package org.trc.resource.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.liangyou.LiangYouBiz;
import org.trc.constants.SupplyConstants;
import org.trc.form.liangyou.LiangYouOrderDO;
import org.trc.form.liangyou.LiangYouTorderDO;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hzwyz on 2017/6/15 0015.
 */
@Component
@Path(SupplyConstants.LiangYouOrder.ROOT)
public class LiangYouResource {

    @Resource
    LiangYouBiz liangYouBiz;

    @PUT
    @Path(SupplyConstants.LiangYouOrder.OUT_ORDER)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONObject> addOutOrder(@BeanParam LiangYouOrderDO orderDO)throws Exception{
        return ResultUtil.createSucssAppResult("非签约订单导入成功", liangYouBiz.addOutOrder(orderDO));
    }

    @PUT
    @Path(SupplyConstants.LiangYouOrder.TOUT_ORDER)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONObject> addOutOrder(@BeanParam LiangYouTorderDO orderDO)throws Exception{
        return ResultUtil.createSucssAppResult("签约订单导入成功", liangYouBiz.addToutOrder(orderDO));
    }

    @GET
    @Path(SupplyConstants.LiangYouOrder.ORDER_STATUS+"/{orderSn}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONObject> addOutOrder(@PathParam("orderSn") String orderSn)throws Exception{
        return ResultUtil.createSucssAppResult("查询订单状态成功", liangYouBiz.getOrderStatus(orderSn));
    }
}
