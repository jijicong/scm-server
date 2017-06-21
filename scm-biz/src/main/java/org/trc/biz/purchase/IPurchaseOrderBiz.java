package org.trc.biz.purchase;

import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.purchase.PurchaseOrderAddData;
import org.trc.domain.supplier.Supplier;
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

    Pagenation<PurchaseOrder> purchaseOrderPage(PurchaseOrderForm form,Pagenation<PurchaseOrder> page) throws Exception;
    /**
     * 根据渠道用户的id查询对应的供应商
     * @param requestContext
     * @return
     * @throws Exception
     */
    List<Supplier> findSuppliersByUserId(ContainerRequestContext requestContext) throws Exception;

    /**
     * 根据供应商的code查询可采购的商品列表
     * @param supplierCode
     * @return
     * @throws Exception
     */
    Pagenation<PurchaseDetail> findPurchaseDetailBySupplierCode(String supplierCode,  ItemForm form, Pagenation<PurchaseDetail> page, String skus) throws Exception;

    void savePurchaseOrder(PurchaseOrderAddData purchaseOrder, String status) throws Exception;

    void updatePurchaseOrderState(PurchaseOrder purchaseOrder) throws Exception;

    PurchaseOrder findPurchaseOrderAddDataById(Long id) throws Exception;

}
