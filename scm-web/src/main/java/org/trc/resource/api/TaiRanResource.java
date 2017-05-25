package org.trc.resource.api;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.service.trc.model.BrandToTrc;
import org.trc.util.AppResult;
import org.trc.util.ResultUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


/**
 * Created by hzdzf on 2017/5/24.
 */
@Path("/tairan")
public class TaiRanResource {

    private static final Logger logger = LoggerFactory.getLogger(TaiRanResource.class);

  /*  @POST
    @Path("/brand/{action}")
    @Produces(MediaType.APPLICATION_JSON)
    public String brandTest(@BeanParam BrandToTrc brandToTrc, @PathParam("action") String action, @QueryParam("operateTime") long operateTime,
                            @QueryParam("sign")String sign, @QueryParam("noticeNum") String noticeNum ) throws Exception {

        logger.info("sign---" + sign);
        logger.info("action---" + action);
        logger.info("operateTime---" + operateTime);
        logger.info("noticeNum---" + noticeNum);
        System.out.println("sign---" + sign);
        System.out.println("action---" + action);
        System.out.println("operateTime---" + operateTime);
        System.out.println("noticeNum---" + noticeNum);
        long now = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();

        //验签，action，noticeNum，operateTime，brandToTrc里字典序，
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("action").append("|").append(operateTime).append("|").append("noticeNum").append("|").
                append(brandToTrc.getAlise()).append("|").append(brandToTrc.getBrandCode()).append("|").append(brandToTrc.getIsValid()).append("|").
                append(brandToTrc.getLogo()).append("|").append(brandToTrc.getName()).append("|").append(brandToTrc.getWebUrl());
        if (!stringBuilder.toString().toLowerCase().equals(sign)) {
            jsonObject.put("status", 0);
            jsonObject.put("msg", "faile");
            return jsonObject.toJSONString();
        }

        //时间
        if ((now - operateTime) / 1000 / 60 >= 30) {
            jsonObject.put("status", 0);
            jsonObject.put("msg", "faile");
            return jsonObject.toJSONString();
        }
        jsonObject.put("status", 1);
        jsonObject.put("msg", "success");
        return jsonObject.toJSONString();


    }*/


    @POST
    @Path("/brand/{action}")
    @Produces(MediaType.APPLICATION_JSON)
    public String brandTest(@BeanParam BrandToTrc brandToTrc, @PathParam("action") String action, @QueryParam("operateTime") long operateTime,
                            @QueryParam("sign")String sign, @QueryParam("noticeNum") String noticeNum ) throws Exception {

        logger.info("sign---" + sign);
        logger.info("action---" + action);
        logger.info("operateTime---" + operateTime);
        logger.info("noticeNum---" + noticeNum);
        System.out.println("sign---" + sign);
        System.out.println("action---" + action);
        System.out.println("operateTime---" + operateTime);
        System.out.println("noticeNum---" + noticeNum);
        long now = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();

        //验签，action，noticeNum，operateTime，brandToTrc里字典序，
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("action").append("|").append(operateTime).append("|").append("noticeNum").append("|").
                append(brandToTrc.getAlise()).append("|").append(brandToTrc.getBrandCode()).append("|").append(brandToTrc.getIsValid()).append("|").
                append(brandToTrc.getLogo()).append("|").append(brandToTrc.getName()).append("|").append(brandToTrc.getWebUrl());
        if (!stringBuilder.toString().toLowerCase().equals(sign)) {
            jsonObject.put("status", 0);
            jsonObject.put("msg", "faile");
            return jsonObject.toJSONString();
        }

        //时间
        if ((now - operateTime) / 1000 / 60 >= 30) {
            jsonObject.put("status", 0);
            jsonObject.put("msg", "faile");
            return jsonObject.toJSONString();
        }
        jsonObject.put("status", 1);
        jsonObject.put("msg", "success");
        return jsonObject.toJSONString();


    }
}
