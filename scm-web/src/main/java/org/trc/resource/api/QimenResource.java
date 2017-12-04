package org.trc.resource.api;


import com.alibaba.fastjson.JSON;
import com.qimen.api.DefaultQimenClient;
import com.qimen.api.QimenClient;
import com.qimen.api.request.EntryorderConfirmRequest;
import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.qimen.IQimenBiz;
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.form.QimenUrlRequest;
import org.trc.form.Response;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


/**
 * @author hzszy
 * 提供给奇门的接口
 */
@Component
@Path(SupplyConstants.Qimen.QI_MEN)

public class QimenResource {
    private Logger logger = LoggerFactory.getLogger(QimenResource.class);

    private static final String FAILURE = "failure";

    private static final String SUCCESS = "success";

    //发货单确认
    private static final String DELIVERY_ORDER_CONFIRM = "taobao.qimen.entryorder.confirm";

    //入库单确认
    private static final String ENTRY_ORDER_CONFIRM = "entryorder.confirm";

    @Autowired
    private IWarehouseNoticeBiz warehouseNoticeBiz;
    @Autowired
    private IQimenBiz qimenBiz;

    @POST
    @Path(SupplyConstants.Qimen.QIMEN_CALLBACK)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response confirmInvoice(@Context HttpServletRequest request,@BeanParam QimenUrlRequest qimenUrlRequest){
        try {
            //获取报文
            logger.info("qimenUrlRequest"+JSON.toJSONString(qimenUrlRequest));
            logger.info("ContentType:"+request.getContentType());
            String requestText = this.getInfo(request,qimenUrlRequest);
            logger.info("获取奇门报文:"+requestText);
            //确认逻辑
            String method = qimenUrlRequest.getMethod();
            this.confirmMethod(requestText, method);

            return new Response(SUCCESS, "0", "invalid appkey") ;
        }catch(Exception e){
            logger.error("确认失败!", e);
            return new Response(FAILURE, "sign-check-failure", e.getMessage()) ;
        }
    }

    //获取报文信息
    private String getInfo(HttpServletRequest request, QimenUrlRequest qimenUrlRequest) throws IOException{
        qimenBiz.checkResult(request,qimenUrlRequest.getMethod());
        InputStream is= request.getInputStream();
        Scanner scanner = new Scanner(is, "UTF-8");
        String requestText = scanner.useDelimiter("\\A").next();
        scanner.close();
        return requestText;
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
