package org.trc.biz.impl.supplier;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.trc.biz.supplier.ISupplierApplyBiz;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.supplier.*;
import org.trc.enums.AuditStatusEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.CategoryException;
import org.trc.exception.SupplierException;
import org.trc.form.supplier.SupplierApplyAuditForm;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.service.supplier.*;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.*;

/**
 * Created by hzqph on 2017/5/12.
 */
@Service("supplierApplyBiz")
public class SupplierApplyBiz implements ISupplierApplyBiz {

    private Logger log = LoggerFactory.getLogger(SupplierApplyBiz.class);
    private final static String SUPPLIER_APPLY_CODE_EX_NAME = "GYSSQ";//供应商申请
    private final static int SUPPLIER_APPLY_CODE_LENGTH = 3;
    @Autowired
    private ISupplierApplyAuditService supplierApplyAuditService;
    @Autowired
    private ISupplierBrandService supplierBrandService;
    @Autowired
    private IAuditLogService auditLogService;
    @Autowired
    private ISupplierApplyService supplierApplyService;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private ISupplierService supplierService;

    @Override
    public Pagenation<SupplierApplyAudit> supplierApplyAuditPage(Pagenation<SupplierApplyAudit> page, SupplierApplyAuditForm queryModel) throws Exception {
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
        if (list != null && !list.isEmpty() && list.size() > 0) {
            list = handleAuditBrandsStr(list);
        }
        int count = supplierApplyAuditService.selectCount(map);
        page.setTotalCount(count);
        page.setResult(list);
        return page;
    }

    @Override
    public SupplierApplyAudit selectOneById(Long id) throws Exception {
        AssertUtil.notNull(id, "根据ID查询供应商审核信息,参数ID不能为空");
        SupplierApplyAudit supplierApplyAudit = supplierApplyAuditService.selectOneById(id);
        if (null == supplierApplyAudit) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询供应商审核信息明细为空").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_AUDIT_QUERY_EXCEPTION, msg);
        }
        List<SupplierApplyAudit> list = new ArrayList<>();
        list.add(supplierApplyAudit);
        list = handleAuditBrandsStr(list);
        return list.get(0);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void auditSupplierApply(SupplierApplyAudit supplierApplyAudit,ContainerRequestContext requestContext) throws Exception {
        AssertUtil.notNull(supplierApplyAudit.getId(), "根据ID更新供应商审核信息,参数ID不能为空");
        String userId= (String) requestContext.getProperty("userId");
        SupplierApplyAudit updateSupplierApplyAudit = new SupplierApplyAudit();
        updateSupplierApplyAudit.setId(supplierApplyAudit.getId());
        updateSupplierApplyAudit.setStatus(supplierApplyAudit.getStatus());
        updateSupplierApplyAudit.setAuditOpinion(supplierApplyAudit.getAuditOpinion());
        updateSupplierApplyAudit.setUpdateTime(Calendar.getInstance().getTime());
        int updateFlag = supplierApplyAuditService.updateByPrimaryKeySelective(updateSupplierApplyAudit);
        if (updateFlag != 1) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", supplierApplyAudit.getId().toString(), "]更新供应商审核信息失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_AUDIT_UPDATE_EXCEPTION, msg);
        }
        AuditLog auditLog = new AuditLog();
        auditLog.setApplyCode(supplierApplyAudit.getApplyCode());
        auditLog.setOperation(AuditStatusEnum.queryNameByCode(supplierApplyAudit.getStatus()).getName());
        if(!StringUtils.isBlank(userId)){
            auditLog.setOperator(userId);
        }
        auditLog.setOperateTime(updateSupplierApplyAudit.getUpdateTime());
        int insertFlag = auditLogService.insertSelective(auditLog);
        if (insertFlag != 1) {
            String msg = CommonUtil.joinStr("根据申请编号[applyCode=", supplierApplyAudit.getApplyCode().toString(), "]保存审核信息日志失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_AUDIT_LOG_INSERT_EXCEPTION, msg);
        }
    }

    @Override
    public Pagenation<SupplierApply> supplierApplyPage(Pagenation<SupplierApply> page, SupplierApplyForm queryModel, ContainerRequestContext requestContext) throws Exception {
        UserAccreditInfo userAccreditInfo = (UserAccreditInfo) requestContext.getProperty("userAccreditInfo");
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("supplierName", queryModel.getSupplierName());
        map.put("contact", queryModel.getContact());
        map.put("supplierCode", queryModel.getSupplierCode());
        map.put("status", queryModel.getStatus());
        map.put("supplierKindCode", queryModel.getSupplierKindCode());
        map.put("startTime", queryModel.getStartDate());
        map.put("endTime", queryModel.getEndDate());
        map.put("channelId", userAccreditInfo.getChannelId());
        List<SupplierApply> list = supplierApplyService.selectList(map);
        //如果查询列表不为空，查询各个供应商下面代理的品牌
        if (list != null && !list.isEmpty() && list.size() > 0) {
            list = handleApplyBrandsStr(list);
        }
        for (SupplierApply supplierApply : list) {
            if (supplierApply.getStatus().equals(AuditStatusEnum.HOLD.getCode()) || supplierApply.getStatus().equals(AuditStatusEnum.REJECT.getCode())) {
                supplierApply.setDeleteAuth(ZeroToNineEnum.ONE.getCode());
                if (supplierApply.getSupplierStatus().equals(ZeroToNineEnum.ONE.getCode())) {
                    supplierApply.setUpdateAuth(ZeroToNineEnum.ONE.getCode());
                }
            }
        }
        int count = supplierApplyService.selectCount(map);
        page.setTotalCount(count);
        page.setResult(list);
        return page;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveSupplierApply(SupplierApply supplierApply, ContainerRequestContext requestContext) throws Exception {
        AssertUtil.notNull(supplierApply, "保存供应商申请信息，申请信息不能为空");
        UserAccreditInfo userAccreditInfo = (UserAccreditInfo) requestContext.getProperty("userAccreditInfo");
        //1.验证这个供应商是否已经经过申请2.供应商是否已经失效
        Supplier validateSupplier = supplierService.selectByPrimaryKey(supplierApply.getSupplierId());
        if (validateSupplier.getIsValid().equals(ZeroToNineEnum.ZERO.getCode())) {
            String msg = "该供应商已经被禁用无法申请，supplierId：" + supplierApply.getSupplierId();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_SAVE_EXCEPTION, msg);
        }
        //2.验证供应商是否已经经过申请
        Example example = new Example(SupplierApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("supplierId", supplierApply.getSupplierId());
        criteria.andEqualTo("channelId", userAccreditInfo.getChannelId());
        List<String> statusList = new ArrayList<>();
        statusList.add(ZeroToNineEnum.ONE.getCode());
        statusList.add(ZeroToNineEnum.TWO.getCode());
        criteria.andIn("status", statusList);
        List<SupplierApply> supplierApplyList = supplierApplyService.selectByExample(example);
        if (!AssertUtil.CollectionIsEmpty(supplierApplyList)) {
            String msg = "该供应商已经被申请，无法继续申请，supplierId：" + supplierApply.getSupplierId();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_SAVE_EXCEPTION, msg);
        }
        SupplierApply insert = new SupplierApply();
        //页面中所传id实际为供应商id
        insert.setSupplierId(supplierApply.getSupplierId());
        insert.setSupplierCode(supplierApply.getSupplierCode());
        insert.setAuditOpinion(supplierApply.getAuditOpinion());
        insert.setDescription(supplierApply.getDescription());
        insert.setStatus(supplierApply.getStatus());
        insert.setChannelId(userAccreditInfo.getChannelId());
        insert.setChannelCode(userAccreditInfo.getChannelCode());
        insert.setCreateOperator(userAccreditInfo.getUserId());
        ParamsUtil.setBaseDO(insert);
        insert.setApplyCode(serialUtilService.generateCode(SUPPLIER_APPLY_CODE_LENGTH, SUPPLIER_APPLY_CODE_EX_NAME, DateUtils.dateToCompactString(insert.getCreateTime())));
        try {
            supplierApplyService.insertSelective(insert);
        } catch (Exception e) {
            log.error(e.getMessage());
            String msg = CommonUtil.joinStr("保存供应商申请信息", JSON.toJSONString(insert), "到数据库失败").toString();
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_SAVE_EXCEPTION, msg);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteSupplierApply(Long supplierApplyId) throws Exception {
        AssertUtil.notNull(supplierApplyId, "供应商申请删除，主键不能为空");
        SupplierApply supplierApply = new SupplierApply();
        supplierApply.setId(supplierApplyId);
        supplierApply.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        supplierApply.setUpdateTime(Calendar.getInstance().getTime());
        try {
            supplierApplyService.updateByPrimaryKeySelective(supplierApply);
        } catch (Exception e) {
            log.error(e.getMessage());
            String msg = CommonUtil.joinStr("删除供应商申请信息", JSON.toJSONString(supplierApply), "失败").toString();
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_DELETE_EXCEPTION, msg);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSupplierApply(SupplierApply supplierApply) throws Exception {
        AssertUtil.notNull(supplierApply, "供应商申请申请信息，申请信息不能为空");
        SupplierApply update = new SupplierApply();
        update.setId(supplierApply.getId());
        update.setDescription(supplierApply.getDescription());
        update.setStatus(supplierApply.getStatus());
        update.setUpdateTime(Calendar.getInstance().getTime());
        int count = supplierApplyService.updateByPrimaryKeySelective(update);
        if (count < 1) {
            String msg = CommonUtil.joinStr("更新供应商申请信息", JSON.toJSONString(supplierApply), "失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_UPDATE_EXCEPTION, msg);
        }
    }

    @Override
    public SupplierApply selectSupplierApplyById(Long id) throws Exception {
        AssertUtil.notNull(id, "供应商申请查询，供应商主键不能为空");
        SupplierApply supplierApply = new SupplierApply();
        supplierApply.setId(id);
        supplierApply = supplierApplyService.selectOne(supplierApply);
        if (null == supplierApply) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询供应商申请为空").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_QUERY_EXCEPTION, msg);
        }
        return supplierApply;
    }


    private List<SupplierApplyAudit> handleAuditBrandsStr(List<SupplierApplyAudit> list) {
        Set<Long> supplierIdsSet = new HashSet<>();
        for (SupplierApplyAudit supplierApplyAudit : list) {
            supplierIdsSet.add(supplierApplyAudit.getSupplierId());
        }
        Long[] supplierIdsArr = new Long[supplierIdsSet.size()];
        supplierIdsSet.toArray(supplierIdsArr);
        List<SupplierBrand> supplierBrands = supplierBrandService.selectListBySupplierIds(supplierIdsArr);
        if (supplierBrands != null && !supplierBrands.isEmpty() && supplierBrands.size() > 0) {
            for (SupplierApplyAudit supplierApplyAudit : list) {
                StringBuilder brandsStr = new StringBuilder();
                for (SupplierBrand supplierBrand : supplierBrands) {
                    if (supplierBrand.getSupplierId().equals(supplierApplyAudit.getSupplierId())) {
                        if (brandsStr == null || brandsStr.length() == 0) {
                            brandsStr.append(supplierBrand.getBrandName());
                        } else {
                            brandsStr.append("," + supplierBrand.getBrandName());
                        }
                    }
                }
                supplierApplyAudit.setBrandNames(brandsStr.toString());
            }
        }
        return list;
    }

    private List<SupplierApply> handleApplyBrandsStr(List<SupplierApply> list) {
        Set<Long> supplierIdsSet = new HashSet<>();
        for (SupplierApply supplierApply : list) {
            supplierIdsSet.add(supplierApply.getSupplierId());
        }
        Long[] supplierIdsArr = new Long[supplierIdsSet.size()];
        supplierIdsSet.toArray(supplierIdsArr);
        List<SupplierBrand> supplierBrands = supplierBrandService.selectListBySupplierIds(supplierIdsArr);
        if (supplierBrands != null && !supplierBrands.isEmpty() && supplierBrands.size() > 0) {
            for (SupplierApply supplierApply : list) {
                StringBuilder brandsStr = new StringBuilder();
                for (SupplierBrand supplierBrand : supplierBrands) {
                    if (supplierBrand.getSupplierId().equals(supplierApply.getSupplierId())) {
                        if (brandsStr == null || brandsStr.length() == 0) {
                            brandsStr.append(supplierBrand.getBrandName());
                        } else {
                            brandsStr.append("," + supplierBrand.getBrandName());
                        }
                    }
                }
                supplierApply.setBrandNames(brandsStr.toString());
            }
        }
        return list;
    }
}
