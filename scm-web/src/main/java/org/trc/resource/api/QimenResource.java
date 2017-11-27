package org.trc.resource.api;


import com.alibaba.fastjson.JSON;
import com.qimen.api.DefaultQimenClient;
import com.qimen.api.QimenClient;
import com.qimen.api.request.EntryorderConfirmRequest;
import org.apache.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.constants.SupplyConstants;
import org.trc.form.Response;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


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
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response confirmInvoice(@BeanParam EntryorderConfirmRequest confirmRequest, @Context HttpRequest request) throws Exception{
        logger.info(JSON.toJSONString(confirmRequest.getEntryOrder()));
        logger.info(JSON.toJSONString(confirmRequest.getItems()));
        logger.info(JSON.toJSONString(confirmRequest.getApiMethodName()));
        logger.info(JSON.toJSONString(confirmRequest.getOrderLines()));
        logger.info(JSON.toJSONString(confirmRequest.getResponseClass()));
        logger.info(JSON.toJSONString(confirmRequest.getExtendProps()));
        logger.info(JSON.toJSONString(request));

        Response qimenResponse = new Response() ;
        qimenResponse.setFlag("success");
        qimenResponse.setCode("0");
        qimenResponse.setMessage("invalid appkey");
        return qimenResponse;
    }





}
