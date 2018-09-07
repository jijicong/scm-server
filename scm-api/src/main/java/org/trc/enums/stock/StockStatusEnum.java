package org.trc.enums.stock;

import org.apache.commons.lang3.StringUtils;


public enum StockStatusEnum {

	IN("I","入库"),
	OUT("O","出库");

	private String code;
	private String name;

	StockStatusEnum(String code, String name){
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
	public static StockStatusEnum getStockStatusEnumByName(String name){
		for(StockStatusEnum stockStatusEnum : StockStatusEnum.values()){
			if(StringUtils.equals(name, stockStatusEnum.getName())){
				return stockStatusEnum;
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
	public static StockStatusEnum getStockStatusEnumByCode(String code){
		for(StockStatusEnum stockStatusEnum : StockStatusEnum.values()){
			if(StringUtils.equals(stockStatusEnum.getCode(), code)){
				return stockStatusEnum;
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
