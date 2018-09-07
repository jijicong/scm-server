package org.trc.form.afterSale;

import lombok.Getter;
import lombok.Setter;
import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.afterSale.AfterSaleWarehouseNoticeDetail;

import java.util.List;

@Getter
@Setter
public class AfterSaleWarehouseNoticeVO extends AfterSaleWarehouseNotice{

	
	private static final long serialVersionUID = 1L;
	
	public List<AfterSaleWarehouseNoticeDetail> warehouseNoticeDetailList;

}
