package org.trc.biz.outbuond;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.OutboundOrder;
import org.trc.form.outbound.OutBoundOrderForm;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;


public interface IOutBoundOrderBiz {

    //发货通知单分页查询
    Pagenation<OutboundOrder> outboundOrderPage(OutBoundOrderForm queryModel, Pagenation<OutboundOrder> page, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    //发货明细更新
    void updateOutboundDetail(String requestText) throws Exception;

    /**
     * 发货通知单创建
     * @param outboundOrderId 主键
     * @throws Exception
     */
    void createOutbound(String outboundOrderId) throws Exception;

    Response orderCancel(Long id, String remark);
}
