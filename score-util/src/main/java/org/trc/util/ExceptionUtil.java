/**   
* @Title: ExceptionUtil.java 
* @Package com.hoo.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 吴东雄
* @date 2016年1月16日 上午12:53:42 
* Copyright (c) 2016, 杭州海适云承科技有限公司 All Rights Reserved.
* @version V1.0   
*/
package org.trc.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * @ClassName: ExceptionUtil 
 * @Description: TODO
 * @author 吴东雄
 * @date 2016年1月16日 上午12:53:42 
 *  
 */
public class ExceptionUtil {
	private static Log log = LogFactory.getLog(ExceptionUtil.class); 	
	/**
	 * @throws com.sun.star.uno.Exception 
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
		String excepMsg = "";
		try {
			String exceptionName = e.getClass().getName();
			exceptionName = exceptionName.substring(
					exceptionName.lastIndexOf(".") + 1, exceptionName.length());
			if (StringUtils.equals(exceptionName, "AuthorizException")) {
				excepMsg = "权限校验异常";
			} else if (StringUtils.equals(exceptionName, "BizException")) {
				excepMsg = "业务异常";
			} else if (StringUtils.equals(exceptionName, "DataException")) {
				excepMsg = "数据异常";
			} else if (StringUtils.equals(exceptionName, "HttpException")) {
				excepMsg = "HTTP调用异常";
			} else if (StringUtils.equals(exceptionName, "ParamValidException")) {
				excepMsg = "参数校验异常";
			} else if (StringUtils.equals(exceptionName, "WebserviceException")) {
				excepMsg = "Webservice调用异常";
			} else if (StringUtils.equals(exceptionName, "ThreadException")) {
				excepMsg = "多线程执行异常";
			} else if (StringUtils.equals(exceptionName, "Exception")) {
				excepMsg = "系统未捕获未知异常";
			}
		} catch (Exception e2) {
			excepMsg = "系统未捕获未知异常";
		}
		builder = new StringBuilder();
		builder.append("调用");
		builder.append(targetClass.getName());
		builder.append("方法");
		builder.append(invokingMethodName);
		builder.append("发生");
		builder.append(excepMsg);
		builder.append(",异常信息：");
		builder.append(e.getMessage());
		return builder.toString();
	}
	
}
