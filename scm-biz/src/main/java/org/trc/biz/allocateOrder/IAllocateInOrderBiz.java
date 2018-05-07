package org.trc.biz.allocateOrder;

import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.form.AllocateOrder.AllocateInOrderForm;
import org.trc.util.Pagenation;

public interface IAllocateInOrderBiz {

    /**
     * 调拨入库单分页查询
     * @param form
     * @param page
     * @return
     */
    Pagenation<AllocateInOrder> allocateInOrderPage(AllocateInOrderForm form, Pagenation<AllocateInOrder> page);

    /**
     * 查询调拨入库单明细
     * @param allocateOrderCode 调拨单号
     * @return
     */
    AllocateSkuDetail queryDetail(String allocateOrderCode);

}
