package org.trc.form.warehouse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScmReturnInOrderDetail {

	/**
	 * 入库单编号
	 */
	private String warehouseNoticeCode;
	/**
	 * 店铺订单编号
	 */
	private String shopOrderCode;
	/**
	 * 商品订单编码
	 */
	private String orderItemCode;
	/**
	 * skucode
	 */
	private String skuCode;
	/**
	 * 商品名称
	 */
	private String skuName;
	/**
	 * 商品规格描述
	 */
	private String specNatureInfo;
	/**
	 * 条形码
	 */
	private String barCode;
	/**
	 * 品牌名称
	 */
	private String brandName;
	/**
	 * 商品图片
	 */
	private String picture;
	/**
	 * 拟退货数量
	 */
	private Integer returnNum;
	/**
	 * 最大退货数量货数量
	 */
	private Integer maxReturnNum;
}
