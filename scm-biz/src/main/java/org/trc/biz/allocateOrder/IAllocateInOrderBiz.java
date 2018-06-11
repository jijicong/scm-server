package org.trc.biz.allocateOrder;

import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.AllocateOrder.AllocateInOrderForm;
import org.trc.form.wms.WmsAllocateOutInRequest;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;

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
    AllocateInOrder queryDetail(String allocateOrderCode);

    /**
     * 调拨入库单取消发货
     * @param allocateOrderCode 调拨单号
     * @param flag 操作标识:0-取消收货,1-重新收货
     * @param cancelReson 关闭原因
     * @param aclUserAccreditInfo
     */
    void orderCancel(String allocateOrderCode, String flag, String cancelReson, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 调拨入库单关闭
     * @param allocateOrderCode 调拨单号
     * @param flag 操作标识:0-关闭,1-取消关闭
     * @param cancelReson 关闭原因
     * @param aclUserAccreditInfo
     */
    void orderClose(String allocateOrderCode, String flag, String cancelReson, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 通知收货
     * @param allocateOrderCode
     * @param aclUserAccreditInfo
     */
    void noticeReciveGoods(String allocateOrderCode, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 自营仓库回调
     * @param req
     * @return
     */
    Response inFinishCallBack(WmsAllocateOutInRequest req);

    void updateAllocateInDetail();

}
