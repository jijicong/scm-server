package org.trc.util;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Created by hzwdx on 2017/5/10.
 */
public class AssertUtil {
    /**
     * 判断空字符串
     * @param str
     * @param msg
     */
    public static void notEmpty(String str, String msg){
        if(org.apache.commons.lang.StringUtils.isEmpty(str))
            throw new IllegalArgumentException(msg);
    }

}
