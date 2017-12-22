package org.trc.service.supplier;

import org.trc.domain.supplier.SupplierBrand;
import org.trc.domain.supplier.SupplierBrandExt;
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

    /**
     * 根据供应商编码查询供应商品牌
     * @param supplierCode
     * @return
     * @throws Exception
     */
    List<SupplierBrandExt> selectSupplierBrands(String supplierCode) throws Exception;

    /**
     * 根据供应商编码查询供应商不同名称品牌
     *
     * @param supplierCode
     * @return
     * @throws Exception
     */
    List<SupplierBrandExt> selectSupplierBrandNames(String supplierCode) throws Exception;

    /**
     * 批量更新供应商品牌
     * @param list
     * @return
     * @throws Exception
     */
    Integer updateSupplerBrand(List<SupplierBrand> list) throws Exception;

    /**
     * 品牌停用时批量更新关联表isValid字段
     *
     * @param isValid
     * @param brandId
     * @return
     */
    Integer updateSupplerBrandIsValid(String isValid, Long brandId) throws Exception;
}
