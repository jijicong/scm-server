package org.trc.resource;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.service.trc.model.BrandToTrc;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * Created by hzdzf on 2017/5/24.
 */
@Path("/tairan")
public class TaiRanResource {

    private static final Logger logger = LoggerFactory.getLogger(TaiRanResource.class);

    @POST
    @Path("/{action}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResult brandTest(@BeanParam BrandToTrc brandToTrc, @PathParam("action") String action, @QueryParam("operateTime") long operateTime,
                                  @QueryParam("sign") String sign,@QueryParam("noticeNum") String noticeNum) throws Exception {

        logger.info("sign---"+sign);
        logger.info("action---"+action);
        logger.info("operateTime---"+operateTime);
        logger.info("noticeNum---"+noticeNum);
        long now = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();

        if ((now-operateTime)/1000/60>=30){
            jsonObject.put("status",0);
            jsonObject.put("msg","faile");
            return ResultUtil.createSucssAppResult(jsonObject.toJSONString(), "");
        }else{
            jsonObject.put("status",1);
            jsonObject.put("msg","success");
            return ResultUtil.createSucssAppResult(jsonObject.toJSONString(), "");
        }

    }
}
