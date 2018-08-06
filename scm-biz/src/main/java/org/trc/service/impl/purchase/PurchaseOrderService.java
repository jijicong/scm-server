package org.trc.service.impl.purchase;

import org.springframework.stereotype.Service;
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.supplier.Supplier;
import org.trc.mapper.purchase.IPurchaseOrderMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.purchase.IPurchaseOrderService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/5/25.
 */
@Service("purchaseOrderService")
public class PurchaseOrderService extends BaseService<PurchaseOrder,Long> implements IPurchaseOrderService {

    @Resource
    private IPurchaseOrderMapper purchaseOrderMapper;

    @Override
    public List<Supplier> findSuppliersByChannelCode(String channelCode, String supplierName) {

        return purchaseOrderMapper.findSuppliersByChannelCode(channelCode, supplierName);

    }

    @Override
    public List<PurchaseDetail> selectItemsBySupplierCode(Map<String, Object> map) {
        return purchaseOrderMapper.selectItemsBySupplierCode(map);
    }

    @Override
    public int selectCountItems(Map<String, Object> map) {
        return purchaseOrderMapper.selectCountItems(map);
    }

    @Override
    public List<PurchaseDetail> selectAllCategory(List<Long> categoryIds) {
        return purchaseOrderMapper.selectAllCategory(categoryIds);
    }

    @Override
    public int selectCountItemsForSupplier(Map<String, Object> map) {
        return purchaseOrderMapper.selectCountItemsForSupplier(map);
    }

    @Override
    public int selectItemsBySupplierCodeCount(Map<String, Object> map) {
        return purchaseOrderMapper.selectItemsBySupplierCodeCount(map);
    }

    @Override
    public List<PurchaseDetail> selectItemsBySupplierCodeCheck(Map<String, Object> map) {
        return purchaseOrderMapper.selectItemsBySupplierCodeCheck(map);
    }


}
