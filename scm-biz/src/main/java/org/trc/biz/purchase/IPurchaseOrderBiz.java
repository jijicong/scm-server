package org.trc.biz.purchase;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.purchase.PurchaseOrderAddData;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierBrandExt;
import org.trc.form.purchase.ItemForm;
import org.trc.form.purchase.PurchaseOrderForm;
import org.trc.util.Pagenation;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
public interface IPurchaseOrderBiz {

    Pagenation<PurchaseOrder> purchaseOrderPage(PurchaseOrderForm form,Pagenation<PurchaseOrder> page,String channelCode) ;
    /**
     * 根据渠道用户的id查询对应的供应商
     * @param userId
     * @return
     * @
     */
    List<Supplier> findSuppliersByUserId(String userId) ;

    /**
     * 根据供应商的code查询可采购的商品列表-(排除已经在采购列表出现的商品)
     * @param supplierCode
     * @return
     * @
     */
    Pagenation<PurchaseDetail> findPurchaseDetailBySupplierCode(String supplierCode,  ItemForm form, Pagenation<PurchaseDetail> page, String skus) ;

    void savePurchaseOrder(PurchaseOrderAddData purchaseOrder, String status,AclUserAccreditInfo aclUserAccreditInfo) ;

    String updatePurchaseOrderState(PurchaseOrder purchaseOrder,AclUserAccreditInfo aclUserAccreditInfo) ;

    PurchaseOrder findPurchaseOrderAddDataById(Long id) ;

    /**
     * 冻结，解冻采购单
     * @param purchaseOrder
     */
    void  updatePurchaseStateFreeze(PurchaseOrder purchaseOrder,AclUserAccreditInfo aclUserAccreditInfo) ;

    PurchaseOrder findPurchaseOrderAddDataByCode(String purchaseCode);

    //查询所有的可卖商品
    List<PurchaseDetail> findAllPurchaseDetailBysupplierCode(String supplierCode) ;

    /**
     * 更新采购单及采购商品
     * @param purchaseOrderAddData  更新的数据
     * @param  aclUserAccreditInfo  用于采购商品的创建人
     */
    void updatePurchaseOrder(PurchaseOrderAddData purchaseOrderAddData,AclUserAccreditInfo aclUserAccreditInfo) ;

    /**
     * 入库通知单的发起(信息保存,还未推送)
     * @param purchaseOrder
     */
    void warahouseAdvice(PurchaseOrder purchaseOrder, AclUserAccreditInfo aclUserAccreditInfo);
    /**
     * 采购单入库通知状态的作废操作
     */
    void cancelWarahouseAdvice(PurchaseOrder purchaseOrder,AclUserAccreditInfo aclUserAccreditInfo);
    /**
     *联想搜索
     */
    List<String> associationSearch(String queryString) throws Exception;

    /**
     * 根据供应商的编码，查询该供应商的品牌
     * @param supplierCode
     * @return
     */
    List<SupplierBrandExt> findSupplierBrand(String supplierCode) throws Exception;

    /**
     * 删除缓存
     */
    public void cacheEvitForPurchaseOrder();

    /**
     * 根据业务线查询仓库信息
     * @param channelCode
     * @return
     */
    Response findWarehousesByChannelCode(String channelCode);

    /**
     * 查询可卖商品
     * 查询可卖商品
     * @return
     */
    Response findPurchaseDetail(ItemForm form,Pagenation<PurchaseDetail> page, String skus) ;

}
