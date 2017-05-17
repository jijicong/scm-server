package org.trc.biz.impl.supplier;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.supplier.ISupplierApplyBiz;
import org.trc.domain.supplier.AuditLog;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierApply;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.enums.AuditStatusEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.SupplierException;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.service.impl.supplier.SupplierApplyService;
import org.trc.service.supplier.IAuditLogService;
import org.trc.service.supplier.ISupplierApplyService;
import org.trc.service.supplier.ISupplierBrandService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import org.trc.util.StringUtil;

import java.util.*;

/**
 * Created by hzqph on 2017/5/12.
 */
@Service("supplierApplyBiz")
public class SupplierApplyBiz implements ISupplierApplyBiz {

    private final static Logger log = LoggerFactory.getLogger(SupplierApplyBiz.class);
    @Autowired
    private ISupplierApplyService supplierApplyService;
    @Autowired
    private ISupplierBrandService supplierBrandService;
    @Autowired
    private IAuditLogService auditLogService;
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
        List<SupplierApply> list = supplierApplyService.selectList(map);
        //如果查询列表不为空，查询各个供应商下面代理的品牌
        if(list!=null&&!list.isEmpty()&&list.size()>0){
            list=handleBrandsStr(list);
        }
        int count = supplierApplyService.selectCount(map);
        page.setTotalCount(count);
        page.setResult(list);
        return page;
    }

    @Override
    public SupplierApply selectOneById(Long id) throws Exception {
        AssertUtil.notNull(id,"根据ID查询供应商审核信息,参数ID不能为空");
        SupplierApply supplierApply=supplierApplyService.selectOneById(id);
        if (null == supplierApply) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询供应商审核信息明细为空").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_QUERY_EXCEPTION,msg);
        }
        List<SupplierApply> list=new ArrayList<>();
        list.add(supplierApply);
        list=handleBrandsStr(list);
        return list.get(0);
    }

    @Override
    public void auditSupplierApply(SupplierApply supplierApply) throws Exception {
        AssertUtil.notNull(supplierApply.getId(),"根据ID更新供应商审核信息,参数ID不能为空");
        SupplierApply updateSupplierApply=new SupplierApply();
        updateSupplierApply.setId(supplierApply.getId());
        updateSupplierApply.setStatus(supplierApply.getStatus());
        updateSupplierApply.setAuditOpinion(supplierApply.getAuditOpinion());
        updateSupplierApply.setUpdateTime(Calendar.getInstance().getTime());
        int updateFlag=supplierApplyService.updateByPrimaryKeySelective(updateSupplierApply);
        if(updateFlag!=1){
            String msg = CommonUtil.joinStr("根据主键ID[id=", supplierApply.getId().toString(), "]更新供应商审核信息失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_UPDATE_EXCEPTION,msg);
        }
        AuditLog auditLog=new AuditLog();
        auditLog.setApplyCode(supplierApply.getApplyCode());
        auditLog.setOperation(AuditStatusEnum.queryNameByCode(supplierApply.getStatus()).getName());
        auditLog.setOperateTime(updateSupplierApply.getUpdateTime());
        int insertFlag=auditLogService.insertSelective(auditLog);
        if(insertFlag!=1){
            String msg = CommonUtil.joinStr("根据申请编号[applyCode=", supplierApply.getApplyCode().toString(), "]保存审核信息日志失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_LOG_INSERT_EXCEPTION,msg);
        }
    }

    private List<SupplierApply> handleBrandsStr(List<SupplierApply> list){
        Set<Long> supplierIdsSet=new HashSet<>();
        for (SupplierApply supplierApply:list) {
            supplierIdsSet.add(supplierApply.getSupplierId());
        }
        Long[] supplierIdsArr=new Long[supplierIdsSet.size()];
        supplierIdsSet.toArray(supplierIdsArr);
        List<SupplierBrand> supplierBrands=supplierBrandService.selectListBySupplierIds(supplierIdsArr);
        if(supplierBrands!=null&&!supplierBrands.isEmpty()&&supplierBrands.size()>0){
            for (SupplierApply supplierApply:list) {
                StringBuilder brandsStr=new StringBuilder();
                for (SupplierBrand supplierBrand:supplierBrands) {
                    if(supplierBrand.getSupplierId().equals(supplierApply.getSupplierId())){
                        if(brandsStr==null||brandsStr.length()==0){
                            brandsStr.append(supplierBrand.getBrandName());
                        }else{
                            brandsStr.append(","+supplierBrand.getBrandName());
                        }
                    }
                }
                supplierApply.setBrandNames(brandsStr.toString());
            }
        }
        return list;
    }
}
