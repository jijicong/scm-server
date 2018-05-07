package org.trc.biz.allocateOrder;

import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.AllocateOrder.AllocateOrderForm;
import org.trc.util.Pagenation;

public interface IAllocateOrderBiz {
	
    Pagenation<AllocateOrder> allocateOrderPage(AllocateOrderForm form, Pagenation<AllocateOrder> page);

	void saveAllocateOrder(AllocateOrder allocateOrder, String skuDetail, String delIds,
			String isReview, AclUserAccreditInfo aclUserAccreditInfo);

	void deleteAllocateOrder(String orderId);

	void dropAllocateOrder(String orderId);

	void noticeWarehouse(String orderId, AclUserAccreditInfo aclUserAccreditInfo);

	AllocateOrder allocateOrderEditGet(String orderId);

}
