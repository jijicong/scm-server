package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseGroupUser;
import org.trc.mapper.purchase.IPurchaseGroupUserMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseGroupUserService;

import javax.annotation.Resource;

/**
 * Created by hzcyn on 2018/4/26.
 */
@Service("purchaseGroupuUserService")
public class PurchaseGroupUserService extends BaseService<PurchaseGroupUser, Long> implements IPurchaseGroupUserService {

    @Resource
    private IPurchaseGroupUserMapper purchaseGroupUserMapper;
}
