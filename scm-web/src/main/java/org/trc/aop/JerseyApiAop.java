package org.trc.aop;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.util.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by hzwdx on 2017/4/22.
 */
//@Component
//@Aspect
public class JerseyApiAop {

    private Logger  log = LoggerFactory.getLogger(JerseyApiAop.class);
    // jersey保存操作方法前缀
    public static final String SAVE_METHOD_PREFIX = "save";


    @Autowired
    private BeanValidator beanValidator;

    //@Pointcut(value = "execution(* org.trc.resource.api.*.*(..))")
    public void jerseyApi() {
    }

   /* @Around("jerseyApi()")
    public Object recordTime(ProceedingJoinPoint point) throws Throwable {
        *//**
         * 统计调用时间 和 次数  ，调用频率
         *//*
        Object resultObj = point.proceed();
        return resultObj;
    }*/

    //@Around("jerseyApi()")
    public Object invoke(ProceedingJoinPoint point) throws Throwable {
        Class<?> targetClass = point.getTarget().getClass();//被代理的类
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();//被执行方法
        Class<?>[] paramTypes = method.getParameterTypes();//方法参数类型
        Class<?> returnType = method.getReturnType();//方法返回类型

        //对QueryModel类型参数对象的字符串字段进行空格截取
        trimQueryModelStrField(point.getArgs());

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
            //环绕模式执行方法
            resultObj = point.proceed();
        } catch (Exception e) {
            String errorMsg = ExceptionUtil.handlerException(e, targetClass, method.getName());
            log.error(errorMsg, e);
            if(StringUtils.equals(ResponseAck.class.getSimpleName(), returnType.getSimpleName())){
                String code = ExceptionUtil.getErrorInfo(e);
                resultObj = new ResponseAck(code, e.getMessage(), "");
            }
        }
        Date end = new Date();
        long endL = System.nanoTime();
        if (log.isInfoEnabled()) {
            log.info(endfix + "结束调用" + targetClass.getName() + "方法" + method.getName() + ". 结束时间" + DateUtils.dateToString(end, DateUtils.DATETIME_FORMAT) + ", 耗时" + DateUtils.getMilliSecondBetween(startL, endL) + "毫秒");
            log.info(endfix + "返回结果：" + JSON.toJSONString(resultObj));
        }
        return resultObj;
    }

    /**
     * 对QueryModel类型参数对象的字符串字段进行空格截取
     * @param args
     */
    private void trimQueryModelStrField(Object[] args) throws IllegalAccessException {
        for(Object paramObj: args){
            if(null != paramObj){
                Class<?> superCls = paramObj.getClass().getSuperclass();
                if(StringUtils.equals(QueryModel.class.getSimpleName(), superCls.getSimpleName())){
                    try{
                        Class _cls = (Class) paramObj.getClass();
                        Field[] fs = _cls.getDeclaredFields();
                        for(int i = 0 ; i < fs.length; i++){
                            Field f = fs[i];
                            f.setAccessible(true); //设置些属性是可以访问的
                            if (StringUtils.equals(f.getType().getSimpleName(), String.class.getSimpleName())) {
                                if(f.get(paramObj) != null){
                                    String val = f.get(paramObj).toString();//得到此属性的值
                                    if(StringUtils.isNotBlank(val)){
                                        f.set(paramObj,val.trim());
                                    }
                                }
                            }
                        }
                    }catch (Exception e){
                        log.error("设置查询对象的字符串类型值异常", e);
                    }

                }
            }
        }
    }





}
