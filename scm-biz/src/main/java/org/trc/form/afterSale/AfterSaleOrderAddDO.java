package org.trc.form.afterSale;

import java.util.List;

import org.trc.domain.afterSale.AfterSaleOrderDetail;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AfterSaleOrderAddDO {

	/**
	 * 订单号
	 */
	public String shopOrderCode;
	
	/**
	 * 图片路劲
	 */
	public String picture;
	
	/**
	 * 备注
	 */
	public String memo;
	
	/**
	 * 快递公司
	 */
	public String logistics_corporation;
	
	/**
	 * 快递公司编码
	 */
	public String logistics_corporation_code;
	
	/**
	 * 快递单号
	 */
	public String express_number;
	
	/**
	 * 入库仓编号
	 */
	public String wms_code;
	
	/**
	 * 售后单详情
	 */
	public List<AfterSaleOrderDetail> afterSaleOrderDetailList;
}
