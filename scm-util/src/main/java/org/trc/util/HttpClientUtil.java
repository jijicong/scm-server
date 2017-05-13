package org.trc.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class HttpClientUtil {

    private static PoolingHttpClientConnectionManager cm;
    private static String EMPTY_STR = "";
    private static String UTF_8 = "UTF-8";
    public static final int SOCKET_TIMEOUT = 1000;//超时时间

    private static void init() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(50);// 整个连接池最大连接数
            cm.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
        }
    }

    /**
     * 通过连接池获取HttpClient
     *
     * @return
     */
    private static CloseableHttpClient getHttpClient() {
        init();
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * @param url
     * @return
     */
    public static String httpGetRequest(String url) {
        HttpGet httpGet = new HttpGet(url);
        return getResult(httpGet);
    }

    public static String httpGetRequest(String url, Map<String, Object> params, Integer timeout) throws URISyntaxException {
        URIBuilder ub = _createURIBuilder(url,params);
        HttpGet httpGet = new HttpGet(ub.build());
        int time_out = null!=timeout?timeout:SOCKET_TIMEOUT;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(time_out).setConnectTimeout(time_out).build();//设置请求和传输超时时间
        httpGet.setConfig(requestConfig);
        return getResult(httpGet);
    }

    private static URIBuilder _createURIBuilder(String url, Map<String, Object> params){
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        ub.setParameters(pairs);
        return ub;
    }

    public static String httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws URISyntaxException {
        URIBuilder ub = _createURIBuilder(url,params);
        HttpGet httpGet = new HttpGet(ub.build());
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }

    public static String httpPostRequest(String url) {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> params, Integer timeout) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        int time_out = null!=timeout?timeout:SOCKET_TIMEOUT;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(time_out).setConnectTimeout(time_out).build();//设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        return getResult(httpPost);
    }

    public static String httpPostJsonRequest(String url, String params, Integer timeout) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE,"application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(params, Charset.forName("UTF-8")));
        int time_out = null!=timeout?timeout:SOCKET_TIMEOUT;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(time_out).setConnectTimeout(time_out).build();//设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        return getResult(httpPost);
    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

    /**
     * 处理Http请求
     *
     * @param request
     * @return
     */
    private static String getResult(HttpRequestBase request) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            // response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // long len = entity.getContentLength();// -1 表示长度未知
                String result = EntityUtils.toString(entity);
                response.close();
                // httpClient.close();
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return EMPTY_STR;
    }

    public static void main(String [] args){

        String tbUrl = "http://101.71.241.100:48080/trcloanweb/tb/";
        String userId = "6075C1290FF04488A15D4858E7CEDDE9";

        Map<String, Object> params = new HashMap();
        params.put("userId",userId);

        try {
            System.out.println(HttpClientUtil.httpGetRequest(tbUrl+userId,params,200));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

//        String key = "xfjghd9i9583k";
//        String action = "JF2T";
//        int amount = 300;
//        String platform = "JF";
//        String requestNo = GuidUtil.getNextUid("tb");
//        StringBuilder sb = new StringBuilder();
//        sb.append("action=").append(action)
//                .append("&amount=").append(amount)
//                .append("&platform=").append(platform)
//                .append("&requestNo=").append(requestNo)
//                .append("&userId=").append(userId)
//                .append("&key=").append(key);
//        String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(sb.toString());
//        String requestTime = DateUtils.formatDate(Calendar.getInstance().getTime(),"yyyy-MM-dd HH:mm:ss");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("amount",amount);
//        jsonObject.put("action",action);
//        jsonObject.put("remarks","积分兑换T币");
//        jsonObject.put("platform",platform);
//        jsonObject.put("sign",sign);
//        jsonObject.put("requestNo",requestNo);
//        jsonObject.put("requestTime",requestTime);
//        jsonObject.put("userId",userId);
//
//        try {
//            System.out.println(HttpClientUtil.httpPostJsonRequest(tbUrl+userId,jsonObject.toJSONString()));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String couponsUrl = "http://172.30.248.230:8080/couponToB/api/biz/event/{eid}/checkEvent";
//        String eid = "E001";
//        String reqUrl = couponsUrl.replaceAll("\\{eid\\}",eid);
//        Map<String, Object> params = new HashMap();
//        params.put("eid",eid);
//        try {
//            System.out.println(HttpClientUtil.httpGetRequest(reqUrl,params));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

    }

}