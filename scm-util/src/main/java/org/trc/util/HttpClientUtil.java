package org.trc.util;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 */
public class HttpClientUtil {

    private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    private static PoolingHttpClientConnectionManager cm;
    private static String EMPTY_STR = "";
    private static String UTF_8 = "UTF-8";
    public static final int SOCKET_TIMEOUT = 1000;//超时时间
    public static final String SOCKET_TIMEOUT_CODE = "404";//超时错误码

    private static void init() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(500);// 整个连接池最大连接数
            cm.setDefaultMaxPerRoute(100);// 每路由最大连接数，默认值是2
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
    public static String httpGetRequest(String url)throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return getResult(httpGet);
    }

    public static String httpGetRequest(String url, Map<String, Object> params) throws URISyntaxException,IOException {
        URIBuilder ub = _createURIBuilder(url,params);
        HttpGet httpGet = new HttpGet(ub.build());
        return getResult(httpGet);
    }

    public static String httpGetRequest(String url, Map<String, Object> params, Integer timeout) throws URISyntaxException,IOException {
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
            throws URISyntaxException,IOException {
        URIBuilder ub = _createURIBuilder(url,params);
        HttpGet httpGet = new HttpGet(ub.build());
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return getResult(httpGet);
    }

    public static String httpPostRequest(String s, String url, int i) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> params, Integer timeout) throws UnsupportedEncodingException,IOException {
        HttpPost httpPost = new HttpPost(url);
        int time_out = null!=timeout?timeout:SOCKET_TIMEOUT;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(time_out).setConnectTimeout(time_out).build();//设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        return getResult(httpPost);
    }

    public static String httpPostJsonRequest(String url, String params, Integer timeout) throws UnsupportedEncodingException,IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE,"application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(params, Charset.forName("UTF-8")));
        int time_out = null!=timeout?timeout:SOCKET_TIMEOUT;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(time_out).setConnectTimeout(time_out).build();//设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        return getResult(httpPost);
    }

    public static String httpPostJsonRequest(String url, String params, HttpPost httpPost, Integer timeout) throws UnsupportedEncodingException,IOException {
        httpPost.setEntity(new StringEntity(params, Charset.forName("UTF-8")));
        int time_out = null!=timeout?timeout:SOCKET_TIMEOUT;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(time_out).setConnectTimeout(time_out).build();//设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params)
            throws UnsupportedEncodingException,IOException {
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
    private static String getResult(HttpRequestBase request) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // long len = entity.getContentLength();// -1 表示长度未知
                String result = EntityUtils.toString(entity, "UTF-8");
                response.close();
                //httpClient.close();
                return result;
            }
        } catch (ClientProtocolException e) {
            log.error(String.format("HttpClientUtil执行接口调用客户端协议异常,%s", e.getMessage()), e);
        } catch (IOException e) {
            log.error(String.format("HttpClientUtil执行接口调用服务不可用,%s", e.getMessage()), e);
            throw e;
        }
        return EMPTY_STR;
    }

    public static String httpPutRequest(String url, Map<String, Object> params, Integer timeout) throws UnsupportedEncodingException,IOException {
        HttpPut httpPut = new HttpPut(url);
        int time_out = null!=timeout?timeout:SOCKET_TIMEOUT;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(time_out).setConnectTimeout(time_out).build();//设置请求和传输超时时间
        httpPut.setConfig(requestConfig);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPut.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        return getResult(httpPut);
    }

    public static void main(String [] args) {

        /*String tbUrl = "http://101.71.241.100:48080/trcloanweb/tb/";
        String userId = "6075C1290FF04488A15D4858E7CEDDE9";

        Map<String, Object> params = new HashMap();
        params.put("userId",userId);*/

        /*String tbUrl = "http://wb.bbc.com/trcapi/v1/notifyOrder";
        Map<String, Object> params = new HashMap();
        params.put("param", "{\"aaa\":123}");
        try {
            System.out.println(HttpClientUtil.httpPostRequest(tbUrl, params, 10000));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        String url = "http://hhh.www.trc.com/api/supply/sync/sku_stock";
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE,"application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        String param = "{\"noticeNum\":\"EDIT_ITEMS_ac85f515040729101400\",\"itemNaturePropery\":[{\"createTime\":1503643164000,\"id\":131,\"isDeleted\":\"0\",\"isValid\":\"0\",\"itemId\":96,\"propertyId\":20,\"propertyValueId\":55,\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503906551000}],\"skus\":[{\"barCode\":\"1234\",\"createTime\":1503643164000,\"id\":189,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"marketPrice\":1100,\"picture\":\"\",\"propertyValue\":\"17号色,X,90ml11112222,长度：50MLLLL\",\"propertyValueId\":\"65,73,63,140\",\"skuCode\":\"SP0201708250000305\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000,\"weight\":12000},{\"barCode\":\"22\",\"createTime\":1503643164000,\"id\":190,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"marketPrice\":2200,\"picture\":\"\",\"propertyValue\":\"17号色,X,100ml,长度：50MLLLL\",\"propertyValueId\":\"65,73,64,140\",\"skuCode\":\"SP0201708250000306\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503643164000,\"weight\":22000},{\"barCode\":\"33\",\"createTime\":1503643226000,\"id\":191,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"marketPrice\":33300,\"picture\":\"\",\"propertyValue\":\"17号色,X,90ml11112222\",\"propertyValueId\":\"65,73,63\",\"skuCode\":\"SP0201708250000307\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503643306000,\"weight\":3333000},{\"barCode\":\"444\",\"createTime\":1503643226000,\"id\":192,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"marketPrice\":444400,\"picture\":\"\",\"propertyValue\":\"17号色,X,100ml\",\"propertyValueId\":\"65,73,64\",\"skuCode\":\"SP0201708250000308\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503643306000,\"weight\":44444000},{\"barCode\":\"555\",\"createTime\":1503643226000,\"id\":193,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"marketPrice\":555500,\"picture\":\"\",\"propertyValue\":\"17号色,X,30ml\",\"propertyValueId\":\"65,73,94\",\"skuCode\":\"SP0201708250000309\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503643306000,\"weight\":55555000}],\"operateTime\":1504072910140,\"sign\":\"b08de1669e1a59d8852d0d2bc518a11c\",\"action\":\"EDIT_ITEMS\",\"itemSalesPropery\":[{\"createTime\":1503643164000,\"id\":344,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"17号色\",\"propertyId\":22,\"propertyValueId\":65,\"skuCode\":\"SP0201708250000305\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643164000,\"id\":345,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"X\",\"propertyId\":24,\"propertyValueId\":73,\"skuCode\":\"SP0201708250000305\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643164000,\"id\":346,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"90ml11112222\",\"propertyId\":21,\"propertyValueId\":63,\"skuCode\":\"SP0201708250000305\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503906432000},{\"createTime\":1503643164000,\"id\":347,\"isDeleted\":\"1\",\"isValid\":\"0\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"长度：50MLLLL\",\"propertyId\":47,\"propertyValueId\":140,\"skuCode\":\"SP0201708250000305\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643164000,\"id\":348,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"17号色\",\"propertyId\":22,\"propertyValueId\":65,\"skuCode\":\"SP0201708250000306\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643164000,\"id\":349,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"X\",\"propertyId\":24,\"propertyValueId\":73,\"skuCode\":\"SP0201708250000306\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643164000,\"id\":350,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"100ml\",\"propertyId\":21,\"propertyValueId\":64,\"skuCode\":\"SP0201708250000306\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503906432000},{\"createTime\":1503643164000,\"id\":351,\"isDeleted\":\"1\",\"isValid\":\"0\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"长度：50MLLLL\",\"propertyId\":47,\"propertyValueId\":140,\"skuCode\":\"SP0201708250000306\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643306000,\"id\":352,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"17号色\",\"propertyId\":22,\"propertyValueId\":65,\"skuCode\":\"SP0201708250000307\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643306000,\"id\":353,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"X\",\"propertyId\":24,\"propertyValueId\":73,\"skuCode\":\"SP0201708250000307\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643306000,\"id\":354,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"90ml11112222\",\"propertyId\":21,\"propertyValueId\":63,\"skuCode\":\"SP0201708250000307\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503906432000},{\"createTime\":1503643306000,\"id\":355,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"17号色\",\"propertyId\":22,\"propertyValueId\":65,\"skuCode\":\"SP0201708250000308\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643306000,\"id\":356,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"X\",\"propertyId\":24,\"propertyValueId\":73,\"skuCode\":\"SP0201708250000308\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643306000,\"id\":357,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"100ml\",\"propertyId\":21,\"propertyValueId\":64,\"skuCode\":\"SP0201708250000308\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503906432000},{\"createTime\":1503643306000,\"id\":358,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"17号色\",\"propertyId\":22,\"propertyValueId\":65,\"skuCode\":\"SP0201708250000309\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643306000,\"id\":359,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"X\",\"propertyId\":24,\"propertyValueId\":73,\"skuCode\":\"SP0201708250000309\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503899061000},{\"createTime\":1503643306000,\"id\":360,\"isDeleted\":\"0\",\"isValid\":\"1\",\"itemId\":96,\"picture\":\"null\",\"propertyActualValue\":\"30ml\",\"propertyId\":21,\"propertyValueId\":94,\"skuCode\":\"SP0201708250000309\",\"spuCode\":\"SPU2017082500053\",\"updateTime\":1503906432000}],\"items\":{\"brandId\":15,\"categoryId\":5,\"id\":96,\"isValid\":\"1\",\"itemNo\":\"222\",\"name\":\"新增825-wxj\",\"producer\":\"\",\"remark\":\"\",\"spuCode\":\"SPU2017082500053\",\"tradeType\":\"crossBorderInBond\",\"updateTime\":1504072905596}}";
        try {
            String response = HttpClientUtil.httpPostJsonRequest(url, param, httpPost, 3000);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}