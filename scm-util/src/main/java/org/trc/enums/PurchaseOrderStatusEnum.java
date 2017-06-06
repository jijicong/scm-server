package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 
* @ClassName: PurchaseOrderStatusEnum
* @Description: 采购订单枚举
* @author sone
* @date 2017年6月1日
*
 */
public enum PurchaseOrderStatusEnum {
   //0-暂存,1-提交审核,2-审核通过,3-审核驳回,4-全部收货,5-收货异常,6-冻结,7-作废
	HOLD("0","暂存"),
	AUDIT("3","提交审核"),
	PASS("2","审核通过"),
	REJECT("1","审核驳回"),
	RECEIVE_ALL("4","全部收货"),
	RECEIVE_EXCEPTION("5","收货异常"),
	FREEZE("6","冻结"),
	CANCEL("7","作废"),
	WAREHOUSE_NOTICE("8","入库通知");

	private String code;
	private String name;

	PurchaseOrderStatusEnum(String code, String name){
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
	public static PurchaseOrderStatusEnum getValidEnumByName(String name){
		for(PurchaseOrderStatusEnum validEnum : PurchaseOrderStatusEnum.values()){
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
	public static PurchaseOrderStatusEnum getValidEnumByCode(String code){
		for(PurchaseOrderStatusEnum validEnum : PurchaseOrderStatusEnum.values()){
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
		for(PurchaseOrderStatusEnum sexEnum : PurchaseOrderStatusEnum.values()){
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
