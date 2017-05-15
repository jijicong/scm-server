package org.trc.service.supplier;

import org.trc.domain.supplier.SupplierBrand;
import org.trc.service.IBaseService;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierBrandService extends IBaseService<SupplierBrand, Long>{

    /**
     * 根据供应商id查询供应商下代理的品牌列表
     * @param supplierIds
     * @return
     */
    List<SupplierBrand> selectListBySupplierIds(Long ...supplierIds);
}
