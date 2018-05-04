package org.trc.form.AllocateOrder;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzcyn on 2018/5/4.
 */
public class AllocateOutOrderForm extends QueryModel {

    @QueryParam("allocateOrderCode")
    private String allocateOrderCode;

    @QueryParam("allocateOutOrderCode")
    private String allocateOutOrderCode;

    @QueryParam("outWarehouseCode")
    private String outWarehouseCode;

    @QueryParam("status")
    private String status;

    @QueryParam("createOperatorName")
    private String createOperatorName;

    public String getAllocateOrderCode() {
        return allocateOrderCode;
    }

    public void setAllocateOrderCode(String allocateOrderCode) {
        this.allocateOrderCode = allocateOrderCode;
    }

    public String getAllocateOutOrderCode() {
        return allocateOutOrderCode;
    }

    public void setAllocateOutOrderCode(String allocateOutOrderCode) {
        this.allocateOutOrderCode = allocateOutOrderCode;
    }

    public String getOutWarehouseCode() {
        return outWarehouseCode;
    }

    public void setOutWarehouseCode(String outWarehouseCode) {
        this.outWarehouseCode = outWarehouseCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateOperatorName() {
        return createOperatorName;
    }

    public void setCreateOperatorName(String createOperatorName) {
        this.createOperatorName = createOperatorName;
    }
}
