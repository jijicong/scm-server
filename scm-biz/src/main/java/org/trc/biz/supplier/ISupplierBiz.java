package org.trc.biz.supplier;

import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.*;
import org.trc.form.supplier.SupplierChannelRelationForm;
import org.trc.form.supplier.SupplierExt;
import org.trc.form.supplier.SupplierForm;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
public interface ISupplierBiz {

    /**
     * 供应商分页查询
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<Supplier> supplierPage(SupplierForm form, Pagenation<Supplier> page) throws Exception;

    /**
     * 供应商分页查询ES
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<Supplier> supplierPageES(SupplierForm form, Pagenation<Supplier> page) throws Exception;

    /**
     * 申请供应商时的分页查询
     * @param page
     * @param aclUserAccreditInfo
     * @param form
     * @return
     * @throws Exception
     */
    Pagenation<Supplier> supplierPage(Pagenation<Supplier> page, AclUserAccreditInfo aclUserAccreditInfo, SupplierForm form) throws Exception;

    /**
     * 查询供应商列表
     * @return
     * @throws Exception
     */
    List<Supplier> querySuppliers(SupplierForm SupplierForm) throws Exception;

    /**
     * 保存供应商
     * @param supplier
     * @param certificate
     * @param supplierCategory
     * @param supplierBrand
     * @param supplierFinancialInfo
     * @param supplierAfterSaleInfo
     * @throws Exception
     */
    void saveSupplier(Supplier supplier, Certificate certificate, SupplierCategory supplierCategory, SupplierBrand supplierBrand,
                      SupplierFinancialInfo supplierFinancialInfo, SupplierAfterSaleInfo supplierAfterSaleInfo, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 更新供应商
     * @param supplier
     * @param certificate
     * @param supplierCategory
     * @param supplierBrand
     * @param supplierFinancialInfo
     * @param supplierAfterSaleInfo
     * @throws Exception
     */
    void updateSupplier(Supplier supplier, Certificate certificate, SupplierCategory supplierCategory, SupplierBrand supplierBrand,
                      SupplierFinancialInfo supplierFinancialInfo, SupplierAfterSaleInfo supplierAfterSaleInfo, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     * 查询供应商代理分类列表
     * @param supplierCode
     * @return
     * @throws Exception
     */
    List<SupplierCategoryExt> querySupplierCategory(String supplierCode) throws Exception;

    /**
     * 查询供应商代理品牌列表
     * @param supplierCode
     * @return
     * @throws Exception
     */
    List<SupplierBrandExt> querySupplierBrand(String supplierCode) throws Exception;

    /**
     * 查询供应商信息
     * @param supplierCode
     * @return
     * @throws Exception
     */
    SupplierExt querySupplierInfo(String supplierCode) throws Exception;

    /**
     * 查询供应商渠道关系
     * @param form
     * @return
     * @throws Exception
     */
    List<SupplierChannelRelationExt> queryChannelRelation(SupplierChannelRelationForm form) throws Exception;

    /**
     * 启用/停用
     * @param isValid
     * @throws Exception
     */
    void updateValid(Long id, String isValid, AclUserAccreditInfo aclUserAccreditInfo) throws Exception;

    /**
     *检查分类品牌启停用状态
     * @param categoryId
     * @param brandId
     * @throws Exception
     */
    void checkCategoryBrandValidStatus(Long categoryId, Long brandId) throws Exception;
    
}
