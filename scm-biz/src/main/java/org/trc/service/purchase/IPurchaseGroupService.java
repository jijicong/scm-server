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

    List<PurchaseGroup> selectPurchaseGroupNames(String[] strs);

    /**
     * 对采购组的新增或者修改，查询当前的新增是否还有效
     * @param strs
     * @return
     */
    List<UserAccreditInfo> selectInvalidUser(String [] strs);

    /**
     * 查询当前userid下可用的采购组员的数量
     * @param strs
     * @return
     */
    Integer selectUserWithPurchaseNum(String [] strs);

}
