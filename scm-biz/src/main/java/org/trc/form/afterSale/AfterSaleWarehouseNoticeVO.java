package org.trc.form.afterSale;

import java.util.List;

import javax.ws.rs.FormParam;

import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.afterSale.AfterSaleWarehouseNoticeDetail;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Api(value = "退货入库单信息")
public class AfterSaleWarehouseNoticeVO extends AfterSaleWarehouseNotice{

	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="退货入库单详情")
	@FormParam("warehouseNoticeDetailList")
	public List<AfterSaleWarehouseNoticeDetail> warehouseNoticeDetailList;

}
