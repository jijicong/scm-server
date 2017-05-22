package org.trc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Modifier;
import java.util.*;

/** 
 * @ClassName: CommonUtil 
 * @Description: TODO
 * @author 吴东雄
 * @date 2016年3月16日 下午10:48:14 
 *  
 */
public class CommonUtil {

	private final static Logger log = LoggerFactory.getLogger(CommonUtil.class);

	public static final String HTTP_SERVLET_REQUEST = "HttpServletRequest";
	public static final String MODEL_MAP = "ModelMap";
	
	public static Map<String, Object> getMap(String key, String Object){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, Object);
		return map;
	}


   /**
    * 
   * @Title: requestToJson 
   * @Description: request参数转json
   * @param @param request
   * @param @return    设定文件 
   * @return JSONObject    返回类型 
   * @throws
    */
   public static JSONObject getJsonParams(HttpServletRequest request){
	   JSONObject jsonObject = new JSONObject();
	   Iterator iterator = request.getParameterMap().entrySet().iterator();
	   while (iterator.hasNext()){
		   Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>)iterator.next();
		   jsonObject.put(entry.getKey(), entry.getValue()[0]);
	   }
	   return jsonObject;
   }

	/**
	 * 获取查询参数
	 * @param uriInfo
	 * @return
	 */
   public static JSONObject getQueryParamJson(UriInfo uriInfo){
	   JSONObject json = new JSONObject();
	   MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
	   for(Map.Entry<String, List<String>> entry : queryParams.entrySet()){
		   json.put(entry.getKey(), entry.getValue().get(0));
	   }
	   return json;
   }
    
    /**
     * 
    * @Title: requestToMap 
    * @Description: request参数转Map
    * @param @param request
    * @param @return    设定文件 
    * @return JSONObject    返回类型 
    * @throws
     */
    public static Map<String, Object> requestToMap(HttpServletRequest request){
    	Map<String, Object> map = new HashMap<String, Object>();
		Iterator iterator = request.getParameterMap().entrySet().iterator();
		while (iterator.hasNext()){
			Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>)iterator.next();
			map.put(entry.getKey(), entry.getValue()[0]);
		}
    	return map;
    }
    
    
    /**
     * 拼接字符串
     * @param strs
     * @return
     */
    public static StringBuffer joinStr(String...strs){
    		StringBuffer buffer = new StringBuffer();
    	for(String str : strs){
    			buffer.append(str);
    	}
    	return buffer;
    }
    
	/**
	 *
	* @Title: getRequestParam
	* @Description: 获取request所有请求参数
	* @param @param request
	* @param @return
	* @return Map<String,Object>
	* @throws
	 */
	public static Map<String, Object> getRequestParam(HttpServletRequest request){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if(null == request)
			return paramMap;
		Enumeration<String> params = request.getParameterNames();
		while(params.hasMoreElements()){
			String paramName = String.valueOf(params.nextElement());
			paramMap.put(paramName, request.getParameter(paramName));
		}
		return paramMap;
	}


	/**
	 * 
	* @Title: getMethodParams 
	* @Description: 获取类方法参数
	* @param @param calzz 类对象
	* @param @param methodName 方法名称
	* @param @return
	* @param @throws Exception    
	* @return String[] 参数名称数组
	* @throws
	 */
	public static String[] getMethodParams(Class<?> calzz, String methodName) throws Exception{
		ClassPool pool = ClassPool.getDefault();  
		CtClass cc = null;
		/**
		 * 此处异常处理是在被拦截的类已经被代理的情况下，获取到被代理的类
		 */
		try {
			pool.insertClassPath(new ClassClassPath(calzz));
			cc = pool.get(calzz.getName());  
		} catch (Exception e) {
			pool.insertClassPath(new ClassClassPath(calzz.getSuperclass()));
			cc =pool.get(calzz.getSuperclass().getName());  
		}
        CtMethod cm = cc.getDeclaredMethod(methodName);  
        //使用javaassist的反射方法获取方法的参数名  
        MethodInfo methodInfo = cm.getMethodInfo();  
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
        String[] paramNames = new String[cm.getParameterTypes().length];  
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
        for (int i = 0; i < paramNames.length; i++)
            paramNames[i] = attr.variableName(i + pos);  
		return paramNames;
	}
	
	
	/**
	 * 
	* @Title: getMethodParam 
	* @Description: 获取正在执行方法的参数map
	* @param @param parameterNames 参数名称数组
	* @param @param parameterValues  参数值数组
	* @param @return paramTypes 参数类型
	* @return Map<String,Object> 参数名称-值map对象
	* @throws
	 */
	public static Map<String, Object> getMethodParam(String[] parameterNames, Object[] parameterValues, Class<?>[]  paramTypes){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		for(int i=0; i<parameterNames.length; i++){
			if(StringUtils.equals(paramTypes[i].getSimpleName(), HTTP_SERVLET_REQUEST)){
				HttpServletRequest request = (HttpServletRequest)parameterValues[i];
				paramMap.putAll(getRequestParam(request));
			}else{
				paramMap.put(parameterNames[i], parameterValues[i]);
			}
		}
		return paramMap;
	}
	

	/**
	 * 
	* @Title: getLocalHttpUrl 
	* @Description: 获取本地http服务路径
	* @param @param request
	* @param @param servicePath 服务路径
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	private String getLocalHttpUrl(HttpServletRequest request, String servicePath){
		String url = CommonUtil.joinStr("http://")
				.append(request.getServerName()).append(":")
				.append(request.getServerPort())
				.append(request.getContextPath()).append("/")
				.append(servicePath).toString();
		return url.toString();
	}
	
	/**
	 * 
	* @Title: converCollectionToString 
	* @Description: 将结合列表转换成字符串格式,用逗号","分割
	* @param @param list
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String converCollectionToString(List<?> list){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<list.size(); i++){
			if(i == (list.size()-1))
				sb.append(list.get(i).toString());
			else
				sb.append(list.get(i).toString()).append(",");
		}
		return sb.toString();
	}



	/**
	 *获取数据库操作条件
	 * @return json对象
	 */
	public static JSONObject getDBOperateCondition(Object json){
		JSONObject param = null;
		if(json instanceof String)
			param = JSON.parseObject((String)json);
		else if(json instanceof JSONObject)
			param = (JSONObject)json;
		else
			param = JSON.parseObject(JSON.toJSONString(json));
		Iterator iterator = param.entrySet().iterator();
		while (iterator.hasNext()){
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>)iterator.next();
			if(null == entry.getValue() || (entry.getValue() instanceof String && StringUtils.isNotEmpty((String)entry.getValue()))){
				param.remove(entry.getKey());
			}
		}
		return param;
	}

	/**
	 * json转map
	 * @param json
	 * @return
	 */
	public static Map<String,Object> jsonToMap(JSONObject json){
		Map<String,Object> map = new HashMap<String,Object>();
		Iterator<String> iterator = json.keySet().iterator();
		while (iterator.hasNext()){
			String key = iterator.next();
			map.put(key, json.get(key));
		}
		return map;
	}



}
