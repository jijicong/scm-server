package org.trc.biz.supplier;

import org.trc.domain.supplier.Certificate;
import org.trc.domain.supplier.Supplier;
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
    public Pagenation<Supplier> SupplierPage(SupplierForm form, Pagenation<Supplier> page) throws Exception;

    /**
     * 查询供应商列表
     * @return
     * @throws Exception
     */
    public List<Supplier> querySuppliers(SupplierForm SupplierForm) throws Exception;

    /**
     * 保存供应商
     * @param Supplier
     * @param certificate
     * @return
     */
    public int saveSupplier(Supplier Supplier, Certificate certificate) throws Exception;

    /**
     * 修改供应商
     * @param Supplier
     * @param id
     * @return
     * @throws Exception
     */
    public int updateSupplier(Supplier Supplier, Long id) throws Exception;

    /**
     *根据主键查询供应商
     * @param id
     * @return
     */
    public Supplier findSupplierById(Long id) throws Exception;
    
    
}
