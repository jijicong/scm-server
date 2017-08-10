package org.trc.aop;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trc.constants.SupplyConstants;
import org.trc.domain.util.CommonDO;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ResultEnum;
import org.trc.util.*;

import javax.ws.rs.BeanParam;
import javax.ws.rs.container.ContainerRequestContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by hzwdx on 2017/4/22.
 */
@Component
@Aspect
public class JerseyServiceAop {

    private Logger  log = LoggerFactory.getLogger(JerseyServiceAop.class);
    //jersey保存操作方法前缀
    public static final String SAVE_METHOD_PREFIX = "save";


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
            //执行方法校验
            validate(point.getArgs());
            //新增操作方法拦截
            if(method.getName().startsWith(SAVE_METHOD_PREFIX)){
                //获取ContainerRequestContext
                ContainerRequestContext requestContext = hasContainerRequestContext(point.getArgs());
                if(null != requestContext){
                    //设置创建人
                    setOperater(requestContext,point.getArgs());
                }
            }
            //执行方法
            resultObj = point.proceed();
        } catch (Exception e) {
            String errorMsg = ExceptionUtil.handlerException(e, targetClass, method.getName());
            log.error(errorMsg, e);
            if(StringUtils.equals(ResponseAck.class.getSimpleName(), returnType.getSimpleName())){
                String code = ExceptionUtil.getErrorInfo(e);
                resultObj = new ResponseAck(code, e.getMessage(), "");
            }if (StringUtils.equals(AppResult.class.getSimpleName(), returnType.getSimpleName())) {
                AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), e.getMessage(), "");
                resultObj = appResult;
            } else if (StringUtils.equals("JSONObject", returnType.getSimpleName())) {
                JSONObject appResult = new JSONObject();
                appResult.put("appcode", ResultEnum.FAILURE.getCode());
                appResult.put("databuffer", e.getMessage());
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

    /**
     * 判断参数里面是否包含ContainerRequestContext
     * @param parameterValues
     * @return
     */
    private ContainerRequestContext hasContainerRequestContext(Object[] parameterValues){
        ContainerRequestContext requestContext = null;
        for(Object obj : parameterValues){
            if(obj instanceof  ContainerRequestContext){
                requestContext = (ContainerRequestContext)obj;
            }
        }
        return requestContext;
    }

    /**
     * 设置当前操作人
     * @param requestContext
     * @param parameterValues
     */
    private void setOperater(ContainerRequestContext requestContext, Object[] parameterValues){
        Object _obj = requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AssertUtil.notNull(_obj, "AOP获取登录用户ID为空");
        String userId = _obj.toString();
        for(Object obj : parameterValues){
            if(obj instanceof CommonDO){
                ((CommonDO)obj).setCreateOperator(userId);
            }
        }
    }


    /**
     * 对QueryModel类型参数对象的字符串字段进行空格截取
     * @param args
     */
    private void trimQueryModelStrField(Object[] args) throws IllegalAccessException {
        for(Object paramObj: args){
            if(StringUtils.equals(QueryModel.class.getSimpleName(), paramObj.getClass().getSuperclass().getSimpleName())){
                Class _cls = (Class) paramObj.getClass();
                Field[] fs = _cls.getDeclaredFields();
                for(int i = 0 ; i < fs.length; i++){
                    Field f = fs[i];
                    f.setAccessible(true); //设置些属性是可以访问的
                    String type = f.getType().toString();//得到此属性的类型
                    if (type instanceof String) {
                        String val = (String)f.get(paramObj);//得到此属性的值
                        if(StringUtils.isNotBlank(val)){
                            f.set(paramObj,val.trim());
                        }
                    }
                }
            }
        }
    }





}
