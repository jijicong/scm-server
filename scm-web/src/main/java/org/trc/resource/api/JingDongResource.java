package org.trc.resource.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.constants.SupplyConstants;
import org.trc.form.JDModel.OrderDO;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hzwyz on 2017/6/1 0001.
 */
@Component
@Path(SupplyConstants.JingDongOrder.ROOT)
public class JingDongResource {
    @Autowired
    private IJingDongBiz iJingDongBiz;

    /**
     * 京东统一下单接口
     *
     * @param orderDO
     * @return
     * @throws Exception
     */
    @POST
    @Path(SupplyConstants.JingDongOrder.BILLORDER)
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONObject> billOrder(@BeanParam OrderDO orderDO) throws Exception {
        return ResultUtil.createSucssAppResult("京东统一下单成功", iJingDongBiz.billOrder(orderDO));
    }

    /**
     * 确认预占库存接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.JingDongOrder.CONFIRM + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<Boolean> confirmOrder(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return ResultUtil.createSucssAppResult("确认预占库存成功", iJingDongBiz.confirmOrder(jdOrderId));
    }

    /**
     * 取消未确认订单接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.JingDongOrder.CANCEL + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONObject> cancel(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return ResultUtil.createSucssAppResult("取消未确认订单成功", iJingDongBiz.cancel(jdOrderId));
    }

    /**
     * 发起支付接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.JingDongOrder.PAY + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONObject> doPay(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return ResultUtil.createSucssAppResult("发起支付成功", iJingDongBiz.doPay(jdOrderId));
    }

    /**
     * 订单反查接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.JingDongOrder.ORDERSELECT + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONObject> selectJdOrderIdByThirdOrder(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return ResultUtil.createSucssAppResult("订单反查成功", iJingDongBiz.selectJdOrderIdByThirdOrder(jdOrderId));
    }

    /**
     * 订单信息查询接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.JingDongOrder.DETAIL + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONObject> selectJdOrder(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return ResultUtil.createSucssAppResult("订单信息查询成功", iJingDongBiz.selectJdOrder(jdOrderId));
    }

    /**
     * 查询配送信息接口
     *
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.JingDongOrder.TRACK + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult<JSONObject> orderTrack(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return ResultUtil.createSucssAppResult("订单信息查询成功", iJingDongBiz.orderTrack(jdOrderId));
    }
}
