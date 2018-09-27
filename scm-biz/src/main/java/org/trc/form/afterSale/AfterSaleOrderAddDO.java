package org.trc.form.afterSale;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.trc.domain.afterSale.AfterSaleOrderDetail;

import javax.ws.rs.FormParam;
import java.util.List;

@Setter
@Getter
public class AfterSaleOrderAddDO {
	


	/**
	 * 系统订单号
	 */
	@ApiModelProperty(value="系统订单号",required=true)
	@FormParam("scmShopOrderCode")
	public String scmShopOrderCode;
	
	/**
	 * 图片路劲
	 */
	@ApiModelProperty(value="图片路劲",required=true)
	@FormParam("picture")
	public String picture;
	
	/**
	 * 备注
	 */
	@ApiModelProperty(value="备注",required=true)
	@FormParam("memo")
	public String memo;
	
	/**
	 * 快递公司
	 */
	@ApiModelProperty(value="快递公司",required=true)
	@FormParam("logisticsCorporation")
	public String logisticsCorporation;
	
	/**
	 * 快递公司编码
	 */
	@ApiModelProperty(value="快递公司编码",required=true)
	@FormParam("logisticsCorporationCode")
	public String logisticsCorporationCode;
	
	/**
	 * 快递单号
	 */
	@ApiModelProperty(value="快递单号",required=true)
	@FormParam("waybillNumber")
	public String waybillNumber;
	
	/**
	 * 入库仓编号
	 */
	@ApiModelProperty(value="入库仓编号",required=true)
	@FormParam("returnWarehouseCode")
	public String returnWarehouseCode;
	
	/**
	 * 入库仓库地址
	 */
	@ApiModelProperty(value="入库仓库地址",required=true)
	@FormParam("returnAddress")
	public String returnAddress;
	/**
	 * 入库仓名称
	 */
	@ApiModelProperty(value="入库仓名称",required=true)
	@FormParam("warehouseName")
	public String warehouseName;
	
	/**
	 * 售后单详情
	 */
	@ApiModelProperty(value="售后单详情")
    @FormParam("afterSaleOrderDetailList")
	public List<AfterSaleOrderDetail> afterSaleOrderDetailList;
}
