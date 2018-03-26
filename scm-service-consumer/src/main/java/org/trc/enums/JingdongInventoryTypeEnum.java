package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 京东库存类型枚举
 */
public enum JingdongInventoryTypeEnum {

	SALE("1","可销售"),
	WAIT_RETURN("2","待退品"),
	SELLER_RESERVE("3","商家预留"),
	WAREHOUSE_LOCK("4","仓库锁定"),
	ADVENT_LOCK("5","临期锁定"),
	INVENTORY_LOCK("6","盘点锁定"),
	ISOLAT("7","隔离库存");

	private String code;
	private String name;

	JingdongInventoryTypeEnum(String code, String name){
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
	public static JingdongInventoryTypeEnum getValidEnumByName(String name){
		for(JingdongInventoryTypeEnum validEnum : JingdongInventoryTypeEnum.values()){
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
	public static JingdongInventoryTypeEnum getValidEnumByCode(String code){
		for(JingdongInventoryTypeEnum validEnum : JingdongInventoryTypeEnum.values()){
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
		for(JingdongInventoryTypeEnum sexEnum : JingdongInventoryTypeEnum.values()){
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
