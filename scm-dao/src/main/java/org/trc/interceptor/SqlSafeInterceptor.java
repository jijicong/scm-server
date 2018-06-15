package org.trc.interceptor;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;

@Intercepts({  
    @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})  
public class SqlSafeInterceptor implements Interceptor {
	
	private static final Set<String> COMMAND = new HashSet<String>(){{add("update"); add("delete");}};
	private static final String TARGET_STRING = "where";

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		
		StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
		
		/** 通过MetaObject优雅访问对象的属性，这里是访问statementHandler的属性   **/
		MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, 
				 SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
		
		/** 
		 * 先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，
		 * 其实现类是BaseStatementHandler，
		 * 然后就到BaseStatementHandler的成员变量mappedStatement
		*/	 
		MappedStatement mappedStatement = (MappedStatement)metaObject.getValue("delegate.mappedStatement");
		
		String cmdType = mappedStatement.getSqlCommandType().toString().toLowerCase();
		
		if (COMMAND.contains(cmdType)) {
			String originSql = statementHandler.getBoundSql().getSql().toLowerCase();
			if (!originSql.contains(TARGET_STRING)) {
				throw new RuntimeException("delete，update语句缺少where条件，原始语句为：" + originSql);
			}
		}
		
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		
		return Plugin.wrap(target, this);
		
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub
		
	}

}
