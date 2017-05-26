package org.trc.resource.api;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.trc.util.MD5;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * 接口示例
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
    @Path("/brand")
    @Produces(MediaType.APPLICATION_JSON)
    public String brandTest( JSONObject information ) throws Exception {
        //取值
        String action = information.getString("action");
        String operateTime = information.getString("operateTime");
        String noticeNum = information.getString("noticeNum");
        String sign = information.getString("sign");

        JSONObject brandToTrc = information.getJSONObject("brandToTrc");
        long now = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();

        //验签，action，noticeNum，operateTime，brandToTrc里字典序，
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(action).append("|").append(operateTime).append("|").append(noticeNum).append("|").
                append(brandToTrc.getString("alise")).append("|").append(brandToTrc.getString("brandCode")).append("|").append(brandToTrc.getString("isValid")).append("|").
                append(brandToTrc.getString("logo")).append("|").append(brandToTrc.getString("name")).append("|").append(brandToTrc.getString("webUrl"));
        System.out.println(stringBuilder.toString());
        String validSign =MD5.encryption(stringBuilder.toString()).toLowerCase();
        if (! validSign.equals(sign)) {
            jsonObject.put(ReturnConstant.STATUS, 0);
            jsonObject.put(ReturnConstant.MSG, "faile");
            return jsonObject.toJSONString();
        }

        //时间
        if ((now - Long.decode(operateTime)) / 1000 / 60 >= 30) {
            jsonObject.put(ReturnConstant.STATUS, 0);
            jsonObject.put(ReturnConstant.MSG, "faile");
            return jsonObject.toJSONString();
        }

        //模拟处理数据出错
        if (Math.random()*100>90){
            jsonObject.put(ReturnConstant.STATUS, 0);
            jsonObject.put(ReturnConstant.MSG, "处理数据错误");
            return jsonObject.toJSONString();
        }

        jsonObject.put(ReturnConstant.STATUS, 1);
        jsonObject.put(ReturnConstant.MSG, "success");
        return jsonObject.toJSONString();

    }



    @POST
    @Path("/property")
    @Produces(MediaType.APPLICATION_JSON)
    public String ProperTyTest( JSONObject information ) throws Exception {
        //取值
        String action = information.getString("action");
        String operateTime = information.getString("operateTime");
        String noticeNum = information.getString("noticeNum");
        String sign = information.getString("sign");
        JSONObject propertyToTrc = information.getJSONObject("propertyToTrc");
        JSONObject valueList = information.getJSONObject("valueList");
        long now = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        //若有属性值改动
        if (valueList!=null){


        }
        //验签，action，noticeNum，operateTime，propertyToTrc里字典序，valueList未参与校验
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(action).append("|").append(noticeNum).append("|").append(operateTime).append("|").
                append(propertyToTrc.getString("description")).append("|").append(propertyToTrc.getString("isvalid")).append("|").
                append(propertyToTrc.getString("name")).append("|").append(propertyToTrc.getString("sort")).append("|").append(propertyToTrc.getString("typeCode")).
                append("|").append(propertyToTrc.getString("valueType"));
        System.out.println(stringBuilder.toString());
        String validSign =MD5.encryption(stringBuilder.toString()).toLowerCase();
        if (! validSign.equals(sign)) {
            jsonObject.put(ReturnConstant.STATUS, 0);
            jsonObject.put(ReturnConstant.MSG, "faile");
            return jsonObject.toJSONString();
        }

        //时间
        if ((now - Long.decode(operateTime)) / 1000 / 60 >= 30) {
            jsonObject.put(ReturnConstant.STATUS, 0);
            jsonObject.put(ReturnConstant.MSG, "faile");
            return jsonObject.toJSONString();
        }
        //模拟处理数据出错
        if (Math.random()*100>90){
            jsonObject.put(ReturnConstant.STATUS, 0);
            jsonObject.put(ReturnConstant.MSG, "处理数据错误");
            return jsonObject.toJSONString();
        }

        jsonObject.put(ReturnConstant.STATUS, 1);
        jsonObject.put(ReturnConstant.MSG, "信息处理成功");
        return jsonObject.toJSONString();
    }

    @POST
    @Path("/category")
    @Produces(MediaType.APPLICATION_JSON)
    public String CategoryTest( JSONObject information ) throws Exception {
        //取值
        String action = information.getString("action");
        String operateTime = information.getString("operateTime");
        String noticeNum = information.getString("noticeNum");
        String sign = information.getString("sign");
        JSONObject categoryToTrc = information.getJSONObject("categoryToTrc");
        long now = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();

        //验签，action，noticeNum，operateTime，categoryToTrc里字典序，
        StringBuilder stringBuilder = new StringBuilder();
        String validSign =MD5.encryption(stringBuilder.toString()).toLowerCase();
        if (! validSign.equals(sign)) {
            jsonObject.put(ReturnConstant.STATUS, 0);
            jsonObject.put(ReturnConstant.MSG, "faile");
            return jsonObject.toJSONString();
        }

        //时间
        if ((now - Long.decode(operateTime)) / 1000 / 60 >= 30) {
            jsonObject.put(ReturnConstant.STATUS, 0);
            jsonObject.put(ReturnConstant.MSG, "faile");
            return jsonObject.toJSONString();
        }
        //模拟处理数据出错
        if (Math.random()*100>90){
            jsonObject.put(ReturnConstant.STATUS, 0);
            jsonObject.put(ReturnConstant.MSG, "处理数据错误");
            return jsonObject.toJSONString();
        }

        jsonObject.put(ReturnConstant.STATUS, 1);
        jsonObject.put(ReturnConstant.MSG, "信息处理成功");
        return jsonObject.toJSONString();

    }
}
