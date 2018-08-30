package org.trc.form.afterSale;

import org.trc.domain.order.OrderItem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AfterSaleOrderItemVO extends OrderItem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 可退款数量
	 */
	@ApiModelProperty(value="可退数量")
	public int canRefundNum;
	
}
