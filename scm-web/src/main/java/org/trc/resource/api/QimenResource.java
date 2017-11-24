package org.trc.resource.api;


import com.alibaba.fastjson.JSON;
import com.qimen.api.QimenResponse;
import com.qimen.api.request.EntryorderConfirmRequest;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.constants.SupplyConstants;

import javax.ws.rs.*;
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
    @Produces(MediaType.APPLICATION_XML)
    public QimenResponse confirmInvoice(@BeanParam EntryorderConfirmRequest confirmRequest) throws Exception{
        logger.info(JSON.toJSONString(confirmRequest));
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
        return null;
    }
}
