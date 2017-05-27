package org.trc.service.purchase;

import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/19.
 */
public interface IPurchaseGroupService extends IBaseService<PurchaseGroup,Long>{

    List<UserAccreditInfo> findPurchaseGroupMemberStateById(Long id);

    List<UserAccreditInfo> selectPurchaseGroupPersons(String purchaseGroupCode);

}
