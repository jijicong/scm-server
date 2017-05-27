package org.trc.service.impl.trc.util;

import java.util.Properties;

/**
 * Created by hzdzf on 2017/5/27.
 */
public class CommomUtils {

    public static String getKey() throws Exception{
        Properties properties = new Properties();
        properties.load(CommomUtils.class.getResourceAsStream("/config/dev/tairan.properties"));
        return properties.getProperty("key");
    }

}
