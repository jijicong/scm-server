package org.trc.biz.impl.supplier;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.supplier.ISupplierApplyBiz;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierApply;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.service.impl.supplier.SupplierApplyService;
import org.trc.service.supplier.ISupplierApplyService;
import org.trc.util.Pagenation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzqph on 2017/5/12.
 */
@Service("supplierApplyBiz")
public class SupplierApplyBiz implements ISupplierApplyBiz {

    private final static Logger log = LoggerFactory.getLogger(SupplierApplyBiz.class);
    @Autowired
    private ISupplierApplyService supplierApplyService;

    @Override
    public Pagenation<SupplierApply> supplierApplyPage(Pagenation<SupplierApply> page, SupplierApplyForm queryModel) throws Exception {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("supplierName", queryModel.getSupplierName());
        map.put("contact", queryModel.getContact());
        map.put("supplierCode", queryModel.getSupplierCode());
        map.put("status", queryModel.getStatus());
        map.put("startTime", queryModel.getStartDate());
        map.put("endTime", queryModel.getEndDate());
        List<SupplierApply> list = supplierApplyService.querySupplierApplyList(map);

        int count = supplierApplyService.queryCountSupplierApply(map);
        page.setTotalCount(count);
        page.setResult(list);
        return page;
    }
}
