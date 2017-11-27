package org.trc.resource.api;


import com.alibaba.fastjson.JSON;
import com.qimen.api.QimenResponse;
import com.qimen.api.request.EntryorderConfirmRequest;
import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.constants.SupplyConstants;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * @author hzszy
 * 提供给奇门的接口
 */
@Component
@Path(SupplyConstants.Qimen.QI_MEN)

public class QimenResource {
    private Logger logger = LoggerFactory.getLogger(QimenResource.class);
    @POST
    @Path(SupplyConstants.Qimen.QIMEN_CALLBACK)
    @Produces(MediaType.APPLICATION_JSON)
    public QimenResponse confirmInvoice(@BeanParam EntryorderConfirmRequest confirmRequest, @Context HttpRequest request) throws Exception{
        logger.info(JSON.toJSONString(confirmRequest.getEntryOrder()));
        logger.info(JSON.toJSONString(confirmRequest.getItems()));
        logger.info(JSON.toJSONString(confirmRequest.getApiMethodName()));
        logger.info(JSON.toJSONString(confirmRequest.getOrderLines()));
        logger.info(JSON.toJSONString(confirmRequest.getResponseClass()));
        logger.info(JSON.toJSONString(confirmRequest.getExtendProps()));
        logger.info(JSON.toJSONString(request));
        QimenResponse qimenResponse = new QimenResponse() {
            @Override
            public void setFlag(String flag) {
                super.setFlag(flag);
            }

            @Override
            public void setCode(String code) {
                super.setCode(code);
            }

            @Override
            public void setMessage(String message) {
                super.setMessage(message);
            }
        };
        qimenResponse.setFlag("success");
        qimenResponse.setCode("0");
        qimenResponse.setMessage("invalid appkey");
        qimenResponse.setBody("hello");
        return qimenResponse;
    }
}
