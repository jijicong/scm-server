package org.trc.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.util.HttpClientUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by george on 2016/12/30.
 */
public class LogisticTrace {

    private final Logger logger = LoggerFactory.getLogger(LogisticTrace.class);

    private int timeout = 3000;//超时时间

    private String realTimeQueryUrl = "http://api.kdniao.cc/Ebusiness/EbusinessOrderHandle.aspx";

    private String EBusinessID = "1271449";
    private String APPKEY = "420c357a-c29c-4f7e-bdfd-82917b189086";

    private final String requestType = "1002";
    private final String dataType = "2";

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setRealTimeQueryUrl(String realTimeQueryUrl) {
        this.realTimeQueryUrl = realTimeQueryUrl;
    }

    public void setEBusinessID(String EBusinessID) {
        this.EBusinessID = EBusinessID;
    }

    public void setAPPKEY(String APPKEY) {
        this.APPKEY = APPKEY;
    }

    public LogisticAck pull(String shipperCode, String logisticCode){
        Map parameters = new HashMap<>();
        parameters.put("EBusinessID",EBusinessID);
        parameters.put("RequestType",requestType);
        parameters.put("DataType",dataType);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("OrderCode", "");
        jsonObject.put("ShipperCode", shipperCode);
        jsonObject.put("LogisticCode", logisticCode);
        String RequestData = jsonObject.toJSONString();
        try {
            String DataSign = base64(MD5(RequestData + APPKEY, "UTF-8"), "UTF-8");
            parameters.put("DataSign",URLEncoder.encode(DataSign, "UTF-8"));
            parameters.put("RequestData",URLEncoder.encode(RequestData, "UTF-8"));
            String result = HttpClientUtil.httpPostRequest(realTimeQueryUrl, parameters, timeout);
            JSONObject json = JSONObject.parseObject(result);
            LogisticAck logisticAck = new LogisticAck();
            logisticAck.setSuccess((boolean)json.get("Success"));
            if(!logisticAck.isSuccess()) {
                logisticAck.setReason((String) json.get("Reason"));
            }
            logisticAck.setState((String)json.get("State"));
            JSONArray array = (JSONArray)json.get("Traces");
            logisticAck.setTraces(array.toJSONString());
            return logisticAck;
        } catch (Exception e) {
            e.getStackTrace();
            logger.error("pull", e.getMessage());
            return LogisticAck.renderFailure("查询失败，请稍后重试!");
        }
    }

    private static String base64(String str, String charset) throws UnsupportedEncodingException {
        String encoded = Base64.encode(str.getBytes(charset));
        return encoded;
    }

    private static String MD5(String str, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    public static void main(String[] args){
        LogisticTrace logisticTrace = new LogisticTrace();
        System.out.println(logisticTrace.pull("EMS","114176711608"));
    }

}
