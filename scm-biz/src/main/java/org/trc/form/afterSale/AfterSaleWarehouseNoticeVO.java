package org.trc.form.afterSale;

import java.util.List;

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
	public List<AfterSaleWarehouseNoticeDetail> warehouseNoticeDetailList;

}
