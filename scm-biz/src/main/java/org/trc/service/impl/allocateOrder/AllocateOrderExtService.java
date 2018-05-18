package org.trc.service.impl.allocateOrder;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateOrderBase;
import org.trc.domain.allocateOrder.AllocateOutInOrderBase;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.util.Area;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ParamValidException;
import org.trc.form.AllocateOrder.AllocateInOrderParamForm;
import org.trc.service.allocateOrder.IAllocateInOrderService;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.allocateOrder.AllocateInOrderDetailStatusEnum;
import org.trc.enums.allocateOrder.AllocateInOrderStatusEnum;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.util.ILocationUtilService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateCheckUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service("allocateOrderExtService")
public class AllocateOrderExtService implements IAllocateOrderExtService {

    private static final String SYSTEM = "系统";
    private static final String ALLOCATE_ORDER_DESCARD = "对应调拨单被作废";

    @Autowired
    private IAclUserAccreditInfoService aclUserAccreditInfoService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IAllocateOutOrderService allocateOutOrderService;
    @Autowired
    private IAllocateInOrderService allocateInOrderService;
    @Autowired
    private IAllocateSkuDetailService allocateSkuDetailService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private ILocationUtilService locationUtilService;

    @Override
    public void setCreateOperator(String createOpertorName, Example.Criteria criteria) {
        Example example = new Example(AclUserAccreditInfo.class);
        Example.Criteria criteria2 = example.createCriteria();
        criteria2.andLike("name", "%" + createOpertorName + "%");
        List<AclUserAccreditInfo> aclUserAccreditInfoList = aclUserAccreditInfoService.selectByExample(example);
        if(!CollectionUtils.isEmpty(aclUserAccreditInfoList)){
            List<String> userIds = new ArrayList<>();
            for(AclUserAccreditInfo userAccreditInfo: aclUserAccreditInfoList){
                userIds.add(userAccreditInfo.getUserId());
            }
            criteria.andIn("createOperator", userIds);
        }
    }

    @Override
    public void setAllocateOrderOtherNames(Pagenation pagenation) {
        if(null == pagenation){
            return;
        }
        if(CollectionUtils.isEmpty(pagenation.getResult())){
            return;
        }
        List<AllocateOrderBase> allocateOrderBaseList = (List<AllocateOrderBase>)pagenation.getResult();
        Set<String> warehouseCodes = new HashSet<>();
        Set<String> operatorIds = new HashSet<>();
        for (AllocateOrderBase base : allocateOrderBaseList) {
            warehouseCodes.add(base.getInWarehouseCode());
            warehouseCodes.add(base.getOutWarehouseCode());
            operatorIds.add(base.getCreateOperator());
        }
        List<WarehouseInfo> warehouseInfoList = null;
        List<AclUserAccreditInfo> aclUserAccreditInfoList = null;
        if(warehouseCodes.size() > 0){
            Example example = new Example(WarehouseInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("code", warehouseCodes);
            warehouseInfoList = warehouseInfoService.selectByExample(example);
        }
        if(operatorIds.size() > 0){
            Example example = new Example(AclUserAccreditInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("userId", operatorIds);
            aclUserAccreditInfoList = aclUserAccreditInfoService.selectByExample(example);
        }
        for (AllocateOrderBase base : allocateOrderBaseList) {
            if(CollectionUtils.isNotEmpty(warehouseInfoList)){
                for(WarehouseInfo warehouseInfo: warehouseInfoList){
                    if(StringUtils.equals(base.getInWarehouseCode(), warehouseInfo.getCode())){
                        base.setInWarehouseName(warehouseInfo.getWarehouseName());
                        break;
                    }
                }
                for(WarehouseInfo warehouseInfo: warehouseInfoList){
                    if(StringUtils.equals(base.getOutWarehouseCode(), warehouseInfo.getCode())){
                        base.setOutWarehouseName(warehouseInfo.getWarehouseName());
                        break;
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(aclUserAccreditInfoList)){
                for(AclUserAccreditInfo userAccreditInfo: aclUserAccreditInfoList){
                    if(StringUtils.equals(base.getCreateOperator(), userAccreditInfo.getUserId())){
                        base.setCreateOperatorName(userAccreditInfo.getName());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void setIsTimeOut(Pagenation pagenation) {
        if(null == pagenation){
            return;
        }
        if(CollectionUtils.isEmpty(pagenation.getResult())){
            return;
        }
        List<AllocateOutInOrderBase> allocateOutInOrderBaseList = pagenation.getResult();

        for(AllocateOutInOrderBase allocateOutInOrderBase : allocateOutInOrderBaseList){
            if((StringUtils.equals(allocateOutInOrderBase.getIsCancel(), ZeroToNineEnum.ONE.getCode())
                    || StringUtils.equals(allocateOutInOrderBase.getIsClose(), ZeroToNineEnum.ONE.getCode())) &&
                    DateCheckUtil.checkDate(allocateOutInOrderBase.getUpdateTime())){
                allocateOutInOrderBase.setIsTimeOut(ZeroToNineEnum.ONE.getCode());
            }else if(StringUtils.equals(allocateOutInOrderBase.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode()) &&
                    StringUtils.equals(allocateOutInOrderBase.getIsCancel(), ZeroToNineEnum.ZERO.getCode()) &&
                    StringUtils.equals(allocateOutInOrderBase.getIsClose(), ZeroToNineEnum.ZERO.getCode())){
                allocateOutInOrderBase.setIsTimeOut(ZeroToNineEnum.ONE.getCode());
            }else{
                allocateOutInOrderBase.setIsTimeOut(ZeroToNineEnum.ZERO.getCode());
            }
        }
    }

    @Override
    public void updateOrderCancelInfo(AllocateOutInOrderBase allocateOutInOrderBase, String remark, boolean isClose, String status) {
        allocateOutInOrderBase.setOldStatus(allocateOutInOrderBase.getStatus());
        allocateOutInOrderBase.setStatus(status);
        if(isClose){
            allocateOutInOrderBase.setIsClose(ZeroToNineEnum.ONE.getCode());
        }else {
            allocateOutInOrderBase.setIsCancel(ZeroToNineEnum.ONE.getCode());
        }
        allocateOutInOrderBase.setUpdateTime(Calendar.getInstance().getTime());
        allocateOutInOrderBase.setMemo(remark);
        if(allocateOutInOrderBase instanceof AllocateOutOrder){
            allocateOutOrderService.updateByPrimaryKey((AllocateOutOrder)allocateOutInOrderBase);
        }else if(allocateOutInOrderBase instanceof AllocateInOrder){
            allocateInOrderService.updateByPrimaryKey((AllocateInOrder)allocateOutInOrderBase);
        }
    }

    @Override
    public void updateOrderCancelInfoExt(AllocateOutInOrderBase allocateOutInOrderBase, boolean isClose) {
        allocateOutInOrderBase.setStatus(allocateOutInOrderBase.getOldStatus());
        allocateOutInOrderBase.setOldStatus("");
        if(isClose){
            allocateOutInOrderBase.setIsClose(ZeroToNineEnum.ZERO.getCode());
        }else{
            allocateOutInOrderBase.setIsCancel(ZeroToNineEnum.ZERO.getCode());
        }
        allocateOutInOrderBase.setUpdateTime(Calendar.getInstance().getTime());
        allocateOutInOrderBase.setMemo("");
        if(allocateOutInOrderBase instanceof AllocateOutOrder){
            allocateOutOrderService.updateByPrimaryKey((AllocateOutOrder)allocateOutInOrderBase);
        }else if(allocateOutInOrderBase instanceof AllocateInOrder){
            allocateInOrderService.updateByPrimaryKey((AllocateInOrder)allocateOutInOrderBase);
        }
    }

    @Override
    public String createAllocateInOrder(AllocateInOrder allocateInOrder, String createOperator) {
    	
        String code = serialUtilService.generateCode(SupplyConstants.Serial.ALLOCATE_ORDER_IN_LENGTH, 
        		SupplyConstants.Serial.ALLOCATE_ORDER_IN_CODE,
        			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        allocateInOrder.setAllocateInOrderCode(code);
        allocateInOrder.setCreateOperator(createOperator);
        allocateInOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        allocateInOrder.setIsValid(ZeroToNineEnum.ONE.getCode());
        allocateInOrder.setStatus(AllocateInOrderStatusEnum.WAIT_OUT_FINISH.getCode().toString());
    	
        allocateInOrderService.insert(allocateInOrder);
        //记录操作日志
        logInfoService.recordLog(allocateInOrder,allocateInOrder.getId().toString(), createOperator, LogOperationEnum.CREATE.getMessage(), "",null);
        return code;
    }

    @Override
    public void discardedAllocateInOrder(String allocateOrderCode) {
        AllocateInOrderParamForm form = this.updateAllocateInOrderByCancel(allocateOrderCode, ZeroToNineEnum.TWO.getCode(), ZeroToNineEnum.ZERO.getCode(), ALLOCATE_ORDER_DESCARD);
        //记录操作日志
        logInfoService.recordLog(form.getAllocateInOrder(),form.getAllocateInOrder().getId().toString(), SYSTEM, LogOperationEnum.DISCARDED.getMessage(), ALLOCATE_ORDER_DESCARD,null);
    }

    @Override
    public String createAllocateOutOrder(AllocateOutOrder outOrder, String createOperator) {
    	
        String code = serialUtilService.generateCode(SupplyConstants.Serial.ALLOCATE_ORDER_OUT_LENGTH, 
        		SupplyConstants.Serial.ALLOCATE_ORDER_OUT_CODE,
        			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
        outOrder.setAllocateOutOrderCode(code);
        outOrder.setCreateOperator(createOperator);
        outOrder.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        outOrder.setIsValid(ZeroToNineEnum.ONE.getCode());
        outOrder.setStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode());
        
        allocateOutOrderService.insert(outOrder);
        //记录操作日志
        logInfoService.recordLog(outOrder, outOrder.getId().toString(), createOperator, LogOperationEnum.CREATE.getMessage(), "",null);
        return code;
    }

    @Override
    public void discardedAllocateOutOrder(String allocateOrderCode) {
        //更新调拨入库单状态为已取消
        AllocateOutOrder allocateOutOrder = new AllocateOutOrder();
        allocateOutOrder.setAllocateOrderCode(allocateOrderCode);
        allocateOutOrder = allocateOutOrderService.selectOne(allocateOutOrder);

        String status = allocateOutOrder.getStatus();
        if(!(StringUtils.equals(AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode(), status) ||
                StringUtils.equals(AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode(), status) ||
                StringUtils.equals(AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode(), status))){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "调拨出库通知单的状态为“待通知出库”或“出库仓接收失败”或“已取消”才能作废");
        }

        if(StringUtils.equals(AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode(), status)){
            return;
        }

        allocateOutOrder.setStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode().toString());
        allocateOutOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
        allocateOutOrder.setIsClose(ZeroToNineEnum.ZERO.getCode());
        allocateOutOrder.setOldStatus("");
        allocateOutOrderService.updateByPrimaryKey(allocateOutOrder);
        //更新调拨入库单sku状态为已取消
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateOrderCode(allocateOrderCode);
        List<AllocateSkuDetail> allocateSkuDetails = allocateSkuDetailService.select(allocateSkuDetail);

        List<AllocateSkuDetail> allocateSkuDetailsUpdate = new ArrayList<>();
        if(allocateSkuDetails != null && allocateSkuDetails.size() > 0){
            for(AllocateSkuDetail allocateSkuDetailTemp : allocateSkuDetails){
                allocateSkuDetailTemp.setOldOutStatus("");
                allocateSkuDetailTemp.setOutStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode().toString());
                allocateSkuDetailTemp.setUpdateTime(Calendar.getInstance().getTime());
                allocateSkuDetailsUpdate.add(allocateSkuDetailTemp);
            }
            allocateSkuDetailService.updateSkuDetailList(allocateSkuDetailsUpdate);
        }
        //记录操作日志
        logInfoService.recordLog(allocateOutOrder, allocateOutOrder.getId().toString(), SYSTEM, LogOperationEnum.CREATE.getMessage(), ALLOCATE_ORDER_DESCARD,null);

    }

    @Override
    public AllocateInOrderParamForm updateAllocateInOrderByCancel(String allocateOrderCode, String type, String flag, String cancelReson) {
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        allocateInOrder = allocateInOrderService.selectOne(allocateInOrder);
        AssertUtil.notNull(allocateInOrder, String.format("根据调拨单号%s查询调拨入库单信息为空", allocateInOrder));
        //当操作是作废、关闭、取消发货时校验状态是否已经是取消状态
        if(StringUtils.equals(type, ZeroToNineEnum.TWO.getCode()) || StringUtils.equals(flag, ZeroToNineEnum.ZERO.getCode())) {//作废或者关闭/取消发货
            if(StringUtils.equals(AllocateInOrderStatusEnum.CANCEL.getCode().toString(), allocateInOrder.getStatus())){
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "调拨单当前已经是取消状态！请刷新页面查看最新数据！");
            }
        }else{//当操作是取消关闭、重新发货时校验状态是否是取消状态
            if(!StringUtils.equals(AllocateInOrderStatusEnum.CANCEL.getCode().toString(), allocateInOrder.getStatus())){
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "调拨单当前不是取消状态！请刷新页面查看最新数据！");
            }
        }
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateOrderCode(allocateOrderCode);
        List<AllocateSkuDetail> allocateSkuDetailList = allocateSkuDetailService.select(allocateSkuDetail);
        AssertUtil.notEmpty(allocateSkuDetailList, String.format("根据调拨单号%s查询调拨入库单明细信息为空", allocateInOrder));
        //更新调拨入库单
        if(StringUtils.equals(type, ZeroToNineEnum.ZERO.getCode())){//关闭类型
            if(StringUtils.equals(flag, ZeroToNineEnum.ZERO.getCode())){//关闭
                allocateInOrder.setOldStatus(allocateInOrder.getStatus());
                allocateInOrder.setStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
                allocateInOrder.setIsClose(ZeroToNineEnum.ONE.getCode());
                allocateInOrder.setMemo(cancelReson);
            }else if(StringUtils.equals(flag, ZeroToNineEnum.ONE.getCode())){//取消关闭
                allocateInOrder.setStatus(allocateInOrder.getOldStatus());
                allocateInOrder.setIsClose(ZeroToNineEnum.ZERO.getCode());
            }
        }else if(StringUtils.equals(type, ZeroToNineEnum.ONE.getCode())){//取消发货类型
            if(StringUtils.equals(flag, ZeroToNineEnum.ZERO.getCode())){//取消发货
                allocateInOrder.setOldStatus(allocateInOrder.getStatus());
                allocateInOrder.setStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
                allocateInOrder.setIsCancel(ZeroToNineEnum.ONE.getCode());
                allocateInOrder.setMemo(cancelReson);
            }else if(StringUtils.equals(flag, ZeroToNineEnum.ONE.getCode())){//重新发货
                allocateInOrder.setStatus(allocateInOrder.getOldStatus());
                allocateInOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
            }
        }else if(StringUtils.equals(type, ZeroToNineEnum.TWO.getCode())){//作废类型
            allocateInOrder.setOldStatus(allocateInOrder.getStatus());
            allocateInOrder.setStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
            allocateInOrder.setMemo(cancelReson);
        }
        if(StringUtils.equals(type, ZeroToNineEnum.TWO.getCode()) || StringUtils.equals(flag, ZeroToNineEnum.ZERO.getCode())){//作废/关闭/取消发货
            for(AllocateSkuDetail detail: allocateSkuDetailList){
                detail.setOldInStatus(detail.getInStatus());
                detail.setInStatus(AllocateInOrderDetailStatusEnum.CANCEL.getCode().toString());
            }
        }else if(StringUtils.equals(flag, ZeroToNineEnum.ONE.getCode())){//取消关闭/重新发货
            for(AllocateSkuDetail detail: allocateSkuDetailList){
                detail.setInStatus(detail.getOldInStatus());
            }
        }

        allocateInOrderService.updateByPrimaryKeySelective(allocateInOrder);
        for(AllocateSkuDetail detail: allocateSkuDetailList){
            allocateSkuDetailService.updateByPrimaryKeySelective(detail);
        }

        AllocateInOrderParamForm form = new AllocateInOrderParamForm();
        form.setAllocateInOrder(allocateInOrder);
        form.setAllocateSkuDetailList(allocateSkuDetailList);
        return form;
    }

    @Override
    public void setArea(AllocateOrderBase allocateOrderBase) {
        Area area = new Area();
        if(StringUtils.isNotEmpty(allocateOrderBase.getReceiverProvince())){
            area.setCode(allocateOrderBase.getReceiverProvince());
            area = locationUtilService.selectOne(area);
            if(area != null){
                allocateOrderBase.setReceiverProvinceName(area.getProvince());
            }
        }
        if(StringUtils.isNotEmpty(allocateOrderBase.getReceiverCity())){
            area = new Area();
            area.setCode(allocateOrderBase.getReceiverCity());
            area = locationUtilService.selectOne(area);
            if(area != null){
                allocateOrderBase.setReceiverCityName(area.getCity());
            }
        }
        if(StringUtils.isNotEmpty(allocateOrderBase.getSenderProvince())){
            area = new Area();
            area.setCode(allocateOrderBase.getSenderProvince());
            area = locationUtilService.selectOne(area);
            if(area != null){
                allocateOrderBase.setSenderProvinceName(area.getProvince());
            }
        }
        if(StringUtils.isNotEmpty(allocateOrderBase.getSenderCity())){
            area = new Area();
            area.setCode(allocateOrderBase.getSenderCity());
            area = locationUtilService.selectOne(area);
            if(area != null){
                allocateOrderBase.setSenderCityName(area.getCity());
            }
        }
    }
    
    @Override
    public void setAllocateOrderWarehouseName(AllocateOrderBase baseOrder) {
        if (null == baseOrder) {
            return;
        }
        Set<String> warehouseCodes = new HashSet<>();
        if (StringUtils.isNotBlank(baseOrder.getInWarehouseCode())) {
        	warehouseCodes.add(baseOrder.getInWarehouseCode());
        }
        if (StringUtils.isNotBlank(baseOrder.getOutWarehouseCode())) {
        	warehouseCodes.add(baseOrder.getOutWarehouseCode());
        }
        List<WarehouseInfo> warehouseInfoList = new ArrayList<>();
        if (warehouseCodes.size() > 0) {
            Example example = new Example(WarehouseInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("code", warehouseCodes);
            warehouseInfoList = warehouseInfoService.selectByExample(example);
        }
        
        for (WarehouseInfo warehouseInfo: warehouseInfoList) {
            if (StringUtils.equals(baseOrder.getOutWarehouseCode(), warehouseInfo.getCode())) {
            	baseOrder.setOutWarehouseName(warehouseInfo.getWarehouseName());
            } else if (StringUtils.equals(baseOrder.getInWarehouseCode(), warehouseInfo.getCode())) {
            	baseOrder.setInWarehouseName(warehouseInfo.getWarehouseName());
            }
        }
    }


}
