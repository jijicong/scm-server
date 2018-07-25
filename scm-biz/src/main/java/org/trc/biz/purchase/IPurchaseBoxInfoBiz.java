package org.trc.biz.purchase;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseBoxInfoVO;

/**
 * Created by hzcyn on 2018/7/25.
 */
public interface IPurchaseBoxInfoBiz {

    /**
     * 暂存装箱信息
     * @param purchaseBoxInfo
     * @param aclUserAccreditInfo
     */
    void savePurchaseBoxInfo(PurchaseBoxInfoVO purchaseBoxInfoVO, String status, AclUserAccreditInfo aclUserAccreditInfo);
}
