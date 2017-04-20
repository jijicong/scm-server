package org.trc.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.util.GuidUtil;
import org.trc.util.HttpClientUtil;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by george on 2016/12/17.
 */
public class TcoinOperation {

    private Logger logger = LoggerFactory.getLogger(TcoinOperation.class);

    private int timeout = 3000;//超时时间

    private String tcoinBaseUrl = "http://101.71.241.100:48080/trcloanweb/";

    private String queryUri = "tb/";

    private String chongzhengUri = "/chongzheng";

    private String realNameCheckUri = "tb/realname/";

    private String key = "xfjghd9i9583k";

    //private String tcoinBaseUrl = "http://cg.trc.com/trcloanweb/tb/";
    //private String key = "werkfd559034";

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getTcoinBaseUrl() {
        return tcoinBaseUrl;
    }

    public void setTcoinBaseUrl(String tcoinBaseUrl) {
        this.tcoinBaseUrl = tcoinBaseUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public RealNameCheckAck realNameCheck(String userId){
        long time1 = System.currentTimeMillis();
        Map<String, Object> params = new HashMap();
        params.put("userId",userId);
        try {
            String result = HttpClientUtil.httpGetRequest(tcoinBaseUrl+realNameCheckUri+userId,params, timeout);
            if("".equals(result)){
                return RealNameCheckAck.renderFailure();
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            RealNameCheckAck realNameCheckAck = new RealNameCheckAck();
            realNameCheckAck.setResultCode((String)jsonObject.get("resultCode"));
            realNameCheckAck.setResultMsg((String)jsonObject.get("resultMsg"));
            realNameCheckAck.setIsRealName((String)jsonObject.get("isRealName"));
            long time2 = System.currentTimeMillis();
            System.out.println(time2-time1);
            return realNameCheckAck;
        } catch (URISyntaxException e) {
            logger.error("realNameCheck error:"+userId);
            return RealNameCheckAck.renderFailure();
        } catch (Exception e){
            logger.error("realNameCheck error:"+userId);
            logger.error(e.getMessage());
            return RealNameCheckAck.renderFailure();
        }
    }

    public TcoinAck queryTcoinBalance(String userId){
        Map<String, Object> params = new HashMap();
        params.put("userId",userId);
        try {
            long time1 = System.currentTimeMillis();
            String result = HttpClientUtil.httpGetRequest(tcoinBaseUrl+queryUri+userId, params, timeout);
            long time2 = System.currentTimeMillis();
            System.out.println(time2-time1);
            if("".equals(result)){
                return TcoinAck.renderFailure();
            }
            JSONObject jsonObject = JSONObject.parseObject(result);
            TcoinAck tcoinAck = new TcoinAck();
            tcoinAck.setAvail((Integer)jsonObject.get("avail")+0l);
            tcoinAck.setToExpire((Integer)jsonObject.get("toExpire")+0l);
            tcoinAck.setResultCode((String)jsonObject.get("resultCode"));
            tcoinAck.setResultMsg((String)jsonObject.get("resultMsg"));
            return tcoinAck;
        } catch (URISyntaxException e) {
            logger.error("queryTcoinBalance error:"+userId);
            return TcoinAck.renderFailure();
        } catch (Exception e){
            logger.error("queryTcoinBalance error:"+userId);
            logger.error(e.getMessage());
            return TcoinAck.renderFailure();
        }
    }

    @Deprecated
    public TcoinAck operateTcoin(String userId, Long amount){
        String requestNo = GuidUtil.getNextUid("tcoin");
        return operateTcoin(userId, amount, requestNo);
    }

    public TcoinAck operateTcoin(String userId, Long amount, String requestNo){
        if(amount == 0l || StringUtils.isEmpty(userId) || StringUtils.isEmpty(requestNo)){
            return null;
        }
        String platform = "JF";
        String requestTime = DateUtils.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd HH:mm:ss");
        String action,remarks,sign;
        if(amount>0){
            //增加Tcoin
            action = "JF2T";
            remarks = "积分兑换T币";
            StringBuilder sb = new StringBuilder();
            sb.append("action=").append(action)
                    .append("&amount=").append(amount)
                    .append("&platform=").append(platform)
                    .append("&requestNo=").append(requestNo)
                    .append("&userId=").append(userId)
                    .append("&key=").append(key);
            sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(sb.toString());
        }else{
            //减少Tcoin
            action = "T2JF";
            remarks = "T币兑换积分";
            StringBuilder sb = new StringBuilder();
            sb.append("action=").append(action)
                    .append("&amount=").append(amount)
                    .append("&platform=").append(platform)
                    .append("&requestNo=").append(requestNo)
                    .append("&userId=").append(userId)
                    .append("&key=").append(key);
            sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(sb.toString());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount",amount);
        jsonObject.put("action",action);
        jsonObject.put("remarks",remarks);
        jsonObject.put("platform",platform);
        jsonObject.put("sign",sign);
        jsonObject.put("requestNo",requestNo);
        jsonObject.put("requestTime",requestTime);
        jsonObject.put("userId",userId);
        try {
            String result = HttpClientUtil.httpPostJsonRequest(tcoinBaseUrl+queryUri+userId,jsonObject.toJSONString(), timeout);
            JSONObject resultJson = JSONObject.parseObject(result);
            TcoinAck tcoinAck = new TcoinAck();
            tcoinAck.setResultCode((String)resultJson.get("resultCode"));
            tcoinAck.setResultMsg((String)resultJson.get("resultMsg"));
            return tcoinAck;
        } catch (UnsupportedEncodingException e) {
            logger.error("operateTcoin UnsupportedEncodingException:"+userId);
            logger.error(e.getMessage());
            return TcoinAck.renderFailure();
        } catch (Exception e){
            logger.error("operateTcoin error:"+userId);
            logger.error(e.getMessage());
            return TcoinAck.renderFailure();
        }
    }

    public TcoinAck chongzhengTcoin(String userId, Long amount, String requestNo){
        if(amount == 0l || StringUtils.isEmpty(userId) || StringUtils.isEmpty(requestNo)){
            return null;
        }
        String platform = "JF";
        String requestTime = DateUtils.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd HH:mm:ss");
        String action = "CHONGZHENG";
        String remarks = "冲正";
        String sign = "";
        if(amount>0){
            StringBuilder sb = new StringBuilder();
            sb.append("action=").append(action)
                    .append("&amount=").append(amount)
                    .append("&platform=").append(platform)
                    .append("&requestNo=").append(requestNo)
                    .append("&userId=").append(userId)
                    .append("&key=").append(key);
            sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(sb.toString());
        }else{
            StringBuilder sb = new StringBuilder();
            sb.append("action=").append(action)
                    .append("&amount=").append(amount)
                    .append("&platform=").append(platform)
                    .append("&requestNo=").append(requestNo)
                    .append("&userId=").append(userId)
                    .append("&key=").append(key);
            sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(sb.toString());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount",amount);
        jsonObject.put("action",action);
        jsonObject.put("remarks",remarks);
        jsonObject.put("platform",platform);
        jsonObject.put("sign",sign);
        jsonObject.put("requestNo",requestNo);
        jsonObject.put("requestTime",requestTime);
        jsonObject.put("userId",userId);
        try {
            System.out.println(jsonObject.toJSONString());
            String result = HttpClientUtil.httpPostJsonRequest(tcoinBaseUrl+queryUri+userId+chongzhengUri, jsonObject.toJSONString(), timeout);
            JSONObject resultJson = JSONObject.parseObject(result);
            TcoinAck tcoinAck = new TcoinAck();
            tcoinAck.setResultCode((String)resultJson.get("resultCode"));
            tcoinAck.setResultMsg((String)resultJson.get("resultMsg"));
            return tcoinAck;
        } catch (UnsupportedEncodingException e) {
            logger.error("chongzhengTcoin UnsupportedEncodingException:"+userId);
            logger.error(e.getMessage());
            return TcoinAck.renderFailure();
        } catch (Exception e){
            logger.error("chongzhengTcoin error:"+userId);
            logger.error(e.getMessage());
            return TcoinAck.renderFailure();
        }
    }

    public static void main(String[] args){
        //String userId = "EAC6C65C1B4F472793E236D9996C4B53";
        //String userId = "6075C1290FF04488A15D4858E7CEDDE9";//未实名认证账户 开发环境
        //String userId = "2016032114132513850905fdb6b354b89acef1d0c82ee4a1f";//实名认证账户 开发环境
        String userId = "20150730230656200fe982b05a1724325bf8090497f109331";
        TcoinOperation tcoinOperation = new TcoinOperation();
        //System.out.println(tcoinOperation.realNameCheck(userId));
        //System.out.println(tcoinOperation.chongzhengTcoin(userId,-4l,"TCOINac847414876445982940"));
        System.out.println(tcoinOperation.queryTcoinBalance(userId));
        //System.out.println(tcoinOperation.operateTcoin(userId,2000l));
    }

}
