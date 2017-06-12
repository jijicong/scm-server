package org.trc.biz.supplier;

import org.trc.domain.supplier.SupplierApply;
import org.trc.domain.supplier.SupplierApplyAudit;
import org.trc.form.supplier.SupplierApplyAuditForm;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.util.Pagenation;

import javax.ws.rs.container.ContainerRequestContext;

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
    Pagenation<SupplierApplyAudit> supplierApplyAuditPage(Pagenation<SupplierApplyAudit> page, SupplierApplyAuditForm queryModel)throws Exception;

    /**
     * 根据supplierApplyId查询单条记录
     * @param id
     * @return
     * @throws Exception
     */
    SupplierApplyAudit selectOneById(Long id)throws Exception;

    void auditSupplierApply(SupplierApplyAudit supplierApplyAudit,ContainerRequestContext requestContext)throws  Exception;

    /**
     * 供应商申请分页方法
     * @param page
     * @param queryModel
     * @return
     * @throws Exception
     */
    Pagenation<SupplierApply> supplierApplyPage(Pagenation<SupplierApply> page, SupplierApplyForm queryModel,ContainerRequestContext requestContext)throws Exception;

    /**
     * 保存供应商申请页面
     * @param supplierApply
     * @throws Exception
     */
    void saveSupplierApply(SupplierApply supplierApply, ContainerRequestContext requestContext)throws Exception;

    /**
     * 删除供应商申请
     * @param supplierApplyId
     * @throws Exception
     */
    void deleteSupplierApply(Long supplierApplyId)throws Exception;


    void updateSupplierApply(SupplierApply supplierApply)throws Exception;

    SupplierApply selectSupplierApplyById(Long id)throws Exception;
}
