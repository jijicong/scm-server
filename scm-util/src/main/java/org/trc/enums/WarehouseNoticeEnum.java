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
public enum WarehouseNoticeEnum {

	TO_BE_NOTIFIED("0","待通知"),
	HAVE_NOTIFIED("1","已通知"),
	WAREHOUSE_RECEIVE_FAILED("8","仓库接收失败"),
	ON_WAREHOUSE_TICKLING("2","仓库接收成功"),
	ALL_GOODS("3","全部入库"),
	RECEIVE_GOODS_EXCEPTION("4","入库异常"),
	RECEIVE_PARTIAL_GOODS("5","部分入库"),
	DROPPED("6","作废"),
	CANCELLATION("7","已取消");
	
	private String code;
	private String name;

	WarehouseNoticeEnum(String code, String name){
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
	public static WarehouseNoticeEnum getValidEnumByName(String name){
		for(WarehouseNoticeEnum validEnum : WarehouseNoticeEnum.values()){
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
	public static WarehouseNoticeEnum getValidEnumByCode(String code){
		for(WarehouseNoticeEnum validEnum : WarehouseNoticeEnum.values()){
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
		for(WarehouseNoticeEnum sexEnum : WarehouseNoticeEnum.values()){
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
