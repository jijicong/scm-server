package org.trc.biz.purchase;

import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.purchase.PurchaseOrderAddData;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierBrandExt;
import org.trc.form.purchase.ItemForm;
import org.trc.form.purchase.PurchaseOrderForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;

import javax.ws.rs.BeanParam;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
public interface IPurchaseOrderBiz {

    Pagenation<PurchaseOrder> purchaseOrderPage(PurchaseOrderForm form,Pagenation<PurchaseOrder> page,ContainerRequestContext requestContext) ;
    /**
     * 根据渠道用户的id查询对应的供应商
     * @param requestContext
     * @return
     * @
     */
    List<Supplier> findSuppliersByUserId(ContainerRequestContext requestContext) ;

    /**
     * 根据供应商的code查询可采购的商品列表-(排除已经在采购列表出现的商品)
     * @param supplierCode
     * @return
     * @
     */
    Pagenation<PurchaseDetail> findPurchaseDetailBySupplierCode(String supplierCode,  ItemForm form, Pagenation<PurchaseDetail> page, String skus) ;

    void savePurchaseOrder(PurchaseOrderAddData purchaseOrder, String status,ContainerRequestContext requestContext) ;

    void updatePurchaseOrderState(PurchaseOrder purchaseOrder,ContainerRequestContext requestContext) ;

    PurchaseOrder findPurchaseOrderAddDataById(Long id) ;

    /**
     * 冻结，解冻采购单
     * @param purchaseOrder
     * @
     */
    void  updatePurchaseStateFreeze(PurchaseOrder purchaseOrder,ContainerRequestContext requestContext) ;

    PurchaseOrder findPurchaseOrderAddDataByCode(String purchaseCode);

    //查询所有的可卖商品
    List<PurchaseDetail> findAllPurchaseDetailBysupplierCode(String supplierCode) ;

    /**
     * 更新采购单及采购商品
     * @param purchaseOrderAddData  更新的数据
     * @param requestContext         用于采购商品的创建人
     * @
     */
    void updatePurchaseOrder(PurchaseOrderAddData purchaseOrderAddData,ContainerRequestContext requestContext) ;

    /**
     * 入库通知单的发起(信息保存,还未推送)
     * @param purchaseOrder
     */
    void warahouseAdvice(PurchaseOrder purchaseOrder, ContainerRequestContext requestContext);
    /**
     * 采购单入库通知状态的作废操作
     */
    void cancelWarahouseAdvice(PurchaseOrder purchaseOrder,ContainerRequestContext requestContext);
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
}
