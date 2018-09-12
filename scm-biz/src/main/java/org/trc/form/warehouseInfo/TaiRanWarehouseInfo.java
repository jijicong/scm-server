package org.trc.form.warehouseInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaiRanWarehouseInfo {

	/**
	 * 仓库编码
	 */
	private String code;
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	/**
	 * 仓库类型 1.保税仓 2.海外仓 3.普通仓
	 */
	private String warehouseTypeCode;
	/**
	 * 仓库联系方式
	 */
	private String warehouseContactNumber;
	/**
	 * 仓库联系人
	 */
	private String warehouseContact;
	/**
	 * 仓库所在的省份
	 */
	private String province;
	/**
	 * 仓库所在的城市
	 */
	private String city;
	/**
	 * 仓库所在的地区(地区包括市下面的县和地区)
	 */
	private String area;
	/**
	 * 仓库所在的详细地址
	 */
	private String address;
	/**
	 * 全地址名称
	 */
	private String allAreaName;
}
