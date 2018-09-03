package org.trc.form.afterSale;

import java.util.List;

import javax.ws.rs.FormParam;

import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.afterSale.AfterSaleWarehouseNoticeDetail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AfterSaleWarehouseNoticeVO extends AfterSaleWarehouseNotice{

	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="售后入库单详情")
	@FormParam("warehouseNoticeDetailList")
	public List<AfterSaleWarehouseNoticeDetail> warehouseNoticeDetailList;

}
