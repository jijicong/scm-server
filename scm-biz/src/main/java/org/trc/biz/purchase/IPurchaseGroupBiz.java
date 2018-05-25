package org.trc.biz.purchase;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseGroupUser;
import org.trc.form.purchase.PurchaseGroupForm;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by sone on 2017/5/19.
 */
public interface IPurchaseGroupBiz {

    Pagenation<PurchaseGroup> purchaseGroupPage(PurchaseGroupForm form , Pagenation<PurchaseGroup> page, AclUserAccreditInfo aclUserAccreditInfo);

    void updatePurchaseStatus(PurchaseGroup purchaseGroup, AclUserAccreditInfo aclUserAccreditInfo);

    void  savePurchaseGroup(PurchaseGroup purchaseGroup, AclUserAccreditInfo aclUserAccreditInfo) ;

    PurchaseGroup findPurchaseByName(String name) ;

    PurchaseGroup findPurchaseById(Long id) ;

    PurchaseGroup findPurchaseGroupByCode(String code) ;

    void updatePurchaseGroup(PurchaseGroup purchaseGroup, AclUserAccreditInfo aclUserAccreditInfo) ;
    /**
     * 查询该采购组，对应的无效状态的成员
     * @param id
     * @return
     * @
     */
    List<AclUserAccreditInfo> findPurchaseGroupMemberStateById(Long id) ;

    /**
     * 查询采购组列表
     * @return
     * @
     */
    List<PurchaseGroup> findPurchaseGroupList(AclUserAccreditInfo aclUserAccreditInfo) ;

    /**
     * 根据采购组的code查询改组的采购人员
     * @param purchaseGroupCode
     * @return
     * @
     */
    List<AclUserAccreditInfo> findPurchaseGroupPersons(String purchaseGroupCode) ;

    /**
     * 获取所有采购组员
     * @return
     */
    List<PurchaseGroupUser> findPurchaseGroupUser(AclUserAccreditInfo aclUserAccreditInfo);

    /**
     * 根据id删除采购组员
     * @param id
     */
    void deletePurchaseCroupUserById(Long id);

    /**
     * 保存采购组员
     * @param id
     */
    void savePurchaseCroupUser(PurchaseGroupUser purchaseGroupUser, AclUserAccreditInfo aclUserAccreditInfo);
}
