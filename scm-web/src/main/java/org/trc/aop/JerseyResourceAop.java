package org.trc.aop;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
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
import org.trc.domain.recordTime.MethodInfo;
import org.trc.domain.recordTime.MethodLongTime;
import org.trc.domain.util.CommonDO;
import org.trc.enums.ResultEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.service.recordTime.IMethodInfoService;
import org.trc.service.recordTime.IMethodLongTimeService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hzwdx on 2017/4/22.
 */
@Component
@Aspect
public class JerseyResourceAop {

    private Logger log = LoggerFactory.getLogger(JerseyResourceAop.class);
    //jersey保存操作方法前缀
    public static final String SAVE_METHOD_PREFIX = "save";

    @Autowired
    private IMethodInfoService methodInfoService;

    @Autowired
    private IMethodLongTimeService methodLongTimeService;

    @Autowired
    private BeanValidator beanValidator;

    @Pointcut(value = "execution(* org.trc.resource.*.*(..))")
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
            if (method.getName().startsWith(SAVE_METHOD_PREFIX)) {
                //获取ContainerRequestContext
                ContainerRequestContext requestContext = hasContainerRequestContext(point.getArgs());
                if (null != requestContext) {
                    //设置创建人
                    setOperater(requestContext, point.getArgs());
                }
            }
            //执行方法
            resultObj = point.proceed();
        } catch (NullPointerException e){
            String errorMsg = ExceptionUtil.handlerException(e, targetClass, method.getName());
            log.error(errorMsg, e);
            AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), "系统繁忙!", "");
            resultObj = Response.status(Response.Status.BAD_REQUEST).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build();
        }catch (Exception e) {
            String errorMsg = ExceptionUtil.handlerException(e, targetClass, method.getName());
            log.error(errorMsg, e);
            AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), e.getMessage(), "");
            resultObj = Response.status(Response.Status.BAD_REQUEST).entity(appResult).type(MediaType.APPLICATION_JSON).encoding("UTF-8").build();
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
     * 拦截统计接口的执行频率和效率
     * @param point 执行的目标方法
     * @return 返回切面处理的结果值
     * @throws Throwable 抛出下层切面抛出的异常
     */
    @Around("jerseyService()")
    public Object recordTime(ProceedingJoinPoint point)  throws Throwable {
        MethodInfo methodInfo = new MethodInfo();
        Class<?> targetClass = point.getTarget().getClass();
        methodInfo.setClassName(targetClass.getSimpleName());//设置类名
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();//被执行方法
        methodInfo.setMethodName(method.getName());//设置方法名
        Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes != null && parameterTypes.length > 0){
            StringBuilder sb = new StringBuilder();
            for (Class<?> cls:parameterTypes) {
                sb.append(cls.getName()+ SupplyConstants.Symbol.COMMA);
            }
            methodInfo.setArgs(sb.toString().substring(0,sb.length()-1));//设置参数
        }else {
            //如果无参数，联合索引就会失效，所以这里要赋予“0”作为标记;
            //并且使联合索引生效
            methodInfo.setArgs(ZeroToNineEnum.ZERO.getCode());
        }
        //类名+方法名+设置参数  = 唯一性密等  联合唯一性索引
        Long startL = System.nanoTime();
        Object resultObj = point.proceed();
        Long endL = System.nanoTime();
        Long useTime = DateUtils.getMilliSecondBetween(startL, endL);//毫秒
        /*
        这层切面的处理逻辑不能，不管能不能处理成功，都不能阻止正常的流程。
         */
        handleMethodTime(methodInfo,useTime);
        return resultObj;
    }

    //时间处理 :{这里为了}
    private void handleMethodTime(MethodInfo methodInfo,Long useTime){
       Boolean flag = true; //这里做开关,防止方法信息执行异常的时候,执行时长的信心数据库操作失败
        MethodInfo info = methodInfoService.selectOne(methodInfo);
        if(info == null){ //数据初始化
            methodInfo.setAverageTime(useTime); //平均耗时
            methodInfo.setCreateTime(Calendar.getInstance().getTime());
            methodInfo.setFrequency(1L);//(次/天) 调用频率
            methodInfo.setUseNumber(1L); //使用次数
            methodInfo.setTotalTime(useTime);
            try{
                methodInfoService.insert(methodInfo);
            }catch (Exception e){
                flag = false;
                log.error("接口信息统计异常",e);
                //这里捕捉唯一索引抛出的异常
                //正常逻辑，这里还需要查询更新一下记录方法
            }
        }else{
            //这里根据查询出来调用次数，作为本次更新的条件，如果条件不满足。说明，此期间有人对该条记录进行过更新
            //不应该覆盖其它操作者的数据  --尽量减少因为 执行效率的统计耗时
            MethodInfo udpateInfo = new MethodInfo();
            //udpateInfo.setId(info.getId());
            udpateInfo.setTotalTime(info.getTotalTime()+useTime);
            udpateInfo.setUseNumber(info.getUseNumber()+1L);
            Long averageTime = (info.getTotalTime()+useTime)/(info.getUseNumber()+1L);
            udpateInfo.setAverageTime(averageTime);
            Date createTime = info.getCreateTime();
            Long day = (new Date().getTime()-createTime.getTime())/(86400000L)+1L;
            Long frequency = (info.getUseNumber()+1L)/(day);
            udpateInfo.setFrequency(frequency);
            Example example = new Example(MethodInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id",info.getId());
            criteria.andEqualTo("useNumber",info.getUseNumber());
            int number = methodInfoService.updateByExampleSelective(udpateInfo,example);
        }
        //final:单次记录这次接口调用的总时长；汇总里面存在误差（并发场景下的，忽视录入）
        // 如果想要人工计算，使用该表可以得到精确的信息
        MethodLongTime methodLongTime = new MethodLongTime();
        methodLongTime.setMethodId(info != null ? info.getId():methodInfo.getId());
        methodLongTime.setCreate_time(Calendar.getInstance().getTime());
        methodLongTime.setDuration(useTime);
        if(flag){
            methodLongTimeService.insert(methodLongTime);
        }

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
     *
     * @param parameterValues
     * @return
     */
    private ContainerRequestContext hasContainerRequestContext(Object[] parameterValues) {
        ContainerRequestContext requestContext = null;
        for (Object obj : parameterValues) {
            if (obj instanceof ContainerRequestContext) {
                requestContext = (ContainerRequestContext) obj;
            }
        }
        return requestContext;
    }

    /**
     * 设置当前操作人
     *
     * @param requestContext
     * @param parameterValues
     */
    private void setOperater(ContainerRequestContext requestContext, Object[] parameterValues) {
        Object _obj = requestContext.getProperty(SupplyConstants.Authorization.USER_ID);
        AssertUtil.notNull(_obj, "AOP获取登录用户ID为空");
        String userId = _obj.toString();
        for (Object obj : parameterValues) {
            if (obj instanceof CommonDO) {
                ((CommonDO) obj).setCreateOperator(userId);
            }
        }
    }


    /**
     * 对QueryModel类型参数对象的字符串字段进行空格截取
     *
     * @param args
     */
    private void trimQueryModelStrField(Object[] args) throws IllegalAccessException {
        for (Object paramObj : args) {
            if (null != paramObj) {
                Class<?> superCls = paramObj.getClass().getSuperclass();
                if (StringUtils.equals(QueryModel.class.getSimpleName(), superCls.getSimpleName())) {
                    try {
                        Class _cls = (Class) paramObj.getClass();
                        Field[] fs = _cls.getDeclaredFields();
                        for (int i = 0; i < fs.length; i++) {
                            Field f = fs[i];
                            f.setAccessible(true); //设置些属性是可以访问的
                            if (StringUtils.equals(f.getType().getSimpleName(), String.class.getSimpleName())) {
                                if (f.get(paramObj) != null) {
                                    String val = f.get(paramObj).toString();//得到此属性的值
                                    if (StringUtils.isNotBlank(val)) {
                                        f.set(paramObj, val.trim());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("设置查询对象的字符串类型值异常", e);
                    }

                }
            }
        }
    }


}
