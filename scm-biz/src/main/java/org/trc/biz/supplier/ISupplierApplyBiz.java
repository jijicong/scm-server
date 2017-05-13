package org.trc.biz.supplier;

import org.trc.domain.supplier.SupplierApply;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.util.Pagenation;

/**
 * Created by hzqph on 2017/5/12.
 */
public interface ISupplierApplyBiz {

    /**
     * 供应商审核分页方法
     * @param page
     * @param queryModel
     * @return
     * @throws Exception
     */
    Pagenation<SupplierApply> supplierApplyPage(Pagenation<SupplierApply> page, SupplierApplyForm queryModel)throws Exception;
}
