package org.trc.form.AllocateOrder;

public class QuerySkuInventory {
	
	/**
	 * sku编码
	 */
	private String skuCode;
	
	/**
	 * 库存类型 0：正品  1：残品
	 */
	private String inventoryType;
	
	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getInventoryType() {
		return inventoryType;
	}

	public void setInventoryType(String inventoryType) {
		this.inventoryType = inventoryType;
	}


	
	
}
