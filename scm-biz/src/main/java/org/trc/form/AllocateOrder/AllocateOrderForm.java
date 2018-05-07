package org.trc.form.AllocateOrder;

import javax.ws.rs.QueryParam;

import org.trc.util.QueryModel;

public class AllocateOrderForm extends QueryModel {
	
	private static final long serialVersionUID = -7555581228989383897L;

	/**
     * 调拨单编号
     */
    @QueryParam("allocateOrderCode")
    private String allocateOrderCode;

    /**
     * 调入仓库编号
     */
    @QueryParam("inWarehouseCode")
    private String inWarehouseCode;
    
    /**
     * 调出仓库编号
     */
    @QueryParam("outWarehouseCode")
    private String outWarehouseCode;
    
    /**
     * 出入库状态
     */
    @QueryParam("inOutStatus")
    private String inOutStatus;
    
    /**
     * 单据状态
     */
    @QueryParam("orderStatus")
    private String orderStatus;
    
    /**
     * 更新时间开始,格式yyyy-mm-dd hh:mi:ss
     */
    @QueryParam("updateTimeStart")
    private String updateTimeStart;
    
    /**
     * 更新时间结束,格式yyyy-mm-dd hh:mi:ss
     */
    @QueryParam("updateTimeEnd")
    private String updateTimeEnd;
    
    /**
     * 创建时间开始,格式yyyy-mm-dd hh:mi:ss
     */
    @QueryParam("createTimeStart")
    private String createTimeStart;
    
    /**
     * 创建时间结束,格式yyyy-mm-dd hh:mi:ss
     */
    @QueryParam("createTimeEnd")
    private String createTimeEnd;
    
    /**
     * 审核状态 0-全部 1-待审核 2-已审核， 不传则显示所有状态
     */
    @QueryParam("auditStatus")
    private String auditStatus;

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getAllocateOrderCode() {
		return allocateOrderCode;
	}

	public void setAllocateOrderCode(String allocateOrderCode) {
		this.allocateOrderCode = allocateOrderCode;
	}

	public String getInWarehouseCode() {
		return inWarehouseCode;
	}

	public void setInWarehouseCode(String inWarehouseCode) {
		this.inWarehouseCode = inWarehouseCode;
	}

	public String getOutWarehouseCode() {
		return outWarehouseCode;
	}

	public void setOutWarehouseCode(String outWarehouseCode) {
		this.outWarehouseCode = outWarehouseCode;
	}

	public String getInOutStatus() {
		return inOutStatus;
	}

	public void setInOutStatus(String inOutStatus) {
		this.inOutStatus = inOutStatus;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getUpdateTimeStart() {
		return updateTimeStart;
	}

	public void setUpdateTimeStart(String updateTimeStart) {
		this.updateTimeStart = updateTimeStart;
	}

	public String getUpdateTimeEnd() {
		return updateTimeEnd;
	}

	public void setUpdateTimeEnd(String updateTimeEnd) {
		this.updateTimeEnd = updateTimeEnd;
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
