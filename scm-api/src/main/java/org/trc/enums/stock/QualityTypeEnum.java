package org.trc.enums.stock;

import org.apache.commons.lang3.StringUtils;


public enum QualityTypeEnum {

	QUALITY("1","正品"),
	DEFECTIVE("2","残品");

	private String code;
	private String name;

	QualityTypeEnum(String code, String name){
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
	public static QualityTypeEnum getQualityTypeEnumByName(String name){
		for(QualityTypeEnum qualityTypeEnum : QualityTypeEnum.values()){
			if(StringUtils.equals(name, qualityTypeEnum.getName())){
				return qualityTypeEnum;
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
	public static QualityTypeEnum getQualityTypeEnumByCode(String code){
		for(QualityTypeEnum qualityTypeEnum : QualityTypeEnum.values()){
			if(StringUtils.equals(qualityTypeEnum.getCode(), code)){
				return qualityTypeEnum;
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
