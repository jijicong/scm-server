package org.trc.service.purchase;

import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/25.
 */
public interface IPurchaseOrderService extends IBaseService<PurchaseOrder,Long>{

     List<Supplier> findSuppliersByUserId(String userId);

     List<PurchaseDetail> selectItemsBySupplierCode(Map<String, Object> map);

     int selectCountItems(Map<String, Object> map);

     /**
      * 拼接分类的全路径名 和 全ids
      * @param categoryIds
      * @return
      */
     List<PurchaseDetail> selectAllCategory(List<Long> categoryIds);

     Integer updateStateByPurchaseOrderCode(Map<String, Object> map);

}
