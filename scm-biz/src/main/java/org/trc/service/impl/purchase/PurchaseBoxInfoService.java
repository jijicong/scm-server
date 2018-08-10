package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseBoxInfo;
import org.trc.mapper.purchase.IPurchaseBoxInfoMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseBoxInfoService;

import javax.annotation.Resource;

/**
 * Created by hzcyn on 2018/7/25.
 */
@Service("purchaseBoxInfoService")
public class PurchaseBoxInfoService extends BaseService<PurchaseBoxInfo,Long> implements IPurchaseBoxInfoService {

    @Resource
    private IPurchaseBoxInfoMapper purchaseBoxInfoMapper;
}
