package org.trc.biz.purchase;

import org.trc.domain.supplier.Supplier;

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

}
