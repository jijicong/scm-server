package org.trc.common;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.util.HttpClientUtil;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by george on 2016/12/21.
 */
public class TrCouponOperation {

    private Logger logger = LoggerFactory.getLogger(TcoinOperation.class);
    private int timeout = 3000;//超时时间
    //pro
//    private String couponsBaseUrl = "http://115.236.23.59:62880/couponToB/api/biz/event/{eid}/checkEvent";
//    private String exchangeUrl = "http://115.236.23.59:62880/couponToB/api/biz/coupon";
    //dev
    private String couponsBaseUrl = "http://172.30.248.230:8080/couponToB/api/biz/event/{eid}/checkEvent";
    private String exchangeUrl = "http://172.30.248.230:8080/couponToB/api/biz/coupon";

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getCouponsBaseUrl() {
        return couponsBaseUrl;
    }

    public void setCouponsBaseUrl(String couponsBaseUrl) {
        this.couponsBaseUrl = couponsBaseUrl;
    }

    public String getExchangeUrl() {
        return exchangeUrl;
    }

    public void setExchangeUrl(String exchangeUrl) {
        this.exchangeUrl = exchangeUrl;
    }

    public TrCouponAck checkEid(String eid){
        String reqUrl = couponsBaseUrl.replaceAll("\\{eid\\}",eid);
        Map<String, Object> params = new HashMap();
        params.put("eid",eid);
        try {
            String result = HttpClientUtil.httpGetRequest(reqUrl,params,timeout);
            JSONObject resultJson = JSONObject.parseObject(result);
            TrCouponAck trCouponAck = new TrCouponAck();
            trCouponAck.setCode((String)resultJson.get("code"));
            trCouponAck.setMessage((String)resultJson.get("message"));
            trCouponAck.setData((String)resultJson.get("data"));
            return trCouponAck;
        } catch (URISyntaxException e) {
            logger.error("checkEid error:"+eid);
            e.printStackTrace();
            return TrCouponAck.renderFailure();
        } catch (Exception e){
            logger.error("checkEid error:"+eid);
            logger.error(e.getMessage());
            return TrCouponAck.renderFailure();
        }
    }

    public TrCouponAck checkEid2(String eid){
        String reqUrl = couponsBaseUrl.replaceAll("\\{eid\\}",eid)+"2";
        Map<String, Object> params = new HashMap();
        params.put("eid",eid);
        try {
            String result = HttpClientUtil.httpGetRequest(reqUrl, params, timeout);
            JSONObject resultJson = JSONObject.parseObject(result);
            TrCouponAck trCouponAck = new TrCouponAck();
            trCouponAck.setCode((String)resultJson.get("code"));
            trCouponAck.setMessage((String)resultJson.get("message"));
            JSONObject dataJson = resultJson.getJSONObject("data");
            trCouponAck.setPackageFrom(dataJson.getLong("packageFrom"));
            trCouponAck.setPackageTo(dataJson.getLong("packageTo"));
            return trCouponAck;
        } catch (URISyntaxException e) {
            logger.error("checkEid error:"+eid);
            e.printStackTrace();
            return TrCouponAck.renderFailure();
        } catch (Exception e){
            logger.error("checkEid error:"+eid);
            logger.error(e.getMessage());
            return TrCouponAck.renderFailure();
        }
    }

    public TrCouponAck exchangeCoupon(String uid, String eid){

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uid",uid);
        jsonObject.put("eid",eid);
        jsonObject.put("channel","trc_score");
        jsonObject.put("subChannel","PC");
        try {
            String result = HttpClientUtil.httpPostJsonRequest(exchangeUrl, jsonObject.toJSONString(), timeout);
            JSONObject resultJson = JSONObject.parseObject(result);
            TrCouponAck trCouponAck = new TrCouponAck();
            trCouponAck.setCode((String)resultJson.get("code"));
            trCouponAck.setMessage((String)resultJson.get("message"));
            trCouponAck.setData((String)resultJson.get("data"));
            return trCouponAck;
        } catch (UnsupportedEncodingException e) {
            logger.error("exchangeCoupon error:"+uid);
            e.printStackTrace();
            return TrCouponAck.renderFailure();
        } catch (Exception e){
            logger.error("exchangeCoupon error:"+uid);
            logger.error(e.getMessage());
            return TrCouponAck.renderFailure();
        }
    }

    public static void main(String[] args){

        //String userId = "EAC6C65C1B4F472793E236D9996C4B53";
        String userId = "6075C1290FF04488A15D4858E7CEDDE9";
        TrCouponOperation trCouponOperation = new TrCouponOperation();
        //System.out.println(trCouponOperation.checkEid("w04L488QvGtypwHU"));
        System.out.println(trCouponOperation.checkEid2("E001"));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1477933200000l);
        System.out.println(cal.getTime());
        cal.setTimeInMillis(1514822159000l);
        System.out.println(cal.getTime());
        boolean dis = true;
        System.out.println(dis==true);
        //System.out.println(trCouponOperation.exchangeCoupon(userId,"E002"));

    }

}
