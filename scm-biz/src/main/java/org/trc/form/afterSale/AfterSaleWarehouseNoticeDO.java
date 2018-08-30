package org.trc.form.afterSale;



import javax.ws.rs.FormParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AfterSaleWarehouseNoticeDO {

	@ApiModelProperty(value="创建时间开始  yyyy-mm-dd")
	@FormParam("createTimeStart")
	public String createTimeStart;
	
	@ApiModelProperty(value="创建时间结束  yyyy-mm-dd")
	@FormParam("createTimeEnd")
	public String createTimeEnd;
	
	@ApiModelProperty(value="入库单编号")
	@FormParam("warehouseNoticeCode")
	public String warehouseNoticeCode;
	
	@ApiModelProperty(value="售后单编号")
	@FormParam("afterSaleCode")
	public String afterSaleCode;
	
	@ApiModelProperty(value="订单编号")
	@FormParam("shopOrderCode")
	public String shopOrderCode;
	
	@ApiModelProperty(value="入库仓库编码")
	@FormParam("warehouseCode")
	public String warehouseCode;
	
	@ApiModelProperty(value="操作人")
	@FormParam("operator")
	public String operator;
}
