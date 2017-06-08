package org.trc.service.purchase;

import org.trc.domain.purchase.PurchaseGroupUserRelation;
import org.trc.service.IBaseService;

import java.util.Map;

/**
 * Created by sone on 2017/5/23.
 */
public interface IPurchaseGroupuUserRelationService extends IBaseService<PurchaseGroupUserRelation,Long>{

    int deleteByPurchaseGroupCode(String code);

    /**
     * 根据采购组编码更新关联表的有效状态
     * @param map
     */
    void updateIsValidByCode(Map<String,Object> map);
}
