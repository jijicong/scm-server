package org.trc.form.afterSale;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.trc.domain.order.OrderItem;

import javax.ws.rs.FormParam;

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
	@FormParam("maxReturnNum")
	public int maxReturnNum;
	
}
