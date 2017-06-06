package org.trc.service.impl.tairan.util;

import java.util.Properties;

/**
 * Created by hzdzf on 2017/5/27.
 */
public class CommomUtils {

    private static final String keyPath = "/config/dev/tairan.properties";

    public static Properties getProperties() throws Exception {
        Properties properties = new Properties();
        properties.load(CommomUtils.class.getResourceAsStream(keyPath));
        return properties;
    }

    public static String getKey() throws Exception {
        return getProperties().getProperty("key");
    }

    public static String getBrandUrl() throws Exception {
        return getProperties().getProperty("brandUrl");
    }

    public static String getPropertyUrl() throws Exception {
        return getProperties().getProperty("propertyUrl");
    }

    public static String getCategoryUrl() throws Exception {
        return getProperties().getProperty("categoryUrl");
    }
}
