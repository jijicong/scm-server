package org.trc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringEscapeUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trc.constant.LiangYouConstant;
import org.trc.form.liangyou.*;
import org.trc.service.ILiangYouService;
import org.trc.util.HttpClientUtil;
import org.trc.util.HttpRequestUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by hzwyz on 2017/6/13 0013.
 */
@Service("LiangYouService")
public class LiangYouServiceImpl implements ILiangYouService {

    private final static Logger log = LoggerFactory.getLogger(JDServiceImpl.class);

    @Value("${liangyou.app_id}")
    private String APP_ID;

    @Value("${liangyou.v}")
    private String VERSION;

    @Value("${liangyou.state}")
    private String STATE;

    @Value("${liangyou.app_secret}")
    private String APP_SECRET;

    @Override
    public ResultType<JSONObject> getToken() throws Exception {
        Map<String,String> param=new HashMap<>();
        param.put("v", VERSION);
        param.put("app_id", APP_ID);
        param.put("state", STATE);
        param.put("app_secret", APP_SECRET);
        Map<String,String> res=paraFilter(param);
        String str=createLinkString(res);
        str= DigestUtils.md5Hex(str);
        Map<String,String> paramValue=new HashMap<>();
        paramValue.put("app_id", APP_ID);
        paramValue.put("sig", str);
        paramValue.put("app", LiangYouConstant.APP);
        paramValue.put("v", VERSION);
        paramValue.put("act", LiangYouConstant.TOKEN_ACT);
        paramValue.put("state", STATE);
        String paramStr=createLinkString(paramValue);
        String result= HttpRequestUtil.sendGetNoUrlEncode(LiangYouConstant.URL,paramStr);
        JSONObject json=JSONObject.parseObject(result);
        JSONObject data=json.getJSONObject("data");
        Integer code = (Integer) json.get("code");
        String message = (String) json.get("message");
        ResultType<JSONObject> resultType = new ResultType<JSONObject>(code,message,data);
        return resultType;
    }

    @Override
    public ResultType<JSONObject> exportGoods(String accessToken,String page) throws Exception {
        Map<String,String> paramValue=new HashMap<>();
        paramValue.put("app", LiangYouConstant.APP);
        paramValue.put("act", LiangYouConstant.GOODS_ACT);
        paramValue.put("access_token", accessToken);
        paramValue.put("page", page);
        String paramStr=createLinkString(paramValue);
        String result=HttpRequestUtil.sendGetNoUrlEncode(LiangYouConstant.URL,paramStr);
        result= StringEscapeUtils.unescapeJava(result);
        JSONObject json=JSONObject.parseObject(result);
        JSONObject data=json.getJSONObject("data");
        Integer code = (Integer) json.get("code");
        String message = (String) json.get("message");
        return new ResultType<JSONObject>(code,message,data);
    }

    @Override
    public ResultType<JSONArray> checkStock(String accessToken,String sku) throws Exception {
        Map<String,String> paramValue=new HashMap<>();
        paramValue.put("app", LiangYouConstant.APP);
        paramValue.put("act", LiangYouConstant.STOCK_ACT);
        paramValue.put("access_token", accessToken);
        paramValue.put("sku", sku);
        String paramStr=createLinkString(paramValue);
        String result=HttpRequestUtil.sendGetNoUrlEncode(LiangYouConstant.URL,paramStr);
        result=StringEscapeUtils.unescapeJava(result);
        JSONObject json=JSONObject.parseObject(result);
        JSONArray data=json.getJSONArray("data");
        Integer code = (Integer) json.get("code");
        String message = (String) json.get("message");
        return new ResultType<JSONArray>(code,message,data);
    }

    @Override
    public ResultType<JSONObject> addOutOrder(LiangYouOrderDO liangYouOrderDO) throws Exception {
        Map<String,Object> paramValue=new HashMap<>();
        JSONObject data = new JSONObject();
        paramValue.put("app", LiangYouConstant.APP);
        paramValue.put("act", LiangYouConstant.ADD_OUT_ORDER_ACT);
        paramValue.put("app_id", APP_ID);
        paramValue.put("v", VERSION);
        JSONObject paramjson =new JSONObject();
        paramjson.put("consignee", URLEncoder.encode(liangYouOrderDO.getConsignee(),"UTF-8"));
        paramjson.put("order_sn", liangYouOrderDO.getOrderSn());
        paramjson.put("out_order_sn", liangYouOrderDO.getOutOrderSn());
        paramjson.put("realName",URLEncoder.encode(liangYouOrderDO.getRealName(),"UTF-8"));
        paramjson.put("imId", liangYouOrderDO.getImId());
        paramjson.put("disType",LiangYouConstant.DIS_TYPE);
        paramjson.put("phoneMob", liangYouOrderDO.getPhoneMob());
        paramjson.put("address",URLEncoder.encode(liangYouOrderDO.getAddress(),"UTF-8"));
        paramjson.put("province",URLEncoder.encode(liangYouOrderDO.getProvince(),"UTF-8"));
        paramjson.put("city",URLEncoder.encode(liangYouOrderDO.getCity(),"UTF-8"));
        paramjson.put("county",URLEncoder.encode(liangYouOrderDO.getCounty(),"UTF-8"));
        paramjson.put("shipping_id", liangYouOrderDO.getShippingId());
        JSONArray outOrderGoods=new JSONArray();
        for (OutOrderGoods good: liangYouOrderDO.getOutOrderGoods()) {
            JSONObject outOrderGood=new JSONObject();
            outOrderGood.put("goods_name",URLEncoder.encode(good.getGoodsName(),"UTF-8"));
            outOrderGood.put("only_sku",good.getOnlySku());
            outOrderGood.put("quantity",good.getQuantity());
            outOrderGoods.add(outOrderGood);
        }
        paramjson.put("outOrderGoods",outOrderGoods);
        paramValue.put("paramjson",encrypt(paramjson.toString(),LiangYouConstant.AES_KEY));
        String result = HttpClientUtil.httpPostRequest(LiangYouConstant.URL,paramValue,5000);
        JSONObject json=JSONObject.parseObject(result);
        Integer code = (Integer) json.get("code");
        String message = (String) json.get("message");
        //可能发生解密失败
        if (StringUtils.isEquals(message,"error")){
            data = (JSONObject) json.get("data");
            return new ResultType<JSONObject>(code,message,data);
        }
        data = JSONObject.parseObject(decrypt(json.getString("data"),LiangYouConstant.AES_KEY));
        return new ResultType<JSONObject>(code,message,data);
    }

    @Override
    public ResultType<JSONObject> addToutOrder(LiangYouTorderDO orderDO) throws Exception {
        Map<String,Object> paramValue=new HashMap<>();
        JSONObject data = new JSONObject();
        paramValue.put("app", LiangYouConstant.APP);
        paramValue.put("act", LiangYouConstant.ADD_TOUT_ORDER_ACT);
        paramValue.put("app_id", APP_ID);
        paramValue.put("v", VERSION);
        JSONObject paramjson =new JSONObject();
        paramjson.put("consignee", URLEncoder.encode(orderDO.getConsignee(),"UTF-8"));
        paramjson.put("order_sn", orderDO.getOrderSn());
        paramjson.put("realName",URLEncoder.encode(orderDO.getRealName(),"UTF-8"));
        //微信商户机构号（如果是微信支付此项必填）
        if (StringUtils.isEquals(orderDO.getPaymentId(),LiangYouConstant.WEIXIN))
        paramjson.put("account_id",orderDO.getAccountId());
        if (!StringUtils.isBlank(orderDO.getImId())){
            paramjson.put("imId", orderDO.getImId());
        }
        paramjson.put("disType",LiangYouConstant.DIS_TYPE);
        paramjson.put("payment_id",orderDO.getPaymentId());
        paramjson.put("tradeNum",orderDO.getTradeNum());
        paramjson.put("out_order_sn",orderDO.getOutOrderSn());
        paramjson.put("order_amount",orderDO.getOrderAmount());
        paramjson.put("phoneMob", orderDO.getPhoneMob());
        paramjson.put("address",URLEncoder.encode(orderDO.getAddress(),"UTF-8"));
        paramjson.put("province",URLEncoder.encode(orderDO.getProvince(),"UTF-8"));
        paramjson.put("city",URLEncoder.encode(orderDO.getCity(),"UTF-8"));
        paramjson.put("county",URLEncoder.encode(orderDO.getCounty(),"UTF-8"));
        paramjson.put("shipping_id", orderDO.getShippingId());
        paramjson.put("shipping_fee",orderDO.getShippingFee());
        JSONArray outOrderGoods=new JSONArray();
        for (OutTorderGoods good: orderDO.getOutTorderGoods()) {
            JSONObject outOrderGood=new JSONObject();
            outOrderGood.put("goods_name",URLEncoder.encode(good.getGoodsName(),"UTF-8"));
            outOrderGood.put("only_sku",good.getOnlySku());
            outOrderGood.put("quantity",good.getQuantity());
            outOrderGood.put("price",good.getPrice());
            outOrderGoods.add(outOrderGood);
        }
        paramjson.put("outOrderGoods",outOrderGoods);
        paramValue.put("paramjson",encrypt(paramjson.toString(),LiangYouConstant.AES_KEY));
        String result = HttpClientUtil.httpPostRequest(LiangYouConstant.URL,paramValue,5000);
        JSONObject json=JSONObject.parseObject(result);
        Integer code = (Integer) json.get("code");
        String message = (String) json.get("message");
        //可能发生解密失败
        if (StringUtils.isEquals(message,"error")){
            data = (JSONObject) json.get("data");
            return new ResultType<JSONObject>(code,message,data);
        }
        data = JSONObject.parseObject(decrypt(json.getString("data"),LiangYouConstant.AES_KEY));
        return new ResultType<JSONObject>(code,message,data);
    }

    @Override
    public ResultType<JSONObject> getOrderStatus(String orderSn) throws Exception {
        Map<String,Object> paramValue=new HashMap<>();
        JSONObject data = new JSONObject();
        paramValue.put("app", LiangYouConstant.APP);
        paramValue.put("act", LiangYouConstant.ORDER_STATE_ACT);
        paramValue.put("app_id", APP_ID);
        paramValue.put("v", VERSION);
        paramValue.put("order_sn", encrypt(orderSn,LiangYouConstant.AES_KEY));
        String result = HttpClientUtil.httpPostRequest(LiangYouConstant.URL,paramValue,5000);
        result= StringEscapeUtils.unescapeJava(result);
        JSONObject json=JSONObject.parseObject(result);
        Integer code = (Integer) json.get("code");
        String message = (String) json.get("message");
        //可能发生解密失败
        if (StringUtils.isEquals(message,"error")){
            data = (JSONObject) json.get("data");
            return new ResultType<JSONObject>(code,message,data);
        }
        data = JSONObject.parseObject(decrypt(json.getString("data"),LiangYouConstant.AES_KEY));
        return new ResultType<JSONObject>(code,message,data);
    }

    @Override
    public ResultType<JSONObject> getGoodsInfo(String accessToken,String sku) throws Exception {
        Map<String,String> paramValue=new HashMap<>();
        paramValue.put("app", LiangYouConstant.APP);
        paramValue.put("act", LiangYouConstant.GOODS_INFO_ACT);
        paramValue.put("access_token", accessToken);
        paramValue.put("sku", sku);
        String paramStr=createLinkString(paramValue);
        String result=HttpRequestUtil.sendGetNoUrlEncode(LiangYouConstant.URL,paramStr);
        result= StringEscapeUtils.unescapeJava(result);
        JSONObject json=JSONObject.parseObject(result);
        Integer code = (Integer) json.get("code");
        String message = (String) json.get("message");
        JSONObject data = (JSONObject) json.get("data");
        return new ResultType<JSONObject>(code,message,data);
    }

    /**
     * 粮油待签名字符串冒泡排序
     * @param sArray
     * @return
     */
    private static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map result = new HashMap();
        if ((sArray == null) || (sArray.size() <= 0)) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = (String) sArray.get(key);
            if ((value == null) || (value.equals("")) || (key.equalsIgnoreCase("sign")) || (key.equalsIgnoreCase("sign_type"))) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 粮油构造待签名字符串
     * @param params
     * @return
     */
    private static String createLinkString(Map<String, String> params) {
        List keys = new ArrayList(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = (String) params.get(key);

            if (i == keys.size() - 1)
                prestr = prestr + key + "=" + value;
            else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    private static String convert(String utfString){

        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while((i=utfString.indexOf("\\u", pos)) != -1){
            sb.append(utfString.substring(pos, i));
            if(i+5 < utfString.length()){
                pos = i+6;
                sb.append((char)Integer.parseInt(utfString.substring(i+2, i+6), 16));
            }
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
    /**
     * AES 加密
     * @param input 待加密字符串
     * @param key app_secret
     * @return
     */
    public static String encrypt(String input, String key){
        byte[] crypted = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes("UTF-8"));
        }catch(Exception e){
            System.out.println(e.toString());
        }
        return new String(Base64.getEncoder().encode(crypted));
    }

    /**
     * AES解密
     * @param input 待解密字符串
     * @param key app_secret
     * @return
     */
    public static String decrypt(String input, String key){
        byte[] output = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.getDecoder().decode(input.getBytes()));
        }catch(Exception e){
            System.out.println(e.toString());
        }
        return new String(output);
    }
}
