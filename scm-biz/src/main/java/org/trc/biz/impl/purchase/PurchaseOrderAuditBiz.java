package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.purchase.IPurchaseDetailBiz;
import org.trc.biz.purchase.IPurchaseOrderAuditBiz;
import org.trc.domain.System.Warehouse;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseGroup;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.domain.purchase.PurchaseOrderAddAudit;
import org.trc.domain.purchase.PurchaseOrderAudit;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.PurchaseOrderAuditException;
import org.trc.exception.PurchaseOrderException;
import org.trc.form.purchase.PurchaseOrderAuditForm;
import org.trc.form.purchase.PurchaseOrderForm;
import org.trc.service.System.IWarehouseService;
import org.trc.service.purchase.IPurchaseGroupService;
import org.trc.service.purchase.IPurchaseOrderAuditService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IWarehouseService warehouseService;

    @Resource
    private IPurchaseGroupService purchaseGroupService;

    @Resource
    private IUserNameUtilService iUserNameUtilService;
    /*
    采购单审核表 与 采购单表 左关联
     */
    @Override
    public Pagenation<PurchaseOrderAddAudit> purchaseOrderAuditPage(PurchaseOrderAuditForm form, Pagenation<PurchaseOrderAddAudit> page) throws Exception {

        PageHelper.startPage(page.getPageNo(), page.getPageSize());
        Map<String, Object> map = new HashMap<>();
        map.put("supplierName", form.getSupplierName());
        map.put("auditStatus", form.getPurchaseOrderAuditStatus());
        map.put("purchaseOrderCode", form.getPurchaseOrderCode());
        map.put("purchaseType",form.getPurchaseType());
        map.put("endDate",form.getEndDate());
        map.put("startDate",form.getStartDate());
        List<PurchaseOrderAddAudit> pageDateList = purchaseOrderAuditService.selectPurchaseOrderAuditList(map);

        iUserNameUtilService.handleUserName(pageDateList);

        pageDateList = selectAssignmentWarehouseName(pageDateList);
        pageDateList = selectAssignmentPurchaseGroupName(pageDateList);
        page.setResult(pageDateList);
        int count = purchaseOrderAuditService.selectCountAuditPurchaseOrder(map);
        page.setTotalCount(count);
        return page;

    }

    //为仓库名称赋值
    private List<PurchaseOrderAddAudit> selectAssignmentWarehouseName(List<PurchaseOrderAddAudit> purchaseOrderList)throws Exception {
        String[] warehouseArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            warehouseArray[i] = purchaseOrderList.get(i).getWarehouseCode();
        }
        List<Warehouse> warehouseList = warehouseService.selectWarehouseNames(warehouseArray);
        for (Warehouse warehouse : warehouseList){
            for (PurchaseOrder purchaseOrder : purchaseOrderList){
                if(warehouse.getCode().equals(purchaseOrder.getWarehouseCode())){
                    purchaseOrder.setWarehouseName(warehouse.getName());
                }
            }
        }
        return purchaseOrderList;
    }
    //赋值采购组名称
    private List<PurchaseOrderAddAudit> selectAssignmentPurchaseGroupName(List<PurchaseOrderAddAudit> purchaseOrderList)throws Exception {

        String[] purchaseGroupArray = new String[purchaseOrderList.size()];

        for(int i = 0 ; i < purchaseOrderList.size();i++){
            purchaseGroupArray[i] = purchaseOrderList.get(i).getPurchaseGroupCode();
        }

        List<PurchaseGroup> purchaseGroupList = purchaseGroupService.selectPurchaseGroupNames(purchaseGroupArray);
        for (PurchaseGroup purchaseGroup : purchaseGroupList) {
            for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                if(purchaseGroup.getCode().equals(purchaseOrder.getPurchaseGroupCode())){
                    purchaseOrder.setPurchaseGroupName(purchaseGroup.getName());
                }
            }
        }
        return purchaseOrderList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void auditPurchaseOrder(PurchaseOrderAudit purchaseOrderAudit) throws Exception {
        //根据采购订单的编码审核采购单
        AssertUtil.notNull(purchaseOrderAudit,"根据采购订单的编码审核采购单,审核信息为空");
        if(purchaseOrderAudit.getStatus().equals(ZeroToNineEnum.THREE.getCode())){ //若是审核驳回 ,校验审核意见
            String auditOpinion = purchaseOrderAudit.getAuditOpinion();
            if(auditOpinion==null || "".equals(auditOpinion.trim())){
                String msg = String.format("审核%s采购单操作失败,%s", JSON.toJSONString(purchaseOrderAudit),"驳回意见为空");
                logger.error(msg);
                throw new PurchaseOrderAuditException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_AUDIT_UPDATE_EXCEPTION, msg);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("purchaseOrderCode",purchaseOrderAudit.getPurchaseOrderCode());
        map.put("status",purchaseOrderAudit.getStatus());
        map.put("auditOpinion",purchaseOrderAudit.getAuditOpinion());
        int count = purchaseOrderAuditService.updatePurchaseOrderByPurchase(map);
        if (count == 0) {
            String msg = String.format("审核%s采购单数据库操作失败", JSON.toJSONString(purchaseOrderAudit));
            logger.error(msg);
            throw new PurchaseOrderAuditException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_AUDIT_UPDATE_EXCEPTION, msg);
        }
        //审核单  2 审核通过  3.审核驳回
        //采购单  "2","审核通过" "1","审核驳回"
        //根据采购单code ， 修改采购单的状态
        if(purchaseOrderAudit.getStatus().equals(ZeroToNineEnum.THREE.getCode())){
            map.put("status",ZeroToNineEnum.ONE.getCode());
        }
        count = iPurchaseOrderService.updateStateByPurchaseOrderCode(map);
        if (count == 0) {
            String msg = String.format("修改%s采购单状态数据库操作失败", JSON.toJSONString(purchaseOrderAudit));
            logger.error(msg);
            throw new PurchaseOrderAuditException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_AUDIT_UPDATE_EXCEPTION, msg);
        }

    }

}
