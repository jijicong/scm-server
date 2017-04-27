/**   
* @Title: ValidateUtil.java 
* @Package com.hoo.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 吴东雄
* @date 2015年11月19日 上午9:30:44 
* Copyright (c) 2015, 杭州海适云承科技有限公司 All Rights Reserved.
* @version V1.0   
*/
package org.trc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ParamValidException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** 
 * @ClassName: ValidateUtil 
 * @Description: TODO
 * @author 吴东雄
 * @date 2015年11月19日 上午9:30:44 
 *  
 */
public class ValidateUtil {

	/**
	 * 
	* @Title: paramCheck 
	* @Description: 检查参数中是否包含paramKeys中的参数，并且校验参数值不为空
	* @param @param request 
	* @param @param validParams 校验参数，格式：“参数名:参数说明”, 如多个参数校验 “name:姓名”,“age:年龄”,
	* @return void
	* @throws
	 */
	public static void requestParamNullCheck(HttpServletRequest request,  String... validParams){
		Map<String, Object> params = CommonUtil.getRequestParam(request);// 获取所有参数
		for (String key : validParams) {
			if(!StringUtils.contains(key, ":"))
			throw new ParamValidException(ExceptionEnum.PARAM_CHECK_EXCEPTION, "校验参数"+key+"不是\"参数名:参数说明\"格式");
			String[] keyVal = key.split(":");
			if(!params.containsKey(keyVal[0])){
				throw new ParamValidException(ExceptionEnum.PARAM_CHECK_EXCEPTION, "参数"+keyVal[1]+":"+keyVal[0]+"必须传入");
			}else{
				Object val = params.get(keyVal[0]);
				if(null == val || StringUtils.equals("", String.valueOf(val)))
				throw new ParamValidException(ExceptionEnum.PARAM_CHECK_EXCEPTION, keyVal[1]+":"+keyVal[0]+"参数值不能为空");
			}
		}
	}
	
	/***
	 * 检查json参数中是否包含paramKeys中的参数，并且校验参数值不为空
	 * @param param json字符串/对象
	 * @param validParams 校验参数，格式：“参数名:参数说明”, 如多个参数校验 “name:姓名”,“age:年龄”,
	 */
	public static void jsonParamNullCheck(Object param,  String... validParams){
		JSONObject jsonObject = null;
		if(param instanceof JSONObject)
			jsonObject = (JSONObject)param;
		if(param instanceof String)
			jsonObject = JSON.parseObject(param.toString());
		for (String key : validParams) {
			if(!StringUtils.contains(key, ":"))
			throw new ParamValidException(ExceptionEnum.PARAM_CHECK_EXCEPTION, "校验参数"+key+"不是\"参数名:参数说明\"格式");
			String[] keyVal = key.split(":");
			if(!jsonObject.containsKey(keyVal[0])){
				throw new ParamValidException(ExceptionEnum.PARAM_CHECK_EXCEPTION, "参数"+keyVal[1]+keyVal[0]+"必须传入");
			}else{
				String val = jsonObject.getString(keyVal[0]);
				if(null == val || StringUtils.isEmpty(val))
				throw new ParamValidException(ExceptionEnum.PARAM_CHECK_EXCEPTION, keyVal[1]+keyVal[0]+"参数值不能为空");
			}
		}
	}
	
	
}
