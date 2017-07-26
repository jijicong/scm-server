package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 
* @ClassName: ClearanceEnum
* @Description: 采购订单的审核状态
* @author A18ccms a18ccms_gmail_com 
* @date 2017年4月6日 上午9:16:13 
*
 */
public enum PurchaseOrderAuditEnum {

	/**
	 * :0-暂存,1-提交审核,2-审核通过,3-审核驳回'
	 */
	//HOLD("0","暂存"),  //待审核
	//AUDIT("1","提交审核"),
	//PASS("2","审核通过"), //已审核
	//REJECT("3","审核驳回");//已审核
	referendum("1","待审核"),
	audited("2","已审核");

	private String code;
	private String name;

	PurchaseOrderAuditEnum(String code, String name){
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
	public static PurchaseOrderAuditEnum getClearanceEnumByName(String name){
		for(PurchaseOrderAuditEnum validEnum : PurchaseOrderAuditEnum.values()){
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
	public static PurchaseOrderAuditEnum getClearanceEnumByCode(String code){
		for(PurchaseOrderAuditEnum validEnum : PurchaseOrderAuditEnum.values()){
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
		for(PurchaseOrderAuditEnum sexEnum : PurchaseOrderAuditEnum.values()){
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
