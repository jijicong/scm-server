package org.trc.util;

import com.taobao.api.internal.spi.CheckResult;
import com.taobao.api.internal.util.StringUtils;
import com.taobao.api.internal.util.TaobaoUtils;
import com.taobao.api.internal.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author hzszy
 */
public class SpiUtils {
    private static Logger log = LoggerFactory.getLogger(SpiUtils.class);
    private static final String TOP_SIGN_LIST = "top-sign-list";
    private static final String[] HEADER_FIELDS_IP = new String[]{"X-Real-IP", "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    public SpiUtils() {
    }

    public static CheckResult checkSign(HttpServletRequest request, String secret) throws IOException {
        CheckResult result = new CheckResult();
        String ctype = request.getContentType();
        String charset = WebUtils.getResponseCharset(ctype);
        if (!ctype.startsWith("application/json") && !ctype.startsWith("text/xml") && !ctype.startsWith("text/plain")&&!ctype.startsWith("application/xml; charset=utf-8")) {
            if (!ctype.startsWith("application/x-www-form-urlencoded")) {
                throw new RuntimeException("Unspported SPI request");
            }

            boolean valid = checkSignInternal(request, (Map)null, (String)null, secret, charset);
            result.setSuccess(valid);
        } else {
            String body = WebUtils.getStreamAsString(request.getInputStream(), charset);
            boolean valid = checkSignInternal(request, (Map)null, body, secret, charset);
            result.setSuccess(valid);
            result.setRequestBody(body);
        }

        return result;
    }

    /** @deprecated */
    public static boolean checkSign4FormRequest(HttpServletRequest request, String secret) throws IOException {
        String charset = WebUtils.getResponseCharset(request.getContentType());
        return checkSignInternal(request, (Map)null, (String)null, secret, charset);
    }

    /** @deprecated */
    public static boolean checkSign4TextRequest(HttpServletRequest request, String body, String secret) throws IOException {
        String charset = WebUtils.getResponseCharset(request.getContentType());
        return checkSignInternal(request, (Map)null, body, secret, charset);
    }

    public static boolean checkSign4FileRequest(HttpServletRequest request, Map<String, String> form, String secret) throws IOException {
        String charset = WebUtils.getResponseCharset(request.getContentType());
        return checkSignInternal(request, form, (String)null, secret, charset);
    }

    private static boolean checkSignInternal(HttpServletRequest request, Map<String, String> form, String body, String secret, String charset) throws IOException {
        Map<String, String> params = new HashMap();
        Map<String, String> headerMap = getHeaderMap(request, charset);
        params.putAll(headerMap);
        Map<String, String> queryMap = getQueryMap(request, charset);
        params.putAll(queryMap);
        if (form == null && body == null) {
            Map<String, String> formMap = getFormMap(request, queryMap);
            params.putAll(formMap);
        } else if (form != null) {
            params.putAll(form);
        }

        String remoteSign = (String)queryMap.get("sign");
        log.info("remoteSign:"+remoteSign);
        String localSign = sign(params, body, secret, charset);
        log.info("localSign:"+localSign);
        if (localSign.equals(remoteSign)) {
            return true;
        } else {
            String paramStr = getParamStrFromMap(params);
            log.error("checkTopSign error^_^remoteSign=" + remoteSign + "^_^localSign=" + localSign + "^_^paramStr=" + paramStr + "^_^body=" + body);
            return false;
        }
    }

    public static Map<String, String> getHeaderMap(HttpServletRequest request, String charset) throws IOException {
        Map<String, String> headerMap = new HashMap();
        String signList = request.getHeader("top-sign-list");
        if (!StringUtils.isEmpty(signList)) {
            String[] keys = signList.split(",");
            String[] arr$ = keys;
            int len$ = keys.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                String value = request.getHeader(key);
                if (StringUtils.isEmpty(value)) {
                    headerMap.put(key, "");
                } else {
                    headerMap.put(key, URLDecoder.decode(value, charset));
                }
            }
        }

        return headerMap;
    }

    public static Map<String, String> getQueryMap(HttpServletRequest request, String charset) throws IOException {
        Map<String, String> queryMap = new HashMap();
        String queryString = request.getQueryString();
        String[] params = queryString.split("&");

        for(int i = 0; i < params.length; ++i) {
            String[] kv = params[i].split("=");
            String key;
            if (kv.length == 2) {
                key = URLDecoder.decode(kv[0], charset);
                String value = URLDecoder.decode(kv[1], charset);
                queryMap.put(key, value);
            } else if (kv.length == 1) {
                key = URLDecoder.decode(kv[0], charset);
                queryMap.put(key, "");
            }
        }

        return queryMap;
    }

    public static Map<String, String> getFormMap(HttpServletRequest request, Map<String, String> queryMap) throws IOException {
        Map<String, String> formMap = new HashMap();
        Set<?> keys = request.getParameterMap().keySet();
        Iterator i$ = keys.iterator();

        while(i$.hasNext()) {
            Object tmp = i$.next();
            String key = String.valueOf(tmp);
            if (!queryMap.containsKey(key)) {
                String value = request.getParameter(key);
                if (StringUtils.isEmpty(value)) {
                    formMap.put(key, "");
                } else {
                    formMap.put(key, value);
                }
            }
        }

        return formMap;
    }

    public static String getStreamAsString(InputStream stream, String charset) throws IOException {
        return WebUtils.getStreamAsString(stream, charset);
    }

    private static String sign(Map<String, String> params, String body, String secret, String charset) throws IOException {
        StringBuilder sb = new StringBuilder(secret);
        sb.append(getParamStrFromMap(params));
        if (body != null) {
            sb.append(body);
        }

        sb.append(secret);
        byte[] bytes = TaobaoUtils.encryptMD5(sb.toString().getBytes(charset));
        return TaobaoUtils.byte2hex(bytes);
    }

    private static String getParamStrFromMap(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            String[] keys = (String[])params.keySet().toArray(new String[0]);
            Arrays.sort(keys);

            for(int i = 0; i < keys.length; ++i) {
                String name = keys[i];
                if (!"sign".equals(name)) {
                    sb.append(name);
                    sb.append((String)params.get(name));
                }
            }
        }

        return sb.toString();
    }

    public static boolean checkTimestamp(HttpServletRequest request, int minutes) {
        String ts = request.getParameter("timestamp");
        if (ts != null) {
            long remote = StringUtils.parseDateTime(ts).getTime();
            long local = Calendar.getInstance().getTime().getTime();
            return local - remote <= (long)(minutes * 60) * 1000L;
        } else {
            return false;
        }
    }

    public static boolean checkRemoteIp(HttpServletRequest request, List<String> topIpList) {
        String ip = request.getRemoteAddr();
        String[] arr$ = HEADER_FIELDS_IP;
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String ipHeader = arr$[i$];
            String realIp = request.getHeader(ipHeader);
            if (!StringUtils.isEmpty(realIp) && !"unknown".equalsIgnoreCase(realIp)) {
                ip = realIp;
                break;
            }
        }

        if (topIpList != null) {
            Iterator i$ = topIpList.iterator();

            while(i$.hasNext()) {
                String topIp = (String)i$.next();
                if (StringUtils.isIpInRange(ip, topIp)) {
                    return true;
                }
            }
        }

        return false;
    }
}
