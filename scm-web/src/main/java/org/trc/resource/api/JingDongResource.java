package org.trc.resource.api;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.constants.SupplyConstants;
import org.trc.enums.JingDongEnum;
import org.trc.form.JDModel.OrderDO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hzwyz on 2017/6/1 0001.
 */
@Path(SupplyConstants.JingDongOrder.ROOT)
public class JingDongResource {
    private static final Logger logger = LoggerFactory.getLogger(JingDongResource.class);

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
    public String billOrder(@BeanParam OrderDO orderDO) throws Exception {
        return iJingDongBiz.billOrder(orderDO);
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
    public String confirmOrder(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return iJingDongBiz.confirmOrder(jdOrderId);
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
    public String cancel(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return iJingDongBiz.cancel(jdOrderId);
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
    public String doPay(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return iJingDongBiz.doPay(jdOrderId);
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
    public String selectJdOrderIdByThirdOrder(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return iJingDongBiz.selectJdOrderIdByThirdOrder(jdOrderId);
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
    public String selectJdOrder(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return iJingDongBiz.selectJdOrder(jdOrderId);
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
    public String orderTrack(@PathParam("jdOrderId") String jdOrderId) throws Exception {
        return iJingDongBiz.orderTrack(jdOrderId);
    }
}
