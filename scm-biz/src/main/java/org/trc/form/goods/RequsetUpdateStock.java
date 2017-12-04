package org.trc.form.goods;

/**
 * 库存更新请求model
 * @author admin
 */
public class RequsetUpdateStock {
	
	/**
	 * 更新的skuCode
	 */
	private String skuCode;
	
	/**
	 * 业务线编码
	 */
	private String channelCode;
	
	/**
	 * 仓库code
	 */
	private String warehouseCode;
	
	/**
	 * 库存更新类型
	 * 1.available_inventory 可用正品库存
	 * 2.available_defective_inventory 可用残次品库存
	 * 3.lock_inventory 锁定库存
	 * 4.air_inventory 在途库存
	 * 5.frozen_inventory 冻结库存
	 * 6.real_inventory 真实库存
	 * 7.defective_inventory 残次品库存
	 */
	private String stockType;
	
	/**
	 * 更新数量
	 */
	private Long num;

	/**
	 * 更新类型
	 */
	private Integer updateType;

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

	public Integer getUpdateType() {
		return updateType;
	}

	public void setUpdateType(Integer updateType) {
		this.updateType = updateType;
	}
	

}
