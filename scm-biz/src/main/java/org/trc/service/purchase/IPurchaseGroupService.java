package org.trc.service.purchase;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by sone on 2017/5/19.
 */
public interface IPurchaseGroupService extends IBaseService<PurchaseGroup,Long>{

    List<AclUserAccreditInfo> findPurchaseGroupMemberStateById(Long id);

    List<AclUserAccreditInfo> selectPurchaseGroupPersons(String purchaseGroupCode);

    List<PurchaseGroup> selectPurchaseGroupNames(String[] strs);

    /**
     * 对采购组的新增或者修改，查询当前的新增是否还有效
     * @param strs
     * @return
     */
    List<AclUserAccreditInfo> selectInvalidUser(String [] strs);

    /**
     * 查询当前userid下可用的采购组员的数量
     * @param strs
     * @return
     */
    Integer selectUserWithPurchaseNum(String [] strs);

}
