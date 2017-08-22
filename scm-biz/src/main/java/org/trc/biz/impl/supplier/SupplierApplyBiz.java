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
import org.trc.biz.supplier.ISupplierApplyBiz;
import org.trc.cache.CacheEvit;
import org.trc.cache.Cacheable;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierApply;
import org.trc.domain.supplier.SupplierApplyAudit;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.enums.AuditStatusEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.SupplierException;
import org.trc.form.supplier.SupplierApplyAuditForm;
import org.trc.form.supplier.SupplierApplyForm;
import org.trc.service.config.ILogInfoService;
import org.trc.service.supplier.*;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * Created by hzqph on 2017/5/12.
 */
@Service("supplierApplyBiz")
public class SupplierApplyBiz implements ISupplierApplyBiz {

    private Logger log = LoggerFactory.getLogger(SupplierApplyBiz.class);
    private final static String SUPPLIER_APPLY_CODE_EX_NAME = "SQGYS";//供应商申请
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
    @Autowired
    private ILogInfoService logInfoService;

    @Override
    @Cacheable(key="#queryModel.toString()+#page.pageNo+#page.pageSize",isList=true)
    public Pagenation<SupplierApplyAudit> supplierApplyAuditPage(Pagenation<SupplierApplyAudit> page, SupplierApplyAuditForm queryModel) throws Exception {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("supplierName", queryModel.getSupplierName());
        map.put("contact", queryModel.getContact());
        map.put("supplierCode", queryModel.getSupplierCode());
        map.put("status", queryModel.getStatus());
        map.put("channelId",queryModel.getApplySquare());
        map.put("startTime", queryModel.getStartDate());
        if(!StringUtils.isBlank(queryModel.getEndDate())){
            map.put("endTime",DateUtils.formatDateTime(DateUtils.addDays(queryModel.getEndDate(),DateUtils.NORMAL_DATE_FORMAT,1)));
        }
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
    @CacheEvit
    public void auditSupplierApply(SupplierApplyAudit supplierApplyAudit, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(supplierApplyAudit.getId(), "根据ID更新供应商审核信息,参数ID不能为空");
        String userId =aclUserAccreditInfo.getUserId();
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
        SupplierApply supplierApply=new SupplierApply();
        supplierApply.setCreateTime(updateSupplierApplyAudit.getUpdateTime());
        //审核日志
        logInfoService.recordLog(supplierApply,supplierApplyAudit.getId().toString(),userId,AuditStatusEnum.queryNameByCode(supplierApplyAudit.getStatus()).getName(),supplierApplyAudit.getAuditOpinion(),null);
    }

    @Override
    @Cacheable(key="#queryModel.toString()+#page.pageNo+#page.pageSize+#aclUserAccreditInfo.channelId",isList=true)
    public Pagenation<SupplierApply> supplierApplyPage(Pagenation<SupplierApply> page, SupplierApplyForm queryModel, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("supplierName", queryModel.getSupplierName());
        map.put("contact", queryModel.getContact());
        map.put("supplierCode", queryModel.getSupplierCode());
        map.put("status", queryModel.getStatus());
        map.put("supplierKindCode", queryModel.getSupplierKindCode());
        map.put("startTime", queryModel.getStartDate());
        if(!StringUtils.isBlank(queryModel.getEndDate())){
            map.put("endTime",DateUtils.formatDateTime(DateUtils.addDays(queryModel.getEndDate(),DateUtils.NORMAL_DATE_FORMAT,1)));
        }
        map.put("channelId", aclUserAccreditInfo.getChannelId());
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
    @CacheEvit
    public void saveSupplierApply(SupplierApply supplierApply, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(supplierApply, "保存供应商申请信息，申请信息不能为空");
        //1.验证这个供应商是否已经经过申请2.供应商是否已经失效
        Supplier validateSupplier = supplierService.selectByPrimaryKey(supplierApply.getSupplierId());
        if (validateSupplier.getIsValid().equals(ZeroToNineEnum.ZERO.getCode())) {
            String msg = "该供应商已经被禁用无法申请，supplierId：" + supplierApply.getSupplierId();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_SAVE_EXCEPTION, "该供应商已经被禁用无法申请");
        }
        //2.验证供应商是否已经经过申请
        Example example = new Example(SupplierApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("supplierId", supplierApply.getSupplierId());
        criteria.andEqualTo("channelId", aclUserAccreditInfo.getChannelId());
        criteria.andEqualTo("isDeleted",ZeroToNineEnum.ZERO.getCode());
        List<SupplierApply> supplierApplyList = supplierApplyService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(supplierApplyList)) {
            String msg = "该供应商已存在，不能重复申请！";
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
        insert.setChannelId(aclUserAccreditInfo.getChannelId());
        insert.setChannelCode(aclUserAccreditInfo.getChannelCode());
        insert.setCreateOperator(aclUserAccreditInfo.getUserId());
        ParamsUtil.setBaseDO(insert);
        insert.setApplyCode(serialUtilService.generateCode(SUPPLIER_APPLY_CODE_LENGTH, SUPPLIER_APPLY_CODE_EX_NAME, DateUtils.dateToCompactString(insert.getCreateTime())));
        try {
            supplierApplyService.insertSelective(insert);
        } catch (Exception e) {
            log.error(e.getMessage());
            String msg = CommonUtil.joinStr("保存供应商申请信息", JSON.toJSONString(insert), "到数据库失败").toString();
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_SAVE_EXCEPTION, msg);
        }
        //供应商申请日志添加
        logInfoService.recordLog(supplierApply,insert.getId().toString(),aclUserAccreditInfo.getUserId(),AuditStatusEnum.queryNameByCode(supplierApply.getStatus()).getName(),null,ZeroToNineEnum.ZERO.getCode());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvit
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
    @CacheEvit
    public void updateSupplierApply(SupplierApply supplierApply, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(supplierApply, "更新供应商申请申请信息，申请信息不能为空");
        //1.验证这个供应商是否已经经过申请2.供应商是否已经失效
        Supplier validateSupplier = supplierService.selectByPrimaryKey(supplierApply.getSupplierId());
        if (validateSupplier.getIsValid().equals(ZeroToNineEnum.ZERO.getCode())) {
            String msg = "该供应商已经被禁用无法申请，supplierId：" + supplierApply.getSupplierId();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_SAVE_EXCEPTION, "该供应商已经被禁用无法申请");
        }
        //2.验证供应商是否已经经过申请
        Example example = new Example(SupplierApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("supplierId", supplierApply.getSupplierId());
        criteria.andEqualTo("channelId", aclUserAccreditInfo.getChannelId());
        criteria.andEqualTo("isDeleted",ZeroToNineEnum.ZERO.getCode());
        List<SupplierApply> supplierApplyList = supplierApplyService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(supplierApplyList)&&supplierApplyList.size()>1) {
            String msg = "该供应商已存在，不能重复申请！";
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_SAVE_EXCEPTION, msg);
        }
        SupplierApply update = new SupplierApply();
        update.setId(supplierApply.getId());
        update.setDescription(supplierApply.getDescription());
        update.setStatus(supplierApply.getStatus());
        update.setUpdateTime(Calendar.getInstance().getTime());
        //提交审核时间沿用创建时间,所以每次暂存和提交审核都更新创建时间.
        update.setCreateTime(Calendar.getInstance().getTime());
        if(supplierApply.getStatus().equals(AuditStatusEnum.COMMIT.getCode())){
            //提交审核之后清空原先的审核意见
            update.setAuditOpinion("");
        }
        int count = supplierApplyService.updateByPrimaryKeySelective(update);
        if (count < 1) {
            String msg = CommonUtil.joinStr("更新供应商申请信息", JSON.toJSONString(supplierApply), "失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_APPLY_UPDATE_EXCEPTION, msg);
        }
        logInfoService.recordLog(supplierApply,supplierApply.getId().toString(),aclUserAccreditInfo.getUserId(),AuditStatusEnum.queryNameByCode(supplierApply.getStatus()).getName(),null,ZeroToNineEnum.ZERO.getCode());
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
