package org.trc.cache.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.trc.biz.impl.purchase.PurchaseOrderBiz;
import org.trc.cache.Cacheable;
import org.trc.util.RedisUtil;

import java.lang.reflect.Method;

@Component
@Aspect
public class CacheableInterceptor {

    private Logger LOGGER = LoggerFactory.getLogger(CacheableInterceptor.class);

    private static final String SCM_PRE = "scm";//1.标志作用 2.前面加scm 防止后面的参数，因为是数字相加，导致key 不唯一

    /**          
    * 定义缓存逻辑                    
     * @throws Throwable 
    */
    @Around("@annotation(org.trc.cache.Cacheable)")
    public Object cache(ProceedingJoinPoint pjp ) throws Throwable {
        Object result=null;
        
//        //判断是否开启缓存
//        try {
//            result= pjp.proceed();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        return result;
        
        String key = "";
        String listKey = "";
        int expireTime = 3600;
        boolean isList = false;
        //是否应该将结果放入缓存
        boolean shouldSet = false;
        try{
            Method method = getMethod(pjp);
            Cacheable cacheable=method.getAnnotation(Cacheable.class);
            //是否是列表
            isList = cacheable.isList();
            expireTime = cacheable.expireTime();
            String cls = cacheable.cls();
            String className = pjp.getTarget().getClass().getName();
            //取对应的缓存结果
            if(isList){
                if (StringUtils.isNotBlank(cls)){
                    key = cls + "LIST";
                }else {
                    key = className + "LIST";
                }
                //Spring_el 利用反射获取方法上的参数，进而获取表达式中生成key的条件
                listKey = method.toString() + parseKey(cacheable.key(),method,pjp.getArgs());
                result= RedisUtil.hget(key,listKey);
            }else{
                String parseKey = parseKey(cacheable.key(),method,pjp.getArgs());
                //类名,方法名,key值保证 key的唯一
                key = className + parseKey;
                result = RedisUtil.getObject(key);
            }
            //到达这一步证明参数正确，没有exception，应该放入缓存
            shouldSet = true;
        }catch(Exception e){
            //出exception了,继续即可,不需要处理
            LOGGER.error("缓存查询处理失败!",e);
        }finally{
        		//没有缓存执行结果
	        	if(result==null){
	        		if(isList){
	        			result=pjp.proceed();
	        			if(shouldSet){
	        				RedisUtil.hset(key,listKey, result,expireTime);
	        			}
		        	}else{
		        		result = pjp.proceed();
		        		if(shouldSet){
		        			Assert.notNull(key,"生成缓存的时,key值为空,缓存存储异常");
		        			RedisUtil.setObject(key, result,expireTime);
		        		}
		        	}
			}
        }
        return result;
    }

    
    /**
     *  获取被拦截方法对象
     *  
     *  MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象
     *    而缓存的注解在实现类的方法上
     *  所以应该使用反射获取当前对象的方法对象
     */
    public Method getMethod(ProceedingJoinPoint pjp){
        //获取参数的类型
        Object [] args=pjp.getArgs();

        Class [] argTypes=new Class[pjp.getArgs().length];
        for(int i=0;i<args.length;i++){
            argTypes[i]=args[i].getClass();
        }
        Method method=null;
        try {
            method=pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(),argTypes);
        } catch (NoSuchMethodException e) {
            LOGGER.error("获取方法失败!",e);
        } catch (SecurityException e) {
            LOGGER.error("未获得得到该方法的权限!",e);
        }
        return method;
        
    }
    /**
     *    获取缓存的key 
     *    key 定义在注解上，支持SPEL表达式
     * @param key String
     * @param method Method
     * @param args Object []
     * @return
     */
    private String parseKey(String key, Method method, Object [] args){
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u =   
            new LocalVariableTableParameterNameDiscoverer();  
        String [] paraNameArr=u.getParameterNames(method);
        
        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser(); 
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        context.setVariable(SCM_PRE,SCM_PRE);
        for(int i=0;i<paraNameArr.length;i++){
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression("#scm+" + key).getValue(context,String.class);
    }

}