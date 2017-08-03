package org.trc.biz.purchase;

import org.trc.domain.purchase.PurchaseDetail;

import java.util.List;

/**
 * Created by sone on 2017/5/25.
 */
public interface IPurchaseDetailBiz {

    List<PurchaseDetail> purchaseDetailList(Long purchaseId) throws Exception;

    /**
     * 根据采购组编码查询采购商品信息，用于页面的详情展示
     * @param purchaseOrderCode
     * @return
     */
    List<PurchaseDetail> purchaseDetailListByPurchaseCode(String purchaseOrderCode) throws Exception;
}
