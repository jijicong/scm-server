package org.trc.util;

import org.trc.enums.CommonExceptionEnum;
import org.trc.exception.ParamValidException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyh on 2017/9/4.
 */
public class ValidateUtil {
    /**
     * 邮箱正则表达式
     */
    public final static String EMAIL_REGEX = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 验证邮箱
     * @param email
     * @return
     */
    public static void checkEmail(String email){
        Pattern regex = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = regex.matcher(email);
        boolean flag = matcher.matches();
        if(!flag)
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "电子邮箱格式错误");
    }


}
