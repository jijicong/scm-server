package org.trc.resource.api;


import com.taobao.api.internal.spi.CheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.trc.biz.qimen.IQimenBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
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
    //发货单确认
    private static final String DELIVERY_ORDER_CONFIRM = "taobao.qimen.entryorder.confirm";
    //入库单确认
    private static final String ENTRY_ORDER_CONFIRM = "entryorder.confirm";
    private Logger logger = LoggerFactory.getLogger(QimenResource.class);
    @Autowired
    private IWarehouseNoticeBiz warehouseNoticeBiz;
    @Autowired
    private IQimenBiz qimenBiz;

    @Value("${qimen.secret}")
    private String secret;
    @Autowired
    private IWarehouseNoticeCallbackService warehouseNoticeCallbackService;

    @POST
    @Path(SupplyConstants.Qimen.QIMEN_CALLBACK)
    @Produces(MediaType.APPLICATION_XML)
    public Response confirmInvoice(@Context HttpServletRequest request,@BeanParam QimenUrlRequest qimenUrlRequest){
        try {
            //接收到请求,先进行验签
            logger.info("URL@@@:"+request.getRequestURI());
            logger.info("URL---:"+request.getContextPath());
            logger.info("URL+++:"+request.getContentType());
            CheckResult checkResult =  SpiUtils.checkSign(request,secret);
             if (checkResult.isSuccess()){
                 logger.info("验签成功!");
             }else {
                 logger.info("验签失败!");
             }
            //获取报文
            String requestText =checkResult.getRequestBody();
             logger.info("报文信息:"+requestText);
            //确认逻辑
            String method = qimenUrlRequest.getMethod();
            logger.info("请求方法:"+method);
            this.confirmMethod(requestText, method);
            return new Response(SUCCESS, "0", "接收奇门反馈成功!") ;
        }catch(Exception e){
            return new Response(FAILURE, "sign-check-failure", "接收奇门反馈失败!") ;
        }
    }

    //确认逻辑
    private void confirmMethod(String requestText, String method){
        switch (method) {
            case ENTRY_ORDER_CONFIRM:
                warehouseNoticeBiz
                        .updateInStock(requestText);
                break;
            case DELIVERY_ORDER_CONFIRM:
                break;
            default:
                break;
        }
    }


}
