package org.trc.util;

import com.tairanchina.csp.foundation.common.sdk.CommonConfig;
import com.tairanchina.csp.foundation.sdk.CSPKernelSDK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonConfigUtil {
    private static Logger log = LoggerFactory.getLogger(CommonConfigUtil.class);
    // 创建私有对象
    private static CSPKernelSDK sdk;
    private CommonConfigUtil() {
    }


    // 取得实例
    public static synchronized CSPKernelSDK getCSPKernelSDK(String applyUri, String applyId, String applySecret) {
        if(null == sdk){
            try {
                CommonConfig commonConfig = new CommonConfig();
                CommonConfig.Basic basicConfig = commonConfig.getBasic();
                basicConfig.setUrl(applyUri);
                basicConfig.setAppId(applyId);
                basicConfig.setAppSecret(applySecret);
                sdk = CSPKernelSDK.instance(commonConfig);
            } catch (Exception e) {
                log.error("CSPKernelSDK初始化异常" + e.getMessage(), e);
            }
        }
        return sdk;
    }
}
