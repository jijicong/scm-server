/**   
* @Title: ResultEnum.java 
* @Package com.hoo.enums 
* @Description: TODO(用一句话描述该文件做什么) 
* @author 吴东雄
* @date 2016年1月15日 下午3:23:10 
* Copyright (c) 2016, 杭州海适云承科技有限公司 All Rights Reserved.
* @version V1.0   
*/
package org.trc.enums;

import org.apache.commons.lang.StringUtils;

/** 
 * @ClassName: ResultEnum 
 * @Description: TODO
 * @author 吴东雄
 * @date 2016年1月15日 下午3:23:10 
 *  
 */
public enum ResultEnum {

	SUCCESS("0","成功"),
	FAILURE("1","失败");
	
	private String code; 
	private String name; 
	
	ResultEnum(String code, String name){
		this.code = code;
		this.name = name;
	}
	
	/**
	 * 
	* @Title: getResultEnumByName 
	* @Description: 根据枚举名称获取枚举
	* @param @param name
	* @param @return    
	* @return ResultEnum
	* @throws
	 */
	public static ResultEnum getResultEnumByName(String name){
		for(ResultEnum resultEnum : ResultEnum.values()){
			if(StringUtils.equals(name, resultEnum.getName())){
				return resultEnum;
			}
		}
		return null;
	}
	
	/**
	 * 
	* @Title: getResultEnumByCode 
	* @Description: 根据枚举编码获取枚举
	* @param @param name
	* @param @return    
	* @return ResultEnum
	* @throws
	 */
	public static ResultEnum getResultEnumByCode(String code){
		for(ResultEnum resultEnum : ResultEnum.values()){
			if(StringUtils.equals(resultEnum.getCode(), code)){
				return resultEnum;
			}
		}
		return null;
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
