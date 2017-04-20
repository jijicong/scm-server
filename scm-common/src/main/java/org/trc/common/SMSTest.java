package org.trc.common;

import org.trc.util.HttpClientUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by george on 2017/3/30.
 */
public class SMSTest {

    public static final String platformCode = "pub";	//elife_cl

    public static final String templateCode = "pub_score_kcgj";//elife_test

    public static final String url = "https://open.trc.com/trcsms/sms/message/rendered";

    public static void main(String[] args){
        try {
            String phone = "15669003888";
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("phone", phone);
            params.put("platformCode", platformCode);
            params.put("type", templateCode);
            params.put("content", "商品(abc)达到库存预警【泰然城】");
            String result = HttpClientUtil.httpPostRequest(url, params, 1000);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("调用短信接口异常");
        }

    }

}
