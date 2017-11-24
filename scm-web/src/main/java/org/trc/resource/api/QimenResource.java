package org.trc.resource.api;


import com.alibaba.fastjson.JSON;
import com.qimen.api.QimenResponse;
import com.qimen.api.request.EntryorderConfirmRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.trc.constants.SupplyConstants;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;



/**
 * @author hzszy
 * 提供给奇门的接口
 */
@Component
@Path(SupplyConstants.Qimen.QI_MEN)

public class QimenResource {
    private Logger logger = LoggerFactory.getLogger(QimenResource.class);
    @GET
    @Path(SupplyConstants.Qimen.QIMEN_CALLBACK)
    @Produces("application/json;charset=utf-8")
    public QimenResponse confirmInvoice(@BeanParam EntryorderConfirmRequest confirmRequest) throws Exception{
        logger.info(JSON.toJSONString(confirmRequest));
        QimenResponse qimenResponse = new QimenResponse() {
        };
        qimenResponse.setCode("1");
        return qimenResponse;
    }
}
