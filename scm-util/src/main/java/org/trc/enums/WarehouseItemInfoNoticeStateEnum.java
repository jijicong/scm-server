package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 
* @ClassName: WarehouseNoticeEnum
* @Description: 入库通知枚举--采购单中的
* @author A18ccms a18ccms_gmail_com 
* @date 2017年6月2日
*
 */
public enum WarehouseItemInfoNoticeStateEnum {

	TO_BE_NOTIFIED("0","待通知"),
	NOTICE_FAILURE("1","通知失败"),
	NOTICE_CANCEL("2","取消通知"),
	NOTICE_("3","通知中"),
	NOTICE_SUCCESS("4","通知成功");



	private String code;
	private String name;

	WarehouseItemInfoNoticeStateEnum(String code, String name){
		this.code = code;
		this.name = name;
	}
	
	/**
	* @Title: getValidEnumByName 
	* @Description: 根据枚举名称获取枚举
	* @param @param name
	* @param @return    
	* @return ValidEnum
	* @throws
	 */
	public static WarehouseItemInfoNoticeStateEnum getValidEnumByName(String name){
		for(WarehouseItemInfoNoticeStateEnum validEnum : WarehouseItemInfoNoticeStateEnum.values()){
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
	public static WarehouseItemInfoNoticeStateEnum getValidEnumByCode(String code){
		for(WarehouseItemInfoNoticeStateEnum validEnum : WarehouseItemInfoNoticeStateEnum.values()){
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
		for(WarehouseItemInfoNoticeStateEnum sexEnum : WarehouseItemInfoNoticeStateEnum.values()){
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
