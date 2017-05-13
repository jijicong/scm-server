package org.trc.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * Created by hzwdx on 2017/5/10.
 */
public class AssertUtil extends Assert{
    /**
     * 判断空字符串
     * @param str
     * @param msg
     */
    public static void notBlank(String str, String msg){
        if(StringUtils.isEmpty(str))
            throw new IllegalArgumentException(msg);
    }

}
