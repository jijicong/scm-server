package org.trc.form.afterSale;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.QueryParam;

@Getter
@Setter
public class AfterSaleWarehouseNoticeDO {

	@QueryParam("createTimeStart")
	@ApiModelProperty(value="创建时间开始  yyyy-mm-dd")
	public String createTimeStart;
	
	@QueryParam("createTimeEnd")
	@ApiModelProperty(value="创建时间结束  yyyy-mm-dd")
	public String createTimeEnd;
	
	@QueryParam("warehouseNoticeCode")
	@ApiModelProperty(value="入库单编号")
	public String warehouseNoticeCode;
	
	@QueryParam("afterSaleCode")
	@ApiModelProperty(value="售后单编号")
	public String afterSaleCode;
	
	@QueryParam("shopOrderCode")
	@ApiModelProperty(value="订单编号")
	public String shopOrderCode;
	
	@QueryParam("warehouseCode")
	@ApiModelProperty(value="入库仓库编码")
	public String warehouseCode;
	
	@QueryParam("operator")
	@ApiModelProperty(value="操作人")
	public String operator;

	@QueryParam("status")
	@ApiModelProperty(value="状态")
	public String status;
}
