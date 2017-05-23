package org.trc.biz.purchase;

import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.form.purchase.PurchaseGroupForm;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by sone on 2017/5/19.
 */
public interface IPurchaseGroupBiz {

    Pagenation<PurchaseGroup> purchaseGroupPage(PurchaseGroupForm form , Pagenation<PurchaseGroup> page)throws Exception;

    void updatePurchaseStatus(PurchaseGroup purchaseGroup) throws Exception;

    void  savePurchaseGroup(PurchaseGroup purchaseGroup) throws Exception;

    PurchaseGroup findPurchaseByName(String name) throws Exception;

    PurchaseGroup findPurchaseById(Long id) throws Exception;

    PurchaseGroup findPurchaseGroupByCode(String code) throws Exception;

    void updatePurchaseGroup(PurchaseGroup purchaseGroup) throws Exception;
    /**
     * 查询该采购组，对应的无效状态的成员
     * @param id
     * @return
     * @throws Exception
     */
    List<UserAccreditInfo> findPurchaseGroupMemberStateById(Long id) throws Exception;
}
