package org.trc.resource.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.jingdong.IJingDongBiz;
import org.trc.constants.SupplyConstants;
import org.trc.form.JDModel.BalanceDetailDO;
import org.trc.form.JDModel.JdBalanceDetail;
import org.trc.form.JDModel.OrderDO;
import org.trc.form.external.OperateForm;
import org.trc.form.external.OrderDetail;
import org.trc.form.external.OrderDetailForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hzwyz on 2017/6/1 0001.
 */
@Component
@Path(SupplyConstants.JingDongOrder.ROOT)
public class JingDongResource {
    private Logger log = LoggerFactory.getLogger(JingDongResource.class);
    @Autowired
    private IJingDongBiz iJingDongBiz;


    /**
     * 查询余额对账明细信息接口
     * @param queryModel
     * @param page
     * @return
     * @throws Exception
     *//*
    @GET
    @Path(SupplyConstants.JingDongOrder.CHECK_BALANCE)
    @Produces("application/json;charset=utf-8")
    public Pagenation<JdBalanceDetail> checkBalanceDetail(@BeanParam BalanceDetailDO queryModel, @BeanParam Pagenation<JdBalanceDetail> page) throws Exception {
        return iJingDongBiz.checkBalanceDetail(queryModel,page);
    }*/

    /**
     * 查询所有京东业务类型接口
     * @return
     * @throws Exception
     */
    @GET
    @Path(SupplyConstants.JingDongOrder.GET_ALL_TREAD_TYPE)
    @Produces("application/json;charset=utf-8")
    public AppResult<JSONArray> getAllTreadType() throws Exception {
        return ResultUtil.createSucssAppResult("业务类型查询成功", iJingDongBiz.getAllTreadType().getResult());
    }

    /**
     * 京东账户余额信息查询接口
     *
     * @return
     * @throws Exception*/

    @GET
    @Path(SupplyConstants.JingDongOrder.GET_BALANCE_INFO)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/json;charset=utf-8")
    public Response queryBalanceInfo() throws Exception {
        log.info("进入余额信息查询接口======>");
        return iJingDongBiz.queryBalanceInfo();
    }


    /**
     * 订单对比明细分页查询接口
     *
     * @return
     * @throws Exception*/

    @GET
    @Path(SupplyConstants.JingDongOrder.ORDER_DETAIL_PAGE)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/json;charset=utf-8")
    public Response orderDetailByPage(@BeanParam OrderDetailForm queryModel, @BeanParam Pagenation<OrderDetail> page) throws Exception {
        log.info("进入订单明细分页查询接口======>"+ JSON.toJSONString(queryModel)+"===>"+JSON.toJSONString(page));
        return ResultUtil.createSuccessPageResult(iJingDongBiz.orderDetailByPage(queryModel,page));
    }

    /**
     * 余额明细分页查询接口
     *
     * @return
     * @throws Exception*/

    @GET
    @Path(SupplyConstants.JingDongOrder.BALANCE_DETAIL_PAGE)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/json;charset=utf-8")
    public Response balanceDetailByPage(@BeanParam BalanceDetailDO queryModel, @BeanParam Pagenation<JdBalanceDetail> page) throws Exception {
        log.info("进入余额明细分页查询接口======>"+ JSON.toJSONString(queryModel)+"===>"+JSON.toJSONString(page));
        return ResultUtil.createSuccessPageResult(iJingDongBiz.balanceDetailByPage(queryModel,page));
    }
    /**
     * 余额明细导出接口
     *
     * @return
     * @throws Exception*/

    @GET
    @Path(SupplyConstants.JingDongOrder.EXPORT_BALANCE_DETAIL)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/octet-stream")
    public Response exportBalanceDetail(@BeanParam BalanceDetailDO queryModel) throws Exception {
        log.info("进入余额明细导出接口======>"+ JSON.toJSONString(queryModel));
        return iJingDongBiz.exportBalanceDetail(queryModel);
    }

    /**
     * 订单明细导出接口
     *
     * @return
     * @throws Exception*/

    @GET
    @Path(SupplyConstants.JingDongOrder.EXPORT_ORDER_DETAIL)
    @Consumes("text/plain;charset=utf-8")
    @Produces("application/json;charset=utf-8")
    public Response exportOrderDetail(@BeanParam OrderDetailForm queryModel) throws Exception {
        log.info("进入订单明细导出接口======>"+ JSON.toJSONString(queryModel));
        return iJingDongBiz.exportOrderDetail(queryModel);
    }

    /**
     * 订单明操作接口
     *
     * @return
     * @throws Exception*/

    @PUT
    @Path(SupplyConstants.JingDongOrder.OPERATE_ORDER)
    @Consumes("application/x-www-form-urlencoded;charset=utf-8")
    @Produces("application/json;charset=utf-8")
    public Response operateRecord(@BeanParam OperateForm orderDetail) throws Exception {
        log.info("进入订单明操作接口======>"+ JSON.toJSONString(orderDetail));
        return iJingDongBiz.operateRecord(orderDetail);
    }
}
