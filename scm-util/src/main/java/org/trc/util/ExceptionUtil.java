package org.trc.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;

/** 
 * @ClassName: ExceptionUtil 
 * @Description:
 * @author 吴东雄
 * @date 2016年1月16日 上午12:53:42 
 *  
 */
public class ExceptionUtil {
	private static Log log = LogFactory.getLog(ExceptionUtil.class); 	
	/**
	 *
	* @Title: handlerException 
	* @Description: 异常结果处理
	* @param @param appResult 返回结果实例
	* @param @param e 异常对象
	* @param @param invokingMethodName 当前调用方法
	* @return void
	* @throws
	 */
	public static String handlerException(Exception e, Class<?> targetClass, String invokingMethodName){
		StringBuilder builder = new StringBuilder();
		ExceptionEnum exceptionEnum = null;
		String excepMsg = "";
		String errorDtl = "";
		String excepCode = "";
		try {
			String exceptionName = e.getClass().getName();
			exceptionName = exceptionName.substring(
					exceptionName.lastIndexOf(".") + 1, exceptionName.length());
			if (StringUtils.equals(exceptionName, ParamValidException.class.getSimpleName())) {
				ParamValidException paramValidException = (ParamValidException)e;
				exceptionEnum = paramValidException.getExceptionEnum();
			} else if (StringUtils.equals(exceptionName, ConfigException.class.getSimpleName())) {
				ConfigException configException = (ConfigException)e;
				exceptionEnum = configException.getExceptionEnum();
			}
		} catch (Exception e2) {
			excepMsg = "系统未捕获未知异常";
			errorDtl = e2.getMessage();
		}
		if(null != exceptionEnum){
			excepCode = exceptionEnum.getCode();
			excepMsg = exceptionEnum.getMessage();
			errorDtl = e.getMessage();
		}
		builder = new StringBuilder();
		builder.append("外部系统调用");
		builder.append(targetClass.getName());
		builder.append("方法");
		builder.append(invokingMethodName);
		builder.append("发生");
		builder.append(excepMsg);
		builder.append(". 异常代码[").append(excepCode);
		builder.append("],错误明细信息：");
		builder.append(errorDtl);
		log.error(builder.toString());
		StringBuilder builder2 = new StringBuilder();
		builder2.append(excepMsg).append(",异常代码[").append(excepCode).append("],异常明细:").append(errorDtl);
		return builder2.toString();
	}
	
}
