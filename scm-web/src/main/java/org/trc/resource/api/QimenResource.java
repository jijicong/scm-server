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
import org.trc.biz.warehouseNotice.IWarehouseNoticeBiz;
import org.trc.constants.SupplyConstants;
import org.trc.form.QimenUrlRequest;
import org.trc.form.Response;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
    @Autowired
    private IWarehouseNoticeBiz warehouseNoticeBiz;
    @POST
    @Path(SupplyConstants.Qimen.QIMEN_CALLBACK)
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response confirmInvoice(@Context HttpServletRequest request,@BeanParam QimenUrlRequest qimenUrlRequest) throws Exception{

        InputStream is= request.getInputStream();
        Scanner scanner = new Scanner(is, "UTF-8");
        String requestText = scanner.useDelimiter("\\A").next();
        scanner.close();
        warehouseNoticeBiz.updateInStock(requestText);
        Response qimenResponse = new Response() ;
        qimenResponse.setFlag("success");
        qimenResponse.setCode("0");
        qimenResponse.setMessage("invalid appkey");
        return qimenResponse;
    }





}
