package org.trc.form.purchase;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(name = "采购退货单编号")
    private String purchaseOutboundOrderCode;

    /**
     *供应商名称
     */
    @QueryParam("supplierName")
    @Length(max = 64)
    @ApiModelProperty(name = "供应商名称")
    private String supplierName;

    /**
     * 退货仓库
     */
    @QueryParam("warehouseName")
    @Length(max = 64)
    @ApiModelProperty(name = "退货仓库")
    private String warehouseName;

    /**
     *  退货类型1-正品，2-残品
     */
    @QueryParam("returnOrderType")
    @ApiModelProperty(name = "退货类型1-正品，2-残品")
    private String returnOrderType;

    /**
     *  单据状态:0-暂存,1-提交审核,2-审核驳回,3-审核通过,4-出库通知,5-作废
     */
    @QueryParam("status")
    @ApiModelProperty(name = "单据状态:0-暂存,1-提交审核,2-审核驳回,3-审核通过,4-出库通知,5-作废")
    private String status;

    /**
     * 出库状态:1-等待出库，2-出库完成，3-出库异常，4-其他
     */
    @QueryParam("outboundStatus")
    @ApiModelProperty(name = "出库状态:1-等待出库，2-出库完成，3-出库异常，4-其他")
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
