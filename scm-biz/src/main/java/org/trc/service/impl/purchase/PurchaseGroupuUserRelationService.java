package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseGroupUserRelation;
import org.trc.mapper.purchase.IPurchaseGroupuUserRelationMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseGroupuUserRelationService;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by sone on 2017/5/23.
 */
@Service("purchaseGroupuUserRelationService")
public class PurchaseGroupuUserRelationService extends BaseService<PurchaseGroupUserRelation,Long> implements IPurchaseGroupuUserRelationService{

    @Resource
    private IPurchaseGroupuUserRelationMapper ipurchaseGroupuUserRelationMapper;
    @Override
    public int deleteByPurchaseGroupCode(String code) {
        return ipurchaseGroupuUserRelationMapper.deleteByPurchaseGroupCode(code);
    }

    @Override
    public void updateIsValidByCode(Map<String, Object> map) {
        ipurchaseGroupuUserRelationMapper.updateIsValidByCode(map);
    }
}
