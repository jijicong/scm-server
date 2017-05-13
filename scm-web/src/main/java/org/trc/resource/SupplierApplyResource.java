package org.trc.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.supplier.ISupplierApplyBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Property;
import org.trc.domain.supplier.SupplierApply;
import org.trc.form.category.PropertyForm;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.util.Pagenation;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;

/**
 * Created by hzqph on 2017/5/12.
 */
@Path(SupplyConstants.Supply.ROOT)
public class SupplierApplyResource {

    @Autowired
    private ISupplierApplyBiz supplierApplyBiz;


    @GET
    @Path(SupplyConstants.Supply.SupplierApply.SUPPLIER_APPLY_PAGE)
    @Produces(MediaType.APPLICATION_JSON)
    public Pagenation<SupplierApply> supplierApplyPage(@BeanParam SupplierApplyForm form, @BeanParam Pagenation<SupplierApply> page)throws Exception{
        return supplierApplyBiz.supplierApplyPage(page,form);
    }


}
