package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.mapper.purchase.IPurchaseGroupMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseGroupService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sone on 2017/5/19.
 */
@Service("purchaseGroupService")
public class PurchaseGroupService extends BaseService<PurchaseGroup,Long> implements IPurchaseGroupService{

    @Resource
    private IPurchaseGroupMapper ipurchaseGroupMapper;
    @Override
    public List<UserAccreditInfo> findPurchaseGroupMemberStateById(Long id) {
        return ipurchaseGroupMapper.findPurchaseGroupMemberStateById(id);
    }

    @Override
    public List<UserAccreditInfo> selectPurchaseGroupPersons(String purchaseGroupCode) {
        return ipurchaseGroupMapper.selectPurchaseGroupPersons(purchaseGroupCode);
    }
}
