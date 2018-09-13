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
public class AfterSaleWarehouseNoticeVO extends AfterSaleWarehouseNotice{

	
	private static final long serialVersionUID = 1L;
	
	//销售渠道名称
	private String sellCodeName;
	
	public List<AfterSaleWarehouseNoticeDetail> warehouseNoticeDetailList;

}
