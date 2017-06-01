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
    private static final Logger logger = LoggerFactory.getLogger(TaiRanResource.class);

    @Autowired
    private IJingDongBiz iJingDongBiz;

    /**
     * 京东统一下单接口
     * @param orderDO
     * @return
     * @throws Exception
     */
    @POST
    @Path(SupplyConstants.JingDongOrder.BILLORDER)
    @Produces(MediaType.APPLICATION_JSON)
    public String billOrder(@BeanParam OrderDO orderDO) throws Exception{
        JSONObject obj = new JSONObject();
        String rev = iJingDongBiz.billOrder(orderDO);
        JSONObject json=JSONObject.parseObject(rev);
        Boolean state = (Boolean) json.get("success");
        String data = (String) json.get("data");
        if (!state){
            obj.put("code", JingDongEnum.ORDER_FALSE.getCode());
            obj.put("data",data);
            obj.put("message","统一下单失败");
        }
        obj.put("code",JingDongEnum.ORDER_SUCCESS.getCode());
        obj.put("data",data);
        obj.put("message","统一下单成功");
        return obj.toJSONString();
    }

    /**
     * 确认预占库存接口
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.JingDongOrder.CONFIRM + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String confirmOrder(@PathParam("jdOrderId") String jdOrderId) throws Exception{
        JSONObject obj = new JSONObject();
        String rev = iJingDongBiz.confirmOrder(jdOrderId);
        JSONObject json=JSONObject.parseObject(rev);
        Boolean state = (Boolean) json.get("success");
        Boolean data = (Boolean) json.get("data");
        if (!state){
            obj.put("code", JingDongEnum.ORDER_FALSE.getCode());
            obj.put("data",data);
            obj.put("message","确认预占库存失败");
            return obj.toJSONString();
        }
        obj.put("code",JingDongEnum.ORDER_SUCCESS.getCode());
        obj.put("data",data);
        obj.put("message","确认预占库存成功");
        return obj.toJSONString();
    }

    /**
     * 取消未确认订单接口
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.JingDongOrder.CANCEL + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String cancel(@PathParam("jdOrderId") String jdOrderId) throws Exception{
        JSONObject obj = new JSONObject();
        String rev = iJingDongBiz.cancel(jdOrderId);
        JSONObject json=JSONObject.parseObject(rev);
        Boolean state = (Boolean) json.get("success");
        Boolean data = (Boolean) json.get("data");
        if (!state){
            obj.put("code", JingDongEnum.ORDER_FALSE.getCode());
            obj.put("data",data);
            obj.put("message","取消未确认订单失败");
            return obj.toJSONString();
        }
        obj.put("code",JingDongEnum.ORDER_SUCCESS.getCode());
        obj.put("data",data);
        obj.put("message","取消未确认订单成功");
        return obj.toJSONString();
    }

    /**
     * 发起支付接口
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @PUT
    @Path(SupplyConstants.JingDongOrder.PAY + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String doPay(@PathParam("jdOrderId") String jdOrderId) throws Exception{
        JSONObject obj = new JSONObject();
        String rev = iJingDongBiz.doPay(jdOrderId);
        JSONObject json=JSONObject.parseObject(rev);
        Boolean state = (Boolean) json.get("success");
        if (!state){
            obj.put("code", JingDongEnum.ORDER_FALSE.getCode());
            obj.put("data",null);
            obj.put("message","发起支付失败");
            return obj.toJSONString();
        }
        obj.put("code",JingDongEnum.ORDER_SUCCESS.getCode());
        obj.put("data",null);
        obj.put("message","发起支付成功");
        return obj.toJSONString();
    }

    /**
     * 订单反查接口
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.JingDongOrder.ORDERSELECT + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String selectJdOrderIdByThirdOrder(@PathParam("jdOrderId") String jdOrderId) throws Exception{
        JSONObject obj = new JSONObject();
        String rev = iJingDongBiz.selectJdOrderIdByThirdOrder(jdOrderId);
        JSONObject json=JSONObject.parseObject(rev);
        Boolean state = (Boolean) json.get("success");
        String data = (String) json.get("data");
        if (!state){
            obj.put("code", JingDongEnum.ORDER_FALSE.getCode());
            obj.put("data",null);
            obj.put("message","订单反查失败");
            return obj.toJSONString();
        }
        obj.put("code",JingDongEnum.ORDER_SUCCESS.getCode());
        obj.put("data",data);
        obj.put("message","订单反查成功");
        return obj.toJSONString();
    }

    /**
     * 订单信息查询接口
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.JingDongOrder.DETAIL + "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String selectJdOrder(@PathParam("jdOrderId") String jdOrderId) throws Exception{
        JSONObject obj = new JSONObject();
        String rev = iJingDongBiz.selectJdOrder(jdOrderId);
        JSONObject json=JSONObject.parseObject(rev);
        Boolean state = (Boolean) json.get("success");
        String data = (String) json.get("data");
        if (!state){
            obj.put("code", JingDongEnum.ORDER_FALSE.getCode());
            obj.put("data",null);
            obj.put("message","订单信息查询失败");
            return obj.toJSONString();
        }
        obj.put("code",JingDongEnum.ORDER_SUCCESS.getCode());
        obj.put("data",data);
        obj.put("message","订单信息查询成功");
        return obj.toJSONString();
    }

    /**
     * 查询配送信息接口
     * @param jdOrderId
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.JingDongOrder.TRACK+ "/{jdOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String orderTrack(@PathParam("jdOrderId") String jdOrderId) throws Exception{
        JSONObject obj = new JSONObject();
        String rev = iJingDongBiz.orderTrack(jdOrderId);
        JSONObject json=JSONObject.parseObject(rev);
        Boolean state = (Boolean) json.get("success");
        String data = (String) json.get("data");
        if (!state){
            obj.put("code", JingDongEnum.ORDER_FALSE.getCode());
            obj.put("data",null);
            obj.put("message","查询配送信息失败");
            return obj.toJSONString();
        }
        obj.put("code",JingDongEnum.ORDER_SUCCESS.getCode());
        obj.put("data",data);
        obj.put("message","查询配送信息成功");
        return obj.toJSONString();
    }
}
