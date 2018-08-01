package org.trc.biz.purchase;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseOutboundDetail;
import org.trc.domain.purchase.PurchaseOutboundOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseNotice.WarehouseNoticeDetails;
import org.trc.form.purchase.PurchaseOutboundItemForm;
import org.trc.form.purchase.PurchaseOutboundOrderForm;
import org.trc.util.Pagenation;

import java.util.List;

public interface IPurchaseOutboundOrderBiz {

    /**
     * 查询采购退货单列表
     *
     * @param form        查询条件
     * @param page        分页数据
     * @param channelCode
     * @return
     */
    Pagenation<PurchaseOutboundOrder> purchaseOutboundOrderPageList(PurchaseOutboundOrderForm form, Pagenation<PurchaseOutboundOrder> page, String channelCode);

    /**
     * 采购退货单保存或提交审核
     *
     * @param form                采购退货单数据
     * @param code                保存类型 0-暂存 1-提交审核
     * @param aclUserAccreditInfo
     */
    void savePurchaseOutboundOrder(PurchaseOutboundOrder form, String code, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 更新采购退货单
     *
     * @param form                表单数据
     * @param aclUserAccreditInfo
     */
    void updatePurchaseOutboundOrder(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 根据采购退货单Id查询采购退货单
     *
     * @param id 采购退货单Id
     * @return
     */
    PurchaseOutboundOrder getPurchaseOutboundOrderById(Long id);

    /**
     * 获取采购退货单商品详情
     *
     * @param form 查询条件
     * @param page
     * @param skus 过滤已选择的sku
     * @return
     */
    Pagenation<PurchaseOutboundDetail> getPurchaseOutboundOrderDetail(PurchaseOutboundItemForm form, Pagenation<PurchaseOutboundDetail> page, String skus);

    /**
     * 采购退货单获取采购历史详情
     *
     * @param form
     * @param page
     * @return
     */
    Pagenation<WarehouseNoticeDetails> getPurchaseHistory(PurchaseOutboundItemForm form, Pagenation<WarehouseNoticeDetails> page);

    /**
     * 作废出库通知操作
     *
     * @param form
     * @param aclUserAccreditInfo
     */
    void cancelWarahouseAdvice(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 更新采购退货单状态
     *
     * @param form
     * @param aclUserAccreditInfo
     * @return
     */
    String updateStatus(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 采购退货单出库通知
     *
     * @param form
     * @param aclUserAccreditInfo
     */
    void warehouseAdvice(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 采购退货单审核操作，获取详情
     *
     * @param id 采购退货单Id
     * @return
     */
    PurchaseOutboundOrder getPurchaseOutboundAuditOrder(Long id);

    /**
     * 获取采购退货单审核列表
     *
     * @param form        查询条件
     * @param page        分页数据
     * @param channelCode
     * @return
     */
    Pagenation<PurchaseOutboundOrder> getAuditPagelist(PurchaseOutboundOrderForm form, Pagenation<PurchaseOutboundOrder> page, String channelCode);

    /**
     * 采购退货单审核
     *
     * @param form
     * @param aclUserAccreditInfo
     */
    void auditPurchaseOrder(PurchaseOutboundOrder form, AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 获取退货仓库下拉列表
     *
     * @param channelCode
     * @return
     */
    List<WarehouseInfo> getWarehousesByChannelCode(String channelCode);

    /**
     * 获取供应商名称下拉列表
     *
     * @param channelCode
     * @return
     */
    List<Supplier> getSuppliersByChannelCode(String channelCode);


    /**
     *  采购退货单保存或
     * @param form  采购退货单数据
     * @param code  保存类型
     * @param property
     */
    //void savePurchaseOutboundOrder(PurchaseOutboundOrderDataForm form, String code, AclUserAccreditInfo property);
}
