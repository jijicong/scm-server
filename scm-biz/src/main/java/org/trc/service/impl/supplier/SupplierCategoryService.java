package org.trc.service.impl.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.supplier.SupplierCategory;
import org.trc.domain.supplier.SupplierCategoryExt;
import org.trc.mapper.supplier.ISupplierCategoryMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.supplier.ISupplierCategoryService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/6.
 */
@Service("supplierCategoryService")
public class SupplierCategoryService extends BaseService<SupplierCategory, Long> implements ISupplierCategoryService {

    @Autowired
    private ISupplierCategoryMapper supplierCategoryMapper;

    @Override
    public List<SupplierCategoryExt> selectSupplierCategorys(String supplierCode) throws Exception{
        return supplierCategoryMapper.selectSupplierCategorys(supplierCode);
    }

    @Override
    public Integer updateSupplerCategory(List<SupplierCategory> list) throws Exception {
        return supplierCategoryMapper.updateSupplerCategory(list);
    }


    /**
     * 停用时批量更新关联表isValid字段
     *
     * @param isValid
     * @param categoryId
     * @return
     */
    @Override
    public Integer updateSupplierCategoryIsValid(String isValid, Long categoryId) throws Exception {
        return supplierCategoryMapper.updateSupplierCategoryIsValid(isValid ,categoryId);
    }

}
