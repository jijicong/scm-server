package org.trc.resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.biz.impl.serial.SerialBiz;
import org.trc.biz.serial.ISerialBiz;
import org.trc.constants.SupplyConstants;
import org.trc.util.AppResult;
import org.trc.util.AssertUtil;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by hzwdx on 2017/5/18.
 */
@Component
@Path(SupplyConstants.Serial.ROOT)
public class SerialResource {

    @Autowired
    private ISerialBiz serialBiz;

    @GET
    @Path(SupplyConstants.Serial.SERIAL+"/{module}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult getSerialCode(@PathParam("module") String module) throws Exception{
        return ResultUtil.createSucssAppResult("获取序列号成功", serialBiz.getSerialCode(module));
    }


}
