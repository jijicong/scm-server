package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 
* @ClassName: OutboundOrderStatusEnum
* @Description: 发货通知单状态枚举
* @author sone
*
 */
public enum OutboundOrderStatusEnum {

	/*0.发送中 1.待仓库反馈 2.全部发货 3.部分发货 4. 已取消*/
	SENDING("0","发送中"),
	ON_WAREHOUSE_NOTICE("1","待仓库反馈"),
	ALL_GOODS("2","全部发货"),
	PART_OF_SHIPMENT("3","部分发货"),
	CANCELED("4","已取消");

	private String code;
	private String name;

	OutboundOrderStatusEnum(String code, String name){
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
	public static OutboundOrderStatusEnum getClearanceEnumByName(String name){
		for(OutboundOrderStatusEnum validEnum : OutboundOrderStatusEnum.values()){
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
	public static OutboundOrderStatusEnum getClearanceEnumByCode(String code){
		for(OutboundOrderStatusEnum validEnum : OutboundOrderStatusEnum.values()){
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
		for(OutboundOrderStatusEnum sexEnum : OutboundOrderStatusEnum.values()){
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
