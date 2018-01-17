package org.trc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.resource.QiniuResource;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件名称为：URLAvailability.java
 * 文件功能简述： 描述一个URL地址是否有效
 *
 */
public class URLAvailability {

    private static Logger log = LoggerFactory.getLogger(QiniuResource.class);

    private static URL url;
    private static HttpURLConnection con;
    private static int state = -1;
        private final static int TRY_TIMES = 15;
    private final static long WAIT_SECONDS = 300;//单位/毫秒

    /**
     * 功能：检测当前URL是否可连接或是否有效,
     * 描述：最多连接网络 TRY_TIMES 次, 如果 TRY_TIMES 次都不成功，视为该ßßß地址不可用
     * @param urlStr 指定URL网络地址
     * @return URL
     */
    public static synchronized boolean isConnect(String urlStr) {
        boolean flag = false;
        int counts = 0;
        if (urlStr == null || urlStr.length() <= 0) {
            return flag;
        }
        while (counts < TRY_TIMES) {
            try {
                url = new URL(urlStr);
                con = (HttpURLConnection) url.openConnection();
                state = con.getResponseCode();
                log.info(String.format("检查url路径%s是否可用,当前第%s次", urlStr, counts+1));
                if (state == 200) {
                    flag = true;
                    log.info(String.format("url路径%s 可用", urlStr));
                    break;
                }else {
                    counts++;
                    Thread.sleep(WAIT_SECONDS);
                }
            }catch (Exception ex) {
                counts++;
                log.error(String.format("URL不可用，连接第 %s 次", counts+1), ex);
                urlStr = null;
                try {
                    Thread.sleep(WAIT_SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
        }
        return flag;
    }

    public static void main(String args[]){
        boolean flag = isConnect("https://scm.trc.com/property%2F3427960537367.png");
        System.out.println(flag);
    }

}