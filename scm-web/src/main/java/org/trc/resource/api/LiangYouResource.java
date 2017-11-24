package org.trc.resource.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.liangyou.LiangYouBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.order.OrderItem;
import org.trc.form.liangyou.LiangYouOrderDO;
import org.trc.form.liangyou.LiangYouTorderDO;
import org.trc.form.liangyou.LyStatementForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzwyz on 2017/6/15 0015.
 */
@Component
@Path(SupplyConstants.LiangYouOrder.ROOT)
public class LiangYouResource {
    private Logger log = LoggerFactory.getLogger(LiangYouResource.class);
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

    /**
     * 粮油代发分页查询接口
     *
     * @return
     * @throws Exception*/

    @GET
    @Path(SupplyConstants.LiangYouOrder.LY_OREDER_PAGE)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/json;charset=utf-8")
    public Response LyStatementPage(@BeanParam LyStatementForm queryModel, @BeanParam Pagenation<OrderItem> page) throws Exception {
        log.info("进入粮油代发分页查询接口======>"+ JSON.toJSONString(queryModel)+"===>"+JSON.toJSONString(page));
        return ResultUtil.createSuccessPageResult(liangYouBiz.LyStatementPage(queryModel,page));
    }

    /**
     * 粮油代发导出接口
     *
     * @return
     * @throws Exception*/

    @GET
    @Path(SupplyConstants.LiangYouOrder.EXPORT_ORDER)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/json;charset=utf-8")
    public Response exportOrderDetail(@BeanParam LyStatementForm queryModel) throws Exception {
        log.info("进入粮油代发报表导出接口======>"+ JSON.toJSONString(queryModel));
        return liangYouBiz.exportStatement(queryModel);
    }
}
