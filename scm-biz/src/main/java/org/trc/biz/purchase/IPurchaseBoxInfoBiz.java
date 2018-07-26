package org.trc.biz.purchase;

import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseBoxInfo;
import org.trc.domain.purchase.PurchaseBoxInfoVO;

import java.util.List;

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

    /**
     * 获取包装方式
     * @return
     */
    List<Dict> findPackingType();

    /**
     * 获取装箱信息
     * @param code
     * @return
     */
    List<PurchaseBoxInfo> findPackingBoxInfo(String code);
}
