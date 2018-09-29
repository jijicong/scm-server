package org.trc.service.impl.supplier;

import org.springframework.stereotype.Service;
import org.trc.domain.supplier.Supplier;
import org.trc.mapper.supplier.ISupplierMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    public List<Supplier> selectSupplierListByApply(Map<String,Object> map) throws Exception {
        return supplierMapper.selectSupplierListByApply(map);
    }

    @Override
    public Integer selectSupplierListCount(Map<String,Object> map) throws Exception {
        return supplierMapper.selectSupplierListCount(map);
    }

    @Override
    public List<Supplier> selectSupplierByName(String name){
        return supplierMapper.selectSupplierByName(name);
    }

    /**
     * 查询所有供应商（包括停用,国内供应商,采购）
     *
     * @param channelCode
     * @return
     */
    @Override
    public List<Supplier> selectAllSuppliers(String channelCode) {
        return supplierMapper.selectAllSuppliers(channelCode);
    }

    @Override
    public Supplier selectSupplierByCode(String supplierCode) {
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(supplierCode);
        return supplierMapper.selectOne(supplier);
    }
}
