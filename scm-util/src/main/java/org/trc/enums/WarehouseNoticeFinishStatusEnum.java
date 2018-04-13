package org.trc.enums;

public enum WarehouseNoticeFinishStatusEnum {

	UNFINISHED("0","未完成"),
	FINISHED("1","已完成");
	
	private String code; 
	private String name; 
	
	WarehouseNoticeFinishStatusEnum (String code, String name){
		this.code = code;
		this.name = name;
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

