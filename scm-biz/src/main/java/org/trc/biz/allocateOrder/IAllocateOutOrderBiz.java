package org.trc.biz.allocateOrder;

import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.AllocateOrder.AllocateOutOrderForm;
import org.trc.form.wms.WmsAllocateOutRequest;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;

public interface IAllocateOutOrderBiz {

    Pagenation<AllocateOutOrder> allocateOutOrderPage(AllocateOutOrderForm form, Pagenation<AllocateOutOrder> page);

    Response close(Long id, String remark, AclUserAccreditInfo aclUserAccreditInfo);

    Response cancelClose(Long id, AclUserAccreditInfo aclUserAccreditInfo);

    AllocateOutOrder queryDetail(Long id);

	Response allocateOrderOutNotice(Long id, AclUserAccreditInfo property);
	
	Response outFinishCallBack(WmsAllocateOutRequest req);
}
