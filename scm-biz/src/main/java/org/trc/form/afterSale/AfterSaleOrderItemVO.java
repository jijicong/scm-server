package org.trc.form.afterSale;

import org.trc.domain.order.OrderItem;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AfterSaleOrderItemVO extends OrderItem{

	/**
	 * 可退款数量
	 */
	public int canRefundNum;
	
}
