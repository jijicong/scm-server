package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * 
* @ClassName: ClearanceEnum
* @Description: 清关的枚举
* @author A18ccms a18ccms_gmail_com 
* @date 2017年4月6日 上午9:16:13 
*
 */
public enum ClearanceEnum {

	VALID("1","是"),
	NOVALID("0","否");

	private String code;
	private String name;

	ClearanceEnum(String code, String name){
		this.code = code;
		this.name = name;
	}
	
	/**
	 * 
	* @Title: getValidEnumByName 
	* @Description: 根据枚举名称获取枚举
	* @param @param name
	* @param @return    
	* @return ValidEnum
	* @throws
	 */
	public static ClearanceEnum getClearanceEnumByName(String name){
		for(ClearanceEnum validEnum : ClearanceEnum.values()){
			if(StringUtils.equals(name, validEnum.getName())){
				return validEnum;
			}
		}
		return null;
	}
	
	/**
	 * 
	* @Title: getValidEnumByCode 
	* @Description: 根据枚举编码获取枚举
	* @param @param name
	* @param @return    
	* @return ValidEnum
	* @throws
	 */
	public static ClearanceEnum getClearanceEnumByCode(String code){
		for(ClearanceEnum validEnum : ClearanceEnum.values()){
			if(StringUtils.equals(validEnum.getCode(), code)){
				return validEnum;
			}
		}
		return null;
	}

	/**
	 *
	 * @Title: toJSONArray
	 * @Description: 转换成json数组
	 * @param @return
	 * @return JSONArray
	 * @throws
	 */
	public static JSONArray toJSONArray(){
		JSONArray array = new JSONArray();
		for(ClearanceEnum sexEnum : ClearanceEnum.values()){
			JSONObject obj = new JSONObject();
			obj.put("code", sexEnum.getCode());
			obj.put("name", sexEnum.getName());
			array.add(obj);
		}
		return array;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
