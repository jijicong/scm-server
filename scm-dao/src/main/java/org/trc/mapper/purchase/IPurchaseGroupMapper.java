package org.trc.mapper.purchase;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by sone on 2017/5/19.
 */
public interface IPurchaseGroupMapper extends BaseMapper<PurchaseGroup>{

    List<AclUserAccreditInfo> findPurchaseGroupMemberStateById(Long id);

    List<AclUserAccreditInfo> selectPurchaseGroupPersons(String purchaseGroupCode);

    List<PurchaseGroup> selectPurchaseGroupNames(String[] strs);

    List<AclUserAccreditInfo> selectInvalidUser(String [] strs);

   Integer selectUserWithPurchaseNum(String [] strs);

}
