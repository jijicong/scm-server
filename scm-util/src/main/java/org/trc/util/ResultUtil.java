/**   
* @Title: ResultUtil.java 
* @Package com.hoo.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 吴东雄
* @date 2015年11月19日 下午2:17:13 
* Copyright (c) 2015, 杭州海适云承科技有限公司 All Rights Reserved.
* @version V1.0   
*/
package org.trc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ResultEnum;
import org.trc.exception.ParamValidException;

import java.util.HashMap;
import java.util.Map;

/** 
 * @ClassName: ResultUtil 
 * @Description: TODO
 * @author 吴东雄
 * @date 2015年11月19日 下午2:17:13 
 *  
 */
public class ResultUtil {

	/**
	 * 判断字符串是否是{"appcode": "0", "databuffer": "","result":""}格式
	* @Title: isAppcodeFormate 
	* @Description: TODO
	* @param @param jsonStr
	* @param @return    
	* @return boolean
	* @throws
	 */
	public static boolean isAppResultFormate(String result){
		try {
			JSONObject jsonObject = JSON.parseObject(result);
            return jsonObject.containsKey("appcode");
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 
	* @Title: getAppResult 
	* @Description: 根据字符串获取AppResult
	* @param @param result
	* @param @return    
	* @return AppResult
	* @throws
	 */
	public static AppResult getAppResult(String result){
		AppResult appResult = null;
		if(isAppResultFormate(result)){
			JSONObject jsonObject = JSON.parseObject(result);
			appResult = new AppResult(jsonObject.getString(AppResult.APPCODE),
					jsonObject.getString(AppResult.DATABUFFER),
					jsonObject.getString(AppResult.RESULT));
		}else{
			throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "获取AppResult的参数不是AppResult格式");
		}
		return appResult;
	}
	
	
	/**
	 * 判断结果字符串是否是true/false
	* @Title: isAppcodeFormate 
	* @Description: TODO
	* @param @param jsonStr
	* @param @return    
	* @return boolean
	* @throws
	 */
	public static boolean isTrueFormate(String result){
		try {
			Boolean.parseBoolean(result);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 获取线程执行结果
	* @Title: getThreadResult 
	* @Description: TODO
	* @param @param id
	* @param @param result
	* @param @return    
	* @return String
	* @throws
	 */
	public static String threadResult(String id, String result){
		JSONObject resultJson = new JSONObject();
		resultJson.put("id", id);
		resultJson.put("result", result);
		return resultJson.toString();
	}

	/***
	 * 创建成功AppResult 对象
	 * @param databuffer 返回结果信息
	 * @param result 返回结果
	 * @return
	 */
	public static AppResult createSucssAppResult(String databuffer, Object result){
		AppResult appResult = new AppResult(ResultEnum.SUCCESS.getCode(), databuffer, result);
		return appResult;
	}
	
	/**
	 * 
	* @Title: createFailAppResult 
	* @Description: 创建失败AppResult 对象
	* @param @return    
	* @return AppResult
	* @throws
	 */
	public static AppResult createFailAppResult(String databuffer){
		AppResult appResult = new AppResult(ResultEnum.FAILURE.getCode(), databuffer, "");
		return appResult;
	}
	
	/**
	* @Title: responseJsonResult 
	* @Description: 返回参数格式化
	* @param @param state
	* @param @param databuffer
	* @param @param result
	* @param @return    设定文件 
	* @return JSONObject    返回类型 
	* @throws
	 */
	public static JSONObject responseJsonResult(String appcode, String databuffer, String result){
		Map<String,String> map = new HashMap<String,String>();
		map.put("appcode", appcode);
		map.put("databuffer", databuffer);
		map.put("result", result);
		return JSON.parseObject(result);
	}
}
