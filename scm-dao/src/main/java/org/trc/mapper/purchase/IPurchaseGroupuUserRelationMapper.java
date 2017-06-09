package org.trc.mapper.purchase;

import org.trc.domain.purchase.PurchaseGroupUserRelation;
import org.trc.util.BaseMapper;

import java.util.Map;

/**
 * Created by sone on 2017/5/23.
 */
public interface IPurchaseGroupuUserRelationMapper extends BaseMapper<PurchaseGroupUserRelation>{

    int deleteByPurchaseGroupCode(String code);

    void updateIsValidByCode(Map<String,Object> map);

}
