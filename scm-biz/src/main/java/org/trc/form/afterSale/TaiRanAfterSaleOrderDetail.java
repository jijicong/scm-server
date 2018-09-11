package org.trc.form.afterSale;

import java.math.BigDecimal;

import javax.ws.rs.QueryParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaiRanAfterSaleOrderDetail {

	/**
	 * 供应链sku编码
	 */
	@QueryParam("skuCode")
	private String skuCode;
	
	/**
	 * 退款金额
	 */
	@QueryParam("refundAmont")
	private BigDecimal refundAmont;
	
	/**
	 * 退货数量
	 */
	@QueryParam("returnNum")
	private Integer returnNum;
	
	/**
	 * 正品入库数量，若退货场景为实体店退货，此项必填
	 */
	@QueryParam("inNum")
	private Integer inNum;
	
	/**
	 * 残品入库数量，若退货场景为实体店退货，此项必填
	 */
	@QueryParam("defectiveInNum")
	private Integer defectiveInNum;
}
