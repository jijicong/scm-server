package org.trc.enums;

import org.apache.commons.lang.StringUtils;

/**
 * 1~9数字枚举
 * @author wdx
 *
 */
public enum ZeroToNineEnum {
	
	ZERO("0","ZERO"),
	ONE("1","ONE"),
	TWO("2","TWO"),
	THREE("3","THREE"),
	FOUR("4","FOUR"),
	FIVE("5","FIVE"),
	SIX("6","SIX"),
	SEVEN("7","SEVEN"),
	EIGHT("8","EIGHT"),
	NINE("9","NINE");
	
	private String code; 
	private String name; 
	
	ZeroToNineEnum(String code, String name){
		this.code = code;
		this.name = name;
	}
	
	/**
	 * 
	* @Title: getOneToNineEnumByName 
	* @Description: 根据枚举名称获取枚举
	* @param @param name
	* @param @return    
	* @return OneToNineEnum
	* @throws
	 */
	public static ZeroToNineEnum getOneToNineEnumByName(String name){
		for(ZeroToNineEnum oneToNineEnum : ZeroToNineEnum.values()){
			if(StringUtils.equals(name, oneToNineEnum.getName())){
				return oneToNineEnum;
			}
		}
		return null;
	}
	
	/**
	 * 
	* @Title: getOneToNineEnumByCode 
	* @Description: 根据枚举编码获取枚举
	* @param @param name
	* @param @return    
	* @return OneToNineEnum
	* @throws
	 */
	public static ZeroToNineEnum getOneToNineEnumByCode(String code){
		for(ZeroToNineEnum oneToNineEnum : ZeroToNineEnum.values()){
			if(StringUtils.equals(oneToNineEnum.getCode(), code)){
				return oneToNineEnum;
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
