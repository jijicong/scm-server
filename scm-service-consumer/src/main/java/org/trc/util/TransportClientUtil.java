package org.trc.util;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by hzszy on 2017/7/17.
 */
public class TransportClientUtil {
    private static Logger log = LoggerFactory.getLogger(TransportClientUtil.class);
    // 创建私有对象
    private static TransportClient client;
    private static Settings settings = Settings.builder().put("cluster.name", "es-application").build();

    private TransportClientUtil() {
    }

    static {
        try {
            client = new PreBuiltTransportClient(settings);
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.30.250.164"), 9300));
        } catch (Exception e) {
            log.error("TransportClient初始化异常" + e.getMessage(), e);
        }
    }

    // 取得实例
    public static synchronized TransportClient getTransportClient() {
        return client;
    }
}
