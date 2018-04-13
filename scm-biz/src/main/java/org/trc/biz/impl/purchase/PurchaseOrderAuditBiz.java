package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.purchase.IPurchaseOrderAuditBiz;
import org.trc.biz.purchase.IPurchaseOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.purchase.PurchaseOrderAddAudit;
import org.trc.domain.purchase.PurchaseOrderAudit;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.AuditStatusEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.PurchaseOrderAuditException;
import org.trc.form.purchase.PurchaseOrderAuditForm;
import org.trc.service.config.ILogInfoService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseOrderAuditService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.cache.PurchaseOrderCacheEvict;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sone on 2017/6/20.
 */
@Service("purchaseOrderAuditBiz")
public class PurchaseOrderAuditBiz implements IPurchaseOrderAuditBiz{

    private Logger logger = LoggerFactory.getLogger(PurchaseOrderAuditBiz.class);

    @Resource
    private IPurchaseOrderAuditService purchaseOrderAuditService;
    @Resource
    private IPurchaseOrderService iPurchaseOrderService;

    @Resource
    private IPurchaseGroupService purchaseGroupService;

    @Resource
    private IUserNameUtilService iUserNameUtilService;

    @Resource
    private ILogInfoService logInfoService;

    @Resource
    private IPurchaseOrderBiz purchaseOrderBiz;

    @Autowired
    private IWarehouseInfoService warehouseInfoService;


    /*
    采购单审核表 与 采购单表 左关联
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.PURCHASE_ORDER)
    public Pagenation<PurchaseOrderAddAudit> purchaseOrderAuditPage(PurchaseOrderAuditForm form, Pagenation<PurchaseOrderAddAudit> page, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {

        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        String  channelCode = aclUserAccreditInfo.getChannelCode(); //获得渠道的编码
        Map<String, Object> map = new HashMap<>();
        map.put("supplierName", form.getSupplierName());
        //若审核状态为已经审核，需要查询审核驳回，和审核通过的订单
        List<String>  statusStrs = new ArrayList<String>();
        String status = form.getPurchaseOrderAuditStatus();
        if(StringUtils.isBlank(status)){
            statusStrs.add(ZeroToNineEnum.ONE.getCode());
            statusStrs.add(ZeroToNineEnum.TWO.getCode());
            statusStrs.add(ZeroToNineEnum.THREE.getCode());
            map.put("auditStatus",statusStrs);
        }
        if(ZeroToNineEnum.ONE.getCode().equals(form.getPurchaseOrderAuditStatus())){
            statusStrs.add(ZeroToNineEnum.ONE.getCode());
            map.put("auditStatus",statusStrs);
        }
        if(ZeroToNineEnum.TWO.getCode().equals(form.getPurchaseOrderAuditStatus())){
            statusStrs.add(ZeroToNineEnum.TWO.getCode());
            statusStrs.add(ZeroToNineEnum.THREE.getCode());
            map.put("auditStatus",statusStrs);
        }
        map.put("purchaseOrderCode", form.getPurchaseOrderCode());
        map.put("purchaseType",form.getPurchaseType());

        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.NORMAL_DATE_FORMAT);
        if(!StringUtils.isBlank(form.getEndDate())){
            Date date = sdf.parse(form.getEndDate());
            date =DateUtils.addDays(date,1);
            form.setEndDate(sdf.format(date));
        }
        map.put("endDate",form.getEndDate());
        map.put("startDate",form.getStartDate());
        map.put("channelCode",channelCode);
        List<PurchaseOrderAddAudit> pageDateList = purchaseOrderAuditService.selectPurchaseOrderAuditList(map);
        iUserNameUtilService.handleUserName(pageDateList);
        if(CollectionUtils.isEmpty(pageDateList)){
            page.setTotalCount(0);
            return page;
        }
        //selectAssignmentWarehouseName(pageDateList);
        //selectAssignmentPurchaseGroupName(pageDateList);
        _renderPurchaseOrders(pageDateList);
        page.setResult(pageDateList);
        int count = purchaseOrderAuditService.selectCountAuditPurchaseOrder(map);
        page.setTotalCount(count);
        return page;

    }
    private void  _renderPurchaseOrders(List<PurchaseOrderAddAudit> purchaseOrderList){

        for(PurchaseOrder purchaseOrder : purchaseOrderList){
            //赋值采购组名称
            PurchaseGroup paramGroup = new PurchaseGroup();
            paramGroup.setCode(purchaseOrder.getPurchaseGroupCode());
            PurchaseGroup entityGroup = purchaseGroupService.selectOne(paramGroup);
            purchaseOrder.setPurchaseGroupName(entityGroup.getName());
            //赋值仓库名称
            WarehouseInfo warehouseInfo = new WarehouseInfo();
            warehouseInfo.setId(purchaseOrder.getWarehouseInfoId());
            WarehouseInfo entityWarehouse = warehouseInfoService.selectOne(warehouseInfo);
            purchaseOrder.setWarehouseName(entityWarehouse.getWarehouseName());

        }

    }


    //为仓库名称赋值
    private void selectAssignmentWarehouseName(List<PurchaseOrderAddAudit> purchaseOrderList)throws Exception {
        String[] warehouseArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            warehouseArray[i] = purchaseOrderList.get(i).getWarehouseCode();
        }
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("code", Arrays.asList(warehouseArray));
        List<WarehouseInfo> warehouseList = warehouseInfoService.selectByExample(example);
        if(CollectionUtils.isEmpty(warehouseList)){
            String msg = "根据仓库编码,查询仓库失败";
            logger.error(msg);
            throw  new PurchaseOrderAuditException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_AUDIT_QUERY_EXCEPTION, msg);
        }
        for (WarehouseInfo warehouse : warehouseList){
            for (PurchaseOrder purchaseOrder : purchaseOrderList){
                if(warehouse.getCode().equals(purchaseOrder.getWarehouseCode())){
                    purchaseOrder.setWarehouseName(warehouse.getWarehouseName());
                }
            }
        }
    }
    //赋值采购组名称
    private void selectAssignmentPurchaseGroupName(List<PurchaseOrderAddAudit> purchaseOrderList)throws Exception {

        String[] purchaseGroupArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            purchaseGroupArray[i] = purchaseOrderList.get(i).getPurchaseGroupCode();
        }

        List<PurchaseGroup> purchaseGroupList = purchaseGroupService.selectPurchaseGroupNames(purchaseGroupArray);
        if(CollectionUtils.isEmpty(purchaseGroupList)){
            String msg = "根据采购组编码,查询采购组失败";
            logger.error(msg);
            throw  new PurchaseOrderAuditException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_AUDIT_QUERY_EXCEPTION, msg);
        }
        for (PurchaseGroup purchaseGroup : purchaseGroupList) {
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                if(purchaseGroup.getCode().equals(purchaseOrder.getPurchaseGroupCode())){
                    purchaseOrder.setPurchaseGroupName(purchaseGroup.getName());
                }
            }
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @PurchaseOrderCacheEvict
    public void auditPurchaseOrder(PurchaseOrderAudit purchaseOrderAudit, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        //根据采购订单的编码审核采购单
        AssertUtil.notNull(purchaseOrderAudit,"根据采购订单的编码审核采购单,审核信息为空");

        PurchaseOrder purchaseOrderLog = new PurchaseOrder();
        purchaseOrderLog.setPurchaseOrderCode(purchaseOrderAudit.getPurchaseOrderCode());
        purchaseOrderLog = iPurchaseOrderService.selectOne(purchaseOrderLog);
        AssertUtil.notNull(purchaseOrderLog.getId(),"根据采购单的编码,查询采购单失败");
        String userId= aclUserAccreditInfo.getUserId();
        //审核单状态修改
        auditPurchaseOrder(purchaseOrderAudit,purchaseOrderLog,userId);

        updatePurchaseOrderStatus(purchaseOrderAudit);

        purchaseOrderBiz.cacheEvitForPurchaseOrder();

    }

    /**
     * 更改采购单的状态，并更改操作日志
     * @param purchaseOrderAudit
     */
    public void updatePurchaseOrderStatus(PurchaseOrderAudit purchaseOrderAudit){
        //审核单  2 审核通过  3.审核驳回
        //采购单  "2","审核通过" "1","审核驳回"
        //根据采购单code ， 修改采购单的状态
        Example exampleOrder = new Example(PurchaseOrder.class);
        Example.Criteria criteriaOrder = exampleOrder.createCriteria();
        criteriaOrder.andEqualTo("purchaseOrderCode",purchaseOrderAudit.getPurchaseOrderCode());
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        if(purchaseOrderAudit.getStatus().equals(ZeroToNineEnum.THREE.getCode())){
            purchaseOrder.setStatus(ZeroToNineEnum.ONE.getCode());
        }else {
            purchaseOrder.setStatus(purchaseOrderAudit.getStatus());
        }
        int count = iPurchaseOrderService.updateByExampleSelective(purchaseOrder,exampleOrder);
        //保存采购单的单据日志
        /*if(ZeroToNineEnum.ONE.getCode().equals(purchaseOrder.getStatus())){//审核驳回
            logInfoService.recordLog(purchaseOrder,purchaseOrderLog.getId().toString(),userId, AuditStatusEnum.REJECT.getName(),purchaseOrderAudit.getAuditOpinion(),ZeroToNineEnum.ZERO.getCode());
        }
        if(ZeroToNineEnum.TWO.getCode().equals(purchaseOrder.getStatus())){//审核通过
            logInfoService.recordLog(purchaseOrder,purchaseOrderLog.getId().toString(),userId, AuditStatusEnum.PASS.getName(),purchaseOrderAudit.getAuditOpinion(),ZeroToNineEnum.ZERO.getCode());
        }*/
        if (count == 0) {
            String msg = String.format("修改%s采购单状态数据库操作失败", JSON.toJSONString(purchaseOrderAudit));
            logger.error(msg);
            throw new PurchaseOrderAuditException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_AUDIT_UPDATE_EXCEPTION, msg);
        }

    }

    /**更改采购单状态，审核日志
     * @param purchaseOrderAudit
     * @param purchaseOrderLog
     * @param userId
     */
    private void auditPurchaseOrder(PurchaseOrderAudit purchaseOrderAudit,PurchaseOrder purchaseOrderLog,String userId){

        //只有审核状态为待审核，才能具有审核操作
        PurchaseOrderAudit compareAudit = new PurchaseOrderAudit();
        compareAudit.setPurchaseOrderCode(purchaseOrderAudit.getPurchaseOrderCode());
        compareAudit = purchaseOrderAuditService.selectOne(compareAudit);
        AssertUtil.notNull(compareAudit.getId(),"查询审核采购单失败!");
        if(!compareAudit.getStatus().equals(ZeroToNineEnum.ONE.getCode())){
            throw new PurchaseOrderAuditException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_AUDIT_UPDATE_EXCEPTION, "状态已更新，请返回列表页查看最新状态");
        }
        //审核驳回，检验审核意见是否为空
        if(ZeroToNineEnum.THREE.getCode().equals(purchaseOrderAudit.getStatus())){//判断是否为驳回
            AssertUtil.notBlank(purchaseOrderAudit.getAuditOpinion(),"审核驳回,审核意见不能为空");
        }
        Example exampleAudit = new Example(PurchaseOrderAudit.class);
        Example.Criteria criteria = exampleAudit.createCriteria();
        criteria.andEqualTo("purchaseOrderCode",purchaseOrderAudit.getPurchaseOrderCode());
        PurchaseOrderAudit audit = new PurchaseOrderAudit();
        audit.setStatus(purchaseOrderAudit.getStatus());
        audit.setAuditOpinion(purchaseOrderAudit.getAuditOpinion());
        audit.setUpdateTime(Calendar.getInstance().getTime());
        int count = purchaseOrderAuditService.updateByExampleSelective(audit,exampleAudit);//审核采购单，更改审核单的状态
        if (count == 0) {
            String msg = String.format("审核%s采购单数据库操作失败", JSON.toJSONString(purchaseOrderAudit));
            logger.error(msg);
            throw new PurchaseOrderAuditException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_AUDIT_UPDATE_EXCEPTION, msg);
        }
        //采购单审核的日志记录
        if(ZeroToNineEnum.THREE.getCode().equals(purchaseOrderAudit.getStatus())){
            logInfoService.recordLog(purchaseOrderLog,purchaseOrderLog.getId().toString(),userId, AuditStatusEnum.REJECT.getName(),purchaseOrderAudit.getAuditOpinion(),null);
        }
        if(ZeroToNineEnum.TWO.getCode().equals(purchaseOrderAudit.getStatus())){
            logInfoService.recordLog(purchaseOrderLog,purchaseOrderLog.getId().toString(),userId, AuditStatusEnum.PASS.getName(),purchaseOrderAudit.getAuditOpinion(),null);
        }

    }

}
