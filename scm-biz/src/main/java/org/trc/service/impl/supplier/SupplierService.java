package org.trc.service.impl.supplier;

import org.springframework.stereotype.Service;
import org.trc.domain.supplier.Supplier;
import org.trc.mapper.supplier.ISupplierMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("supplierService")
public class SupplierService extends BaseService<Supplier, Long> implements ISupplierService {

    @Resource
    private ISupplierMapper supplierMapper;

    @Override
    public List<Supplier> selectSupplierNames(String[] strs) {
        return supplierMapper.selectSupplierNames(strs);
    }

    @Override
    public List<Supplier> selectSupplierListByApply(Long... ids) throws Exception {
        return supplierMapper.selectSupplierListByApply(ids);
    }

    @Override
    public Integer selectSupplierListCount(Long... ids) throws Exception {
        return supplierMapper.selectSupplierListCount(ids);
    }
}
