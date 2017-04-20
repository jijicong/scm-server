package org.trc.util;

import com.alibaba.fastjson.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by george on 2017/2/28.
 */
public class ResponseAck {

    public static final String ENCODING = "UTF-8";
    public static final String ERROR_CUSTOM_CODE = "99";

    public static Response renderFailure(String description){
        return renderFailure(ERROR_CUSTOM_CODE,description);
    }

    public static Response renderFailure(String code,String errorMsg){
        return renderFailure(Response.Status.BAD_REQUEST, code, errorMsg);
    }

    public static Response renderFailure(Response.Status status, String code, String errorMsg){
        JSONObject json = new JSONObject();
        JSONObject error = new JSONObject();
        error.put("code", code);
        error.put("errorMsg", errorMsg);
        json.put("error", error);
        return Response.status(status).entity(json.toString()).type(MediaType.APPLICATION_JSON).encoding(ResponseAck.ENCODING).build();
    }

    public static void main(String[] args){
        JSONObject json = new JSONObject();
        JSONObject error = new JSONObject();
        error.put("code", "12");
        error.put("errorMsg", "123");
        json.put("error", error);
        System.out.println(json.toString());
    }

}
