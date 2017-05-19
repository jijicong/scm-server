package org.trc.service.impl.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.domain.supplier.SupplierBrandExt;
import org.trc.mapper.supplier.ISupplierBrandMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierBrandService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("supplierBrandService")
public class SupplierBrandService extends BaseService<SupplierBrand, Long> implements ISupplierBrandService {

    @Autowired
    private ISupplierBrandMapper supplierBrandMapper;

    @Override
    public List<SupplierBrand> selectListBySupplierIds(Long... supplierIds) {
        return supplierBrandMapper.selectListBySupplierIds(supplierIds);
    }

    @Override
    public List<SupplierBrandExt> selectSupplierBrands(String supplierCode) throws Exception{
        return supplierBrandMapper.selectSupplierBrands(supplierCode);
    }


}
