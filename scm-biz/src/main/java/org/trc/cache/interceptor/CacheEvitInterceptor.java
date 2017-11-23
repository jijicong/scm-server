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
import org.trc.cache.CacheEvit;
import org.trc.util.RedisUtil;

import javax.ws.rs.container.ContainerRequestContext;
import java.lang.reflect.Method;

@Component
@Aspect
public class CacheEvitInterceptor {

	private Logger LOGGER = LoggerFactory.getLogger(CacheEvitInterceptor.class);

	private static final String SCM_PRE = "scm";

	/**
	 * 定义缓存逻辑
	 * @throws Throwable 
	 */
	@Around("@annotation(org.trc.cache.CacheEvit)")
	public Object cache(ProceedingJoinPoint pjp) throws Throwable {
		Object result = null;
		// 开关
		// try {
		// result= pjp.proceed();
		// } catch (Throwable e) {
		// e.printStackTrace();
		// }
		// return result;
		try {
			//
			Method method = getMethod(pjp);
			CacheEvit cacheevit = method.getAnnotation(org.trc.cache.CacheEvit.class);
			String className = pjp.getTarget().getClass().getName();

			// 列表
			RedisUtil.delObject(className + "LIST");
			String[] keys = cacheevit.key();
			for (String key : keys) { //删除根据set（key，value）中的值
				String parseKey =  parseKey(key, method, pjp.getArgs());
				key = className + parseKey;
				// 本体
				RedisUtil.delObject(key);
			}

		} catch (Throwable e) {
			//出exception了,继续即可,不需要处理
			LOGGER.error("内存删除失败!",e);
		}finally{
			result = pjp.proceed();
		}
		
		return result;
	}

	/**
	 * 获取被拦截方法对象
	 * 
	 * MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象 而缓存的注解在实现类的方法上
	 * 所以应该使用反射获取当前对象的方法对象
	 */
	public Method getMethod(ProceedingJoinPoint pjp) {
		// 获取参数的类型
		Object[] args = pjp.getArgs();
		Class[] argTypes = new Class[pjp.getArgs().length];
		for (int i = 0; i < args.length; i++) {
			if("ContainerRequest".equals(args[i].getClass().getSimpleName())){
				argTypes[i] = ContainerRequestContext.class;
			}else {
				argTypes[i] = args[i].getClass();
			}
		}

		Method method = null;
		try {
			String name =  pjp.getSignature().getName();
			method = pjp.getTarget().getClass().getMethod(name, argTypes);
		} catch (NoSuchMethodException e) {
			LOGGER.error("获取方法失败!",e);
		} catch (SecurityException e) {
			LOGGER.error("未获得得到该方法的权限!",e);
		}
		return method;
	}

	/**
	 * 获取缓存的key key 定义在注解上，支持SPEL表达式
	 * 
	 * @param key
	 *            String
	 * @param method
	 *            Method
	 * @param args
	 *            Object []
	 * @return
	 */
	private String parseKey(String key, Method method, Object[] args) {

		// 获取被拦截方法参数名列表(使用Spring支持类库)
		LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
		String[] paraNameArr = u.getParameterNames(method);

		// 使用SPEL进行key的解析
		ExpressionParser parser = new SpelExpressionParser();
		// SPEL上下文
		StandardEvaluationContext context = new StandardEvaluationContext();
		// 把方法参数放入SPEL上下文中
		context.setVariable(SCM_PRE,SCM_PRE);
		for (int i = 0; i < paraNameArr.length; i++) {
			context.setVariable(paraNameArr[i], args[i]);
		}
		return parser.parseExpression(StringUtils.isBlank(key) == true ? "#scm" : "#scm+"+key).getValue(context, String.class);
	}
}
//	Signature sig = pjp.getSignature();
//			MethodSignature msig = null;
//			if (!(sig instanceof MethodSignature)) {
//				throw new IllegalArgumentException("该注解只能用于方法");
//			}
//			msig = (MethodSignature) sig;
//			Object target = pjp.getTarget();
//			Method method = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());