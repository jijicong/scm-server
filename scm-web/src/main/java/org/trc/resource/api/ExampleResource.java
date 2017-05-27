package org.trc.resource.api;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.trc.util.MD5;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * 接口示例(挡板)
 * Created by hzdzf on 2017/5/24.
 */
@Path("/example")
public class ExampleResource {

    private static final Logger logger = LoggerFactory.getLogger(ExampleResource.class);


    @POST
    @Path("/brand")
    @Produces(MediaType.APPLICATION_JSON)
    public String brandTest( JSONObject information ) throws Exception {
        logger.info(information.toJSONString());
        //取值
        String action = information.getString("action");
        String operateTime = information.getString("operateTime");
        String noticeNum = information.getString("noticeNum");
        String sign = information.getString("sign");
        JSONObject brandToTrc = information.getJSONObject("brandToTrc");

        //验签，KEY,action，noticeNum，operateTime，brandToTrc里字典序，
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constant.Commom.KEY).append("|").append(action).append("|").append(noticeNum).append("|").append(operateTime).append("|").
                append(brandToTrc.getString("alise")).append("|").append(brandToTrc.getString("brandCode")).append("|").append(brandToTrc.getString("isValid")).append("|").
                append(brandToTrc.getString("logo")).append("|").append(brandToTrc.getString("name")).append("|").append(brandToTrc.getString("webUrl"));
        System.out.println(stringBuilder.toString());
        String validSign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        //时间和sign校验
        String result = verifyInformation(validSign,sign,operateTime);
        if (result!=null){
            return result;
        }
        JSONObject jsonObject = new JSONObject();
        //模拟处理数据出错
        if (Math.random()*100>90){
            jsonObject.put(Constant.Return.STATUS, 0);
            jsonObject.put(Constant.Return.MSG, "处理数据错误");
            return jsonObject.toJSONString();
        }

        //成功
        jsonObject.put(Constant.Return.STATUS, 1);
        jsonObject.put(Constant.Return.MSG, "信息处理成功");
        return jsonObject.toJSONString();
    }



    @POST
    @Path("/property")
    @Produces(MediaType.APPLICATION_JSON)
    public String ProperTyTest( JSONObject information ) throws Exception {
        logger.info(information.toJSONString());
        //取值
        String action = information.getString("action");
        String operateTime = information.getString("operateTime");
        String noticeNum = information.getString("noticeNum");
        String sign = information.getString("sign");
        JSONObject propertyToTrc = information.getJSONObject("propertyToTrc");
        JSONObject valueList = information.getJSONObject("valueList");
        //若有属性值改动
        if (valueList!=null){


        }
        //验签，KEY,action，noticeNum，operateTime，propertyToTrc里字典序，valueList未参与校验
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constant.Commom.KEY).append("|").append(action).append("|").append(noticeNum).append("|").append(operateTime).append("|").
                append(propertyToTrc.getString("description")).append("|").append(propertyToTrc.getString("isvalid")).append("|").
                append(propertyToTrc.getString("name")).append("|").append(propertyToTrc.getString("sort")).append("|").append(propertyToTrc.getString("typeCode")).
                append("|").append(propertyToTrc.getString("valueType"));
        System.out.println(stringBuilder.toString());
        String validSign =MD5.encryption(stringBuilder.toString()).toLowerCase();
        //时间和sign校验
        String result = verifyInformation(validSign,sign,operateTime);
        if (result!=null){
            return result;
        }
        JSONObject jsonObject = new JSONObject();
        //模拟处理数据出错
        if (Math.random()*100>90){
            jsonObject.put(Constant.Return.STATUS, 0);
            jsonObject.put(Constant.Return.MSG, "处理数据错误");
            return jsonObject.toJSONString();
        }

        //成功
        jsonObject.put(Constant.Return.STATUS, 1);
        jsonObject.put(Constant.Return.MSG, "信息处理成功");
        return jsonObject.toJSONString();
    }

    @POST
    @Path("/category")
    @Produces(MediaType.APPLICATION_JSON)
    public String CategoryTest( JSONObject information ) throws Exception {
        logger.info(information.toJSONString());
        //取值
        String action = information.getString("action");
        String operateTime = information.getString("operateTime");
        String noticeNum = information.getString("noticeNum");
        String sign = information.getString("sign");
        JSONObject categoryToTrc = information.getJSONObject("categoryToTrc");

        //验签KEY，action，noticeNum，operateTime，categoryToTrc里字典序，
        StringBuilder stringBuilder = new StringBuilder();
        String validSign =MD5.encryption(stringBuilder.toString()).toLowerCase();
        //时间和sign校验
        String result = verifyInformation(validSign,sign,operateTime);
        if (result!=null){
            return result;
        }
        JSONObject jsonObject = new JSONObject();
        //模拟处理数据出错
        if (Math.random()*100>90){
            jsonObject.put(Constant.Return.STATUS, 0);
            jsonObject.put(Constant.Return.MSG, "处理数据错误");
            return jsonObject.toJSONString();
        }

        //成功
        jsonObject.put(Constant.Return.STATUS, 1);
        jsonObject.put(Constant.Return.STATUS, "信息处理成功");
        return jsonObject.toJSONString();
    }

    public String verifyInformation(String validSign,String sign,String operateTime){
        JSONObject jsonObject = new JSONObject();
        if (! validSign.equals(sign)) {
            jsonObject.put(Constant.Return.STATUS, 0);
            jsonObject.put(Constant.Return.MSG, "验签不通过");
            return jsonObject.toJSONString();
        }

        //时间
        if ((System.currentTimeMillis() - Long.decode(operateTime)) >= Constant.Commom.TIMELIMIT) {
            jsonObject.put(Constant.Return.STATUS, 0);
            jsonObject.put(Constant.Return.MSG, "已超时");
            return jsonObject.toJSONString();
        }
        return null;
    }
}
