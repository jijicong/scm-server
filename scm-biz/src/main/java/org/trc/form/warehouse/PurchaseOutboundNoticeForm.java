package org.trc.form.warehouse;

import javax.ws.rs.QueryParam;

import org.trc.util.QueryModel;

import io.swagger.annotations.ApiModelProperty;

public class PurchaseOutboundNoticeForm extends QueryModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4060748749867618253L;

	/**
     * 退货出库通知单编号
     */
    @ApiModelProperty("退货出库通知单编号")
    @QueryParam("outboundNoticeCode")
    private String outboundNoticeCode;
    
	/**
     * 采购退货单编号
     */
    @ApiModelProperty(name = "采购退货单编号")
    @QueryParam("purchaseOrderCode")
    private String purchaseOutboundOrderCode;
    
    /**
     * 退货仓库编号
     */
    @ApiModelProperty("退货仓库编号")
    @QueryParam("warehouseCode")
    private String warehouseCode;
    
    /**
     * 供应商id
     */
    @ApiModelProperty("供应商id")
    @QueryParam("supplierId")
    private String supplierId;
    
    /**
     * 状态:0-待通知出库,1-出库仓接收成功,2-出库仓接收失败,3-出库完成,4-出库异常,5-已取消
     */
    @ApiModelProperty("出库单状态")
    @QueryParam("status")
    private String status;
    
    /**
     * 出库单创建人
     */
    @ApiModelProperty("出库单创建人")
    @QueryParam("create_user")
    private String createUser;
    
    /**
     * 创建时间开始,格式yyyy-mm-dd hh:mi:ss
     */
    @ApiModelProperty("创建时间开始")
    @QueryParam("createTimeStart")
    private String createTimeStart;
    
    /**
     * 创建时间结束,格式yyyy-mm-dd hh:mi:ss
     */
    @ApiModelProperty("创建时间结束")
    @QueryParam("createTimeEnd")
    private String createTimeEnd;

	public String getOutboundNoticeCode() {
		return outboundNoticeCode;
	}

	public void setOutboundNoticeCode(String outboundNoticeCode) {
		this.outboundNoticeCode = outboundNoticeCode;
	}

	public String getPurchaseOutboundOrderCode() {
		return purchaseOutboundOrderCode;
	}

	public void setPurchaseOutboundOrderCode(String purchaseOutboundOrderCode) {
		this.purchaseOutboundOrderCode = purchaseOutboundOrderCode;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}
    
    

}
