package org.trc.enums;


public enum OrderCancelResultEnum {
	
	/**
	 * @author admin
	 * 单据取消状态
	 */
		
	CANCEL_SUCC("1","取消成功"),
	CANCEL_FAIL("2","取消失败"),
	CANCELLING("3","取消中");

	public String code;
	public String name;

	OrderCancelResultEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
		

}

