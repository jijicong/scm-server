package org.trc.form.purchase;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Description〈采购退货单〉
 *
 * @author hzliuwei
 * @create 2018/7/24
 */
public class PurchaseOutboundOrderForm extends QueryModel {

    static final long serialVersionUID = 42L;

    /**
     * 采购退货单编号
     */
    @QueryParam("purchaseOrderCode")
    @Length(max = 32)
    private String purchaseOutboundOrderCode;

    /**
     *供应商名称
     */
    @QueryParam("supplierName")
    @Length(max = 64)
    private String supplierName;

    /**
     * 退货仓库
     */
    @QueryParam("warehouseName")
    @Length(max = 64)
    private String warehouseName;

    /**
     *  退货类型1-正品，2-残品
     */
    @QueryParam("returnOrderType")
    private String returnOrderType;

    /**
     *  单据状态:0-暂存,1-提交审核,2-审核驳回,3-审核通过,4-出库通知,5-作废
     */
    @QueryParam("status")
    private String status;

    /**
     * 出库状态:1-等待出库，2-出库完成，3-出库异常，4-其他
     */
    @QueryParam("outboundStatus")
    private String outboundStatus;

    public String getPurchaseOutboundOrderCode() {
        return purchaseOutboundOrderCode;
    }

    public void setPurchaseOutboundOrderCode(String purchaseOutboundOrderCode) {
        this.purchaseOutboundOrderCode = purchaseOutboundOrderCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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
