package org.trc.resource.api;


import com.alibaba.fastjson.JSON;
import com.taobao.api.internal.spi.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.trc.biz.outbuond.IOutBoundOrderBiz;
import org.trc.biz.qimen.IQimenBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.QimenException;
import org.trc.form.QimenUrlRequest;
import org.trc.form.Response;
import org.trc.service.config.IWarehouseNoticeCallbackService;
import org.trc.util.SpiUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


/**
 * @author hzszy
 * 提供给奇门的接口
 */
@Component
@Path(SupplyConstants.Qimen.QI_MEN)

public class QimenResource {
    private static final String FAILURE = "failure";
    private static final String SUCCESS = "success";
    /**
     * 发货单确认
     */
    private static final String DELIVERY_ORDER_CONFIRM = "taobao.qimen.deliveryorder.confirm";
    /**
     * 入库单确认
     */
    private static final String ENTRY_ORDER_CONFIRM = "entryorder.confirm";
    /**
     * 出库单确认
     */
    private static final String STOCKOUT_CONFIRM = "taobao.qimen.stockout.confirm";
    /**
     * 退货入库单确认
     */
    private static final String RETURNORDER_CONFIRM = "taobao.qimen.returnorder.confirm";
    /**
     * 订单流水通知
     */
    private static final String ORDERPROCESS_REPORT = "taobao.qimen.orderprocess.report";
    /**
     * 库存盘点通知
     */
    private static final String INVENTORY_REPORT = "taobao.qimen.inventory.report";

    private Logger logger = LoggerFactory.getLogger(QimenResource.class);
    @Autowired
    private IWarehouseNoticeBiz warehouseNoticeBiz;
    @Autowired
    private IQimenBiz qimenBiz;

    @Value("${qimen.secret}")
    private String secret;
    @Autowired
    private IWarehouseNoticeCallbackService warehouseNoticeCallbackService;
    @Autowired
    private IOutBoundOrderBiz outBoundOrderBiz;

    @POST
    @Path(SupplyConstants.Qimen.QIMEN_CALLBACK)
    @Produces(MediaType.APPLICATION_XML)
    public Response confirmInvoice(@Context HttpServletRequest request,@BeanParam QimenUrlRequest qimenUrlRequest){
        try {
            //接收到请求,先进行验签
            logger.info("qimenUrlRequest:"+ JSON.toJSONString(qimenUrlRequest));
            CheckResult checkResult =  SpiUtils.checkSign(request,secret);
             if (checkResult.isSuccess()){
                 logger.info("验签成功!");
                 //获取报文
                 String requestText =checkResult.getRequestBody();
                 logger.info("报文信息:"+requestText);
                 //确认逻辑
                 String method = qimenUrlRequest.getMethod();
                 logger.info("请求方法:"+method);
                 this.confirmMethod(requestText, method);
             }else {
                 String msg = "验签失败!";
                 throw new QimenException(ExceptionEnum.SIGN_ERROR, msg);
             }
            return new Response(SUCCESS, "0", "接收奇门反馈成功!") ;
        }catch(Exception e){
            logger.error("接收奇门反馈处理失败",e);
            return new Response(FAILURE, "sign-check-failure", "接收奇门反馈处理失败!"+e) ;
        }
    }

    /**
     * 确认逻辑
     * @param requestText
     * @param method
     */
    private void confirmMethod(String requestText, String method) throws Exception{
        switch (method) {
            case ENTRY_ORDER_CONFIRM:
                warehouseNoticeBiz
                        .updateInStock(requestText);
                break;
//            case DELIVERY_ORDER_CONFIRM:
//                try {
//                    outBoundOrderBiz.updateOutboundDetail(requestText);
//                } catch (Exception e) {
//                   logger.error("发货明细更新异常",e);
//                   throw new Exception("发货明细更新异常");
//                }
//                break;
            case STOCKOUT_CONFIRM:
                break;
            case RETURNORDER_CONFIRM:
                break;
            case ORDERPROCESS_REPORT:
                break;
            case INVENTORY_REPORT:
                break;
            default:
                break;
        }
    }


}
