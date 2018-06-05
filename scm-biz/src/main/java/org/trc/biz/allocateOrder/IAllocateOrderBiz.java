package org.trc.biz.allocateOrder;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.form.AllocateOrder.AllocateItemForm;
import org.trc.form.AllocateOrder.AllocateOrderForm;
import org.trc.util.Pagenation;

public interface IAllocateOrderBiz {
	
    Pagenation<AllocateOrder> allocateOrderPage(AllocateOrderForm form, Pagenation<AllocateOrder> page);

	void saveAllocateOrder(AllocateOrder allocateOrder, String skuDetail, String delIds,
			String isReview, AclUserAccreditInfo aclUserAccreditInfo);

	void deleteAllocateOrder(String orderId);

	void dropAllocateOrder(String orderId, AclUserAccreditInfo aclUserAccreditInfo);

	void setDropAllocateOrder(String orderId, AclUserAccreditInfo aclUserAccreditInfo);

	void noticeWarehouse(String orderId, AclUserAccreditInfo aclUserAccreditInfo);

	AllocateOrder allocateOrderEditGet(String orderId);

	Pagenation<AllocateSkuDetail> querySkuList(AllocateItemForm form, Pagenation<AllocateSkuDetail> page, String skus);

	void allocateOrderAudit(String orderId, String auditOpinion, String auditResult, AclUserAccreditInfo property);

	Response queryWarehouse();

	Map<String, Long> inventoryQuery(String warehouseCode, String queryStr);

}
