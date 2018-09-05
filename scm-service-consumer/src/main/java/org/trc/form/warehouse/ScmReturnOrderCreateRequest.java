package org.trc.form.warehouse;


import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScmReturnOrderCreateRequest  extends ScmWarehouseRequestBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 售后单编号
	 */
	private String afterSaleCode;
	/**
	 * 入库单编号
	 */
	private String warehouseNoticeCode;
	/**
	 * 系统订单号
	 */
	private String scmShopOrderCode;
	/**
	 * 销售渠道订单号
	 */
	private String shopOrderCode;
	/**
	 * 入库仓库编码
	 */
	private String warehouseCode;
	/**
	 * 发件人
	 */
	private String sender;
	/**
	 * 发件方详细地址
	 */
	private String senderAddress;
	/**
	 * 发件人所在城市
	 */
	private String senderCity;
	/**
	 * 发件人手机
	 */
	private String senderNumber;
	/**
	 * 发件人所在省
	 */
	private String senderProvince;
	/**
	 * 收货人
	 */
	private String receiver;
	/**
	 * 收件方地址
	 */
	private String receiverAddress;
	/**
	 * 收件方城市
	 */
	private String receiverCity;
	/**
	 * 发件人手机
	 */
	private String receiverNumber;
	/**
	 * 收件方省份
	 */
	private String receiverProvince;
	/**
	 * 商品行数(sku数量)
	 */
	private int skuNum;
	/**
	 * 操作人
	 */
	private String operator;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 子退货入库单
	 */
	private List<ScmReturnInOrderDetail> returnInOrderDetailList;
	

}
