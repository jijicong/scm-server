package org.trc.biz.purchase;

import org.trc.domain.impower.AclUserAccreditInfo;
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
    List<AclUserAccreditInfo> findPurchaseGroupMemberStateById(Long id) throws Exception;

    /**
     * 查询采购组列表
     * @return
     * @throws Exception
     */
    List<PurchaseGroup> findPurchaseGroupList() throws Exception;

    /**
     * 根据采购组的code查询改组的采购人员
     * @param purchaseGroupCode
     * @return
     * @throws Exception
     */
    List<AclUserAccreditInfo> findPurchaseGroupPersons(String purchaseGroupCode) throws Exception;
}
