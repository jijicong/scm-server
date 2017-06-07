package org.trc.mapper.supplier;

import org.apache.ibatis.annotations.Param;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.domain.supplier.SupplierBrandExt;
import org.trc.domain.supplier.SupplierCategoryExt;
import org.trc.util.BaseMapper;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierBrandMapper extends BaseMapper<SupplierBrand> {

    List<SupplierBrand> selectListBySupplierIds(@Param(value = "supplierIds") Long... supplierIds);

    /**
     * 根据供应商编码查询供应商品牌
     *
     * @param supplierCode
     * @return
     * @throws Exception
     */
    List<SupplierBrandExt> selectSupplierBrands(String supplierCode) throws Exception;

    /**
     * 批量更新供应商品牌
     *
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
    Integer updateSupplerBrandIsValid(@Param("isValid")String isValid,@Param("brandId") Long brandId) throws Exception;
}
