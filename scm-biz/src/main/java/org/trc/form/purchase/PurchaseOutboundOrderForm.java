package org.trc.form.purchase;

import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Description〈采购退货单查询条件〉
 *
 * @author hzliuwei
 * @create 2018/7/24
 */
public class PurchaseOutboundOrderForm extends QueryModel {


    private static final long serialVersionUID = -4853351816027411420L;
    /**
     * 采购退货单编号
     */
    @QueryParam("purchaseOrderCode")
    @Length(max = 32)
    @ApiParam(value = "采购退货单编号")
    private String purchaseOutboundOrderCode;

    /**
     *供应商名称
     */
    @QueryParam("supplierCode")
    @Length(max = 64)
    @ApiParam(value = "供应商Code")
    private String supplierCode;

    /**
     * 退货仓库id
     */
    @QueryParam("warehouseInfoId")
    @Length(max = 64)
    @ApiParam(value = "退货仓库id")
    private String warehouseInfoId;

    /**
     *  退货类型1-正品，2-残品
     */
    @QueryParam("returnOrderType")
    @ApiParam(value = "退货类型1-正品，2-残品")
    private String returnOrderType;

    /**
     *  单据状态:0-暂存,1-提交审核,2-审核驳回,3-审核通过,4-出库通知,5-作废
     */
    @QueryParam("status")
    @ApiParam(value = "单据状态:0-暂存,1-提交审核,2-审核驳回,3-审核通过,4-出库通知,5-作废")
    private String status;

    /**
     * 出库状态:1-等待出库，2-出库完成，3-出库异常，4-其他
     */
    @QueryParam("outboundStatus")
    @ApiParam(value = "出库状态:1-等待出库，2-出库完成，3-出库异常，4-其他")
    private String outboundStatus;

    /**
     * 审核状态：1-提交审核,2-审核驳回,3-审核通过,
     */
    @QueryParam("auditStatus")
    @ApiParam(value = "审核状态：1-提交审核,2-审核驳回,3-审核通过,")
    private String auditStatus;

    /**
     * 提交审核时间
     */
    @QueryParam("commitAuditTime")
    @ApiParam(value = "提交审核时间")
    private String commitAuditTime;

    public String getCommitAuditTime() {
        return commitAuditTime;
    }

    public void setCommitAuditTime(String commitAuditTime) {
        this.commitAuditTime = commitAuditTime;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getPurchaseOutboundOrderCode() {
        return purchaseOutboundOrderCode;
    }

    public void setPurchaseOutboundOrderCode(String purchaseOutboundOrderCode) {
        this.purchaseOutboundOrderCode = purchaseOutboundOrderCode;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getWarehouseInfoId() {
        return warehouseInfoId;
    }

    public void setWarehouseInfoId(String warehouseInfoId) {
        this.warehouseInfoId = warehouseInfoId;
    }

    public String getReturnOrderType() {
        return returnOrderType;
    }

    public void setReturnOrderType(String returnOrderType) {
        this.returnOrderType = returnOrderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutboundStatus() {
        return outboundStatus;
    }

    public void setOutboundStatus(String outboundStatus) {
        this.outboundStatus = outboundStatus;
    }
}
