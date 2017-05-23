package org.trc.biz.impl.supplier;

import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.supplier.ISupplierApplyBiz;
import org.trc.domain.supplier.AuditLog;
import org.trc.domain.supplier.SupplierApplyAudit;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.enums.AuditStatusEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.SupplierException;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.service.supplier.IAuditLogService;
import org.trc.service.supplier.ISupplierApplyAuditService;
import org.trc.service.supplier.ISupplierBrandService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;

import java.util.*;

/**
 * Created by hzqph on 2017/5/12.
 */
@Service("supplierApplyBiz")
public class SupplierApplyBiz implements ISupplierApplyBiz {

    private final static Logger log = LoggerFactory.getLogger(SupplierApplyBiz.class);
    @Autowired
    private ISupplierApplyAuditService supplierApplyAuditService;
    @Autowired
    private ISupplierBrandService supplierBrandService;
    @Autowired
    private IAuditLogService auditLogService;
    @Override
    public Pagenation<SupplierApplyAudit> supplierApplyAuditPage(Pagenation<SupplierApplyAudit> page, SupplierApplyForm queryModel) throws Exception {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("supplierName", queryModel.getSupplierName());
        map.put("contact", queryModel.getContact());
        map.put("supplierCode", queryModel.getSupplierCode());
        map.put("status", queryModel.getStatus());
        map.put("startTime", queryModel.getStartDate());
        map.put("endTime", queryModel.getEndDate());
        List<SupplierApplyAudit> list = supplierApplyAuditService.selectList(map);
        //如果查询列表不为空，查询各个供应商下面代理的品牌
        if(list!=null&&!list.isEmpty()&&list.size()>0){
            list=handleBrandsStr(list);
        }
        int count = supplierApplyAuditService.selectCount(map);
        page.setTotalCount(count);
        page.setResult(list);
        return page;
    }

    @Override
    public SupplierApplyAudit selectOneById(Long id) throws Exception {
        AssertUtil.notNull(id,"根据ID查询供应商审核信息,参数ID不能为空");
        SupplierApplyAudit supplierApplyAudit = supplierApplyAuditService.selectOneById(id);
        if (null == supplierApplyAudit) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询供应商审核信息明细为空").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_QUERY_EXCEPTION,msg);
        }
        List<SupplierApplyAudit> list=new ArrayList<>();
        list.add(supplierApplyAudit);
        list=handleBrandsStr(list);
        return list.get(0);
    }

    @Override
    public void auditSupplierApply(SupplierApplyAudit supplierApplyAudit) throws Exception {
        AssertUtil.notNull(supplierApplyAudit.getId(),"根据ID更新供应商审核信息,参数ID不能为空");
        SupplierApplyAudit updateSupplierApplyAudit =new SupplierApplyAudit();
        updateSupplierApplyAudit.setId(supplierApplyAudit.getId());
        updateSupplierApplyAudit.setStatus(supplierApplyAudit.getStatus());
        updateSupplierApplyAudit.setAuditOpinion(supplierApplyAudit.getAuditOpinion());
        updateSupplierApplyAudit.setUpdateTime(Calendar.getInstance().getTime());
        int updateFlag= supplierApplyAuditService.updateByPrimaryKeySelective(updateSupplierApplyAudit);
        if(updateFlag!=1){
            String msg = CommonUtil.joinStr("根据主键ID[id=", supplierApplyAudit.getId().toString(), "]更新供应商审核信息失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_UPDATE_EXCEPTION,msg);
        }
        AuditLog auditLog=new AuditLog();
        auditLog.setApplyCode(supplierApplyAudit.getApplyCode());
        auditLog.setOperation(AuditStatusEnum.queryNameByCode(supplierApplyAudit.getStatus()).getName());
        auditLog.setOperateTime(updateSupplierApplyAudit.getUpdateTime());
        int insertFlag=auditLogService.insertSelective(auditLog);
        if(insertFlag!=1){
            String msg = CommonUtil.joinStr("根据申请编号[applyCode=", supplierApplyAudit.getApplyCode().toString(), "]保存审核信息日志失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_LOG_INSERT_EXCEPTION,msg);
        }
    }

    private List<SupplierApplyAudit> handleBrandsStr(List<SupplierApplyAudit> list){
        Set<Long> supplierIdsSet=new HashSet<>();
        for (SupplierApplyAudit supplierApplyAudit :list) {
            supplierIdsSet.add(supplierApplyAudit.getSupplierId());
        }
        Long[] supplierIdsArr=new Long[supplierIdsSet.size()];
        supplierIdsSet.toArray(supplierIdsArr);
        List<SupplierBrand> supplierBrands=supplierBrandService.selectListBySupplierIds(supplierIdsArr);
        if(supplierBrands!=null&&!supplierBrands.isEmpty()&&supplierBrands.size()>0){
            for (SupplierApplyAudit supplierApplyAudit :list) {
                StringBuilder brandsStr=new StringBuilder();
                for (SupplierBrand supplierBrand:supplierBrands) {
                    if(supplierBrand.getSupplierId().equals(supplierApplyAudit.getSupplierId())){
                        if(brandsStr==null||brandsStr.length()==0){
                            brandsStr.append(supplierBrand.getBrandName());
                        }else{
                            brandsStr.append(","+supplierBrand.getBrandName());
                        }
                    }
                }
                supplierApplyAudit.setBrandNames(brandsStr.toString());
            }
        }
        return list;
    }
}
