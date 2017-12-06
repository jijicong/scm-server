package org.trc.form.order;

import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundOrder;

import java.util.List;

public class OutboundForm {

    /**
     * 发货通知单
     */
    private OutboundOrder outboundOrder;
    /**
     * 发货通知单明细
     */
    private List<OutboundDetail> outboundDetailList;

    public OutboundOrder getOutboundOrder() {
        return outboundOrder;
    }

    public void setOutboundOrder(OutboundOrder outboundOrder) {
        this.outboundOrder = outboundOrder;
    }

    public List<OutboundDetail> getOutboundDetailList() {
        return outboundDetailList;
    }

    public void setOutboundDetailList(List<OutboundDetail> outboundDetailList) {
        this.outboundDetailList = outboundDetailList;
    }
}
