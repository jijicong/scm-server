package org.trc.mapper.purchase;

import org.trc.domain.purchase.PurchaseGroupUserRelation;
import org.trc.util.BaseMapper;

/**
 * Created by sone on 2017/5/23.
 */
public interface IPurchaseGroupuUserRelationMapper extends BaseMapper<PurchaseGroupUserRelation>{

    int deleteByPurchaseGroupCode(String code);

}
