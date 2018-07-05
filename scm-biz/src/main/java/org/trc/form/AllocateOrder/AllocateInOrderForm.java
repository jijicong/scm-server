package org.trc.form.AllocateOrder;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzcyn on 2018/5/4.
 */
public class AllocateInOrderForm extends QueryModel {

    @QueryParam("allocateOrderCode")
    private String allocateOrderCode;

    @QueryParam("allocateInOrderCode")
    private String allocateInOrderCode;

    @QueryParam("inWarehouseCode")
    private String inWarehouseCode;

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

    public String getAllocateInOrderCode() {
        return allocateInOrderCode;
    }

    public void setAllocateInOrderCode(String allocateInOrderCode) {
        this.allocateInOrderCode = allocateInOrderCode;
    }

    public String getInWarehouseCode() {
        return inWarehouseCode;
    }

    public void setInWarehouseCode(String inWarehouseCode) {
        this.inWarehouseCode = inWarehouseCode;
    }
}
