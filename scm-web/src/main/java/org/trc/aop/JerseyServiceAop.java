package org.trc.aop;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.enums.ResultEnum;
import org.trc.util.*;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by hzwdx on 2017/4/22.
 */
@Component
@Aspect
public class JerseyServiceAop {

    private final static Logger log = LoggerFactory.getLogger(JerseyServiceAop.class);

    @Autowired
    private BeanValidator beanValidator;

    @Pointcut("within(@javax.ws.rs.Path *)")
    public void jerseyService() {
    }

    @Around("jerseyService()")
    public Object invoke(ProceedingJoinPoint point) throws Throwable {
        Class<?> targetClass = point.getTarget().getClass();//被代理的类
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();//被执行方法
        Class<?>[] paramTypes = method.getParameterTypes();//方法参数类型
        Class<?> returnType = method.getReturnType();//方法返回类型
        Date start = new Date();
        long startL = System.nanoTime();
        String prefix = ">>>>>";
        String endfix = "<<<<<";
        String[] paramNames = CommonUtil.getMethodParams(targetClass, method.getName());//获取参数名称数组
        if (log.isInfoEnabled()) {
            log.info(prefix + "开始调用" + targetClass.getName() + "方法" + method.getName() + ", 参数：" +
                    CommonUtil.getMethodParam(paramNames, point.getArgs(), paramTypes) + ". 开始时间" +
                    DateUtils.dateToString(start, DateUtils.DATETIME_FORMAT));
        }
        Object resultObj = null;
        try {
            //执行方法校验
            validate(point.getArgs());
            //执行方法
            resultObj = point.proceed();
        } catch (Exception e) {
            String errorMsg = ExceptionUtil.handlerException(e, targetClass, method.getName());
            log.error(errorMsg, e);
            if (StringUtils.equals("AppResult", returnType.getSimpleName())) {
                AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), errorMsg, "");
                resultObj = appResult;
            } else if (StringUtils.equals("JSONObject", returnType.getSimpleName())) {
                JSONObject appResult = new JSONObject();
                appResult.put("appcode", ResultEnum.FAILURE.getCode());
                appResult.put("databuffer", errorMsg);
                appResult.put("result", "");
                resultObj = appResult;
            }
        }
        Date end = new Date();
        long endL = System.nanoTime();
        if (log.isInfoEnabled()) {
            log.info(endfix + "结束调用" + targetClass.getName() + "方法" + method.getName() + ". 结束时间" + DateUtils.dateToString(end, DateUtils.DATETIME_FORMAT) + ", 耗时" + DateUtils.getMilliSecondBetween(startL, endL) + "毫秒");
            log.info(endfix + "返回结果：" + BeanToMapUtil.convertBeanToMap(resultObj));
        }
        return resultObj;
    }

    /**
     * 参数校验
     *
     * @param arguments
     */
    private void validate(Object[] arguments) {
        for (Object arg : arguments) {
            beanValidator.validate(arg);
        }

    }


}
