package org.trc.enums.stock;

import org.apache.commons.lang3.StringUtils;

/**
 * 库存变动类型
 * 库存变动类型1.销售订单出库2.调拨出库3.退货出库4.采购入库5.调拨入库
 *
 */
public enum StockTypeEnum {

	SELL_ORDER_OUT("1","销售订单出库"),
	ALLOCATTE_OUT("2","调拨出库"),
	RETURN_OUTBOUND("3", "退货出库"),
	PURCHASE_IN("4","采购入库"),
	ALLOCATTE_IN("5","调拨入库");

	private String code;
	private String name;

	StockTypeEnum(String code, String name){
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
	public static StockTypeEnum getStockTypeEnumByName(String name){
		for(StockTypeEnum stockTypeEnum : StockTypeEnum.values()){
			if(StringUtils.equals(name, stockTypeEnum.getName())){
				return stockTypeEnum;
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
	public static StockTypeEnum getStockTypeEnumByCode(String code){
		for(StockTypeEnum stockTypeEnum : StockTypeEnum.values()){
			if(StringUtils.equals(stockTypeEnum.getCode(), code)){
				return stockTypeEnum;
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
