package org.trc.enums;

/**
 * 是否门店订单
 */
public enum IsStoreOrderEnum {

	NOT_STORE_ORDER(1,"非门店订单"),
	STORE_ORDER(2,"门店订单");

	private Integer code;
	private String name;

	IsStoreOrderEnum(Integer code, String name){
		this.code = code;
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
