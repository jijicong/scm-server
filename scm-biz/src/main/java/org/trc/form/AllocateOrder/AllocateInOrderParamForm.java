package org.trc.form.AllocateOrder;

import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;

import java.util.List;

public class AllocateInOrderParamForm {

    private AllocateInOrder allocateInOrder;

    private List<AllocateSkuDetail> allocateSkuDetailList;

    public AllocateInOrder getAllocateInOrder() {
        return allocateInOrder;
    }

    public void setAllocateInOrder(AllocateInOrder allocateInOrder) {
        this.allocateInOrder = allocateInOrder;
    }

    public List<AllocateSkuDetail> getAllocateSkuDetailList() {
        return allocateSkuDetailList;
    }

    public void setAllocateSkuDetailList(List<AllocateSkuDetail> allocateSkuDetailList) {
        this.allocateSkuDetailList = allocateSkuDetailList;
    }
}
