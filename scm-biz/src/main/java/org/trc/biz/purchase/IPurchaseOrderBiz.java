package org.trc.biz.purchase;

import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.form.purchase.ItemForm;
import org.trc.util.AppResult;
import org.trc.util.Pagenation;

import javax.ws.rs.BeanParam;
import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
public interface IPurchaseOrderBiz {
    /**
     * 根据渠道用户的id查询对应的供应商
     * @param userId
     * @return
     * @throws Exception
     */
    List<Supplier> findSuppliersByUserId(String userId) throws Exception;

    /**
     * 根据供应商的code查询可采购的商品列表
     * @param supplierCode
     * @return
     * @throws Exception
     */
    Pagenation<PurchaseDetail> findPurchaseDetailBySupplierCode(String supplierCode,  ItemForm form, Pagenation<PurchaseDetail> page) throws Exception;

    void savePurchaseOrder(PurchaseOrder purchaseOrder, String userId) throws Exception;
}
