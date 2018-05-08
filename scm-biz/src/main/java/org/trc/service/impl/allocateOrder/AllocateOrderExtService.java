package org.trc.service.impl.allocateOrder;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateInOrder;
import org.trc.domain.allocateOrder.AllocateOrderBase;
import org.trc.domain.allocateOrder.AllocateOutInOrderBase;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.service.allocateOrder.IAllocateInOrderService;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.allocateOrder.AllocateInOrderDetailStatusEnum;
import org.trc.enums.allocateOrder.AllocateInOrderStatusEnum;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateCheckUtil;
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
    public void createAllocateInOrder(AllocateInOrder allocateInOrder, String createOperator) {
        allocateInOrderService.insert(allocateInOrder);
        //记录操作日志
        logInfoService.recordLog(allocateInOrder,allocateInOrder.getId().toString(), createOperator, LogOperationEnum.CREATE.getMessage(), "",null);
    }

    @Override
    public void discardedAllocateInOrder(String allocateOrderCode) {
        //更新调拨入库单状态为已取消
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
        allocateInOrder.setIsCancel(ZeroToNineEnum.ONE.getCode());
        Example example = new Example(AllocateInOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("allocateOrderCode", allocateOrderCode);
        allocateInOrderService.updateByExampleSelective(allocateInOrder, example);
        //更新调拨入库单sku状态为已取消
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateInStatus(AllocateInOrderDetailStatusEnum.CANCEL.getCode().toString());
        Example example2 = new Example(AllocateSkuDetail.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("allocateOrderCode", allocateOrderCode);
        allocateSkuDetailService.updateByExampleSelective(allocateSkuDetail, example2);
        //记录操作日志
        logInfoService.recordLog(allocateInOrder,allocateInOrder.getId().toString(), SYSTEM, LogOperationEnum.CREATE.getMessage(), ALLOCATE_ORDER_DESCARD,null);
    }

    @Override
    public void createAllocateOutOrder(AllocateOutOrder allocateoutOrder, String createOperator) {
        allocateOutOrderService.insert(allocateoutOrder);
        //记录操作日志
        logInfoService.recordLog(allocateoutOrder, allocateoutOrder.getId().toString(), createOperator, LogOperationEnum.CREATE.getMessage(), "",null);
    }

    @Override
    public void discardedAllocateOutOrder(String allocateOrderCode) {
        //更新调拨入库单状态为已取消
        AllocateOutOrder allocateOutOrder = new AllocateOutOrder();
        allocateOutOrder.setStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
        allocateOutOrder.setIsCancel(ZeroToNineEnum.ONE.getCode());
        Example example = new Example(AllocateInOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("allocateOrderCode", allocateOrderCode);
        allocateOutOrderService.updateByExampleSelective(allocateOutOrder, example);
        //更新调拨入库单sku状态为已取消
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateOutStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode().toString());
        Example example2 = new Example(AllocateSkuDetail.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("allocateOrderCode", allocateOrderCode);
        allocateSkuDetailService.updateByExampleSelective(allocateSkuDetail, example2);
        //记录操作日志
        logInfoService.recordLog(allocateOutOrder, allocateOutOrder.getId().toString(), SYSTEM, LogOperationEnum.CREATE.getMessage(), ALLOCATE_ORDER_DESCARD,null);
    }

    @Override
    public Map<String, Object> updateAllocateInOrderByCancel(String allocateOrderCode, String type, String flag, String cancelReson) {
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        allocateInOrder = allocateInOrderService.selectOne(allocateInOrder);
        AssertUtil.notNull(allocateInOrder, String.format("根据调拨单号%s查询调拨入库单信息为空", allocateInOrder));
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateOrderCode(allocateOrderCode);
        List<AllocateSkuDetail> allocateSkuDetailList = allocateSkuDetailService.select(allocateSkuDetail);
        AssertUtil.notEmpty(allocateSkuDetailList, String.format("根据调拨单号%s查询调拨入库单明细信息为空", allocateInOrder));
        //更新调拨入库单
        if(StringUtils.equals(type, ZeroToNineEnum.ZERO.getCode())){//关闭类型
            if(StringUtils.equals(flag, ZeroToNineEnum.ZERO.getCode())){//关闭
                allocateInOrder.setOldtatus(allocateInOrder.getStatus());
                allocateInOrder.setStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
                allocateInOrder.setIsClose(ZeroToNineEnum.ONE.getCode());
                allocateInOrder.setMemo(cancelReson);
            }else if(StringUtils.equals(flag, ZeroToNineEnum.ONE.getCode())){//取消关闭
                allocateInOrder.setStatus(allocateInOrder.getOldtatus());
                allocateInOrder.setIsClose(ZeroToNineEnum.ZERO.getCode());
            }
        }else if(StringUtils.equals(type, ZeroToNineEnum.ONE.getCode())){//取消发货类型
            if(StringUtils.equals(flag, ZeroToNineEnum.ZERO.getCode())){//取消发货
                allocateInOrder.setOldtatus(allocateInOrder.getStatus());
                allocateInOrder.setStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
                allocateInOrder.setIsCancel(ZeroToNineEnum.ONE.getCode());
                allocateInOrder.setMemo(cancelReson);
            }else if(StringUtils.equals(flag, ZeroToNineEnum.ONE.getCode())){//重新发货
                allocateInOrder.setStatus(allocateInOrder.getOldtatus());
                allocateInOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
            }
        }else if(StringUtils.equals(type, ZeroToNineEnum.TWO.getCode())){//作废类型
            allocateInOrder.setOldtatus(allocateInOrder.getStatus());
            allocateInOrder.setStatus(AllocateInOrderStatusEnum.CANCEL.getCode().toString());
            allocateInOrder.setMemo(cancelReson);
        }



        /*Example example = new Example(AllocateInOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("allocateOrderCode", allocateOrderCode);
        allocateInOrderService.updateByExampleSelective(allocateInOrder, example);
        //更新调拨入库单sku

        allocateSkuDetail.setAllocateInStatus(AllocateInOrderDetailStatusEnum.CANCEL.getCode().toString());
        Example example2 = new Example(AllocateSkuDetail.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("allocateOrderCode", allocateOrderCode);
        allocateSkuDetailService.updateByExampleSelective(allocateSkuDetail, example2);*/
        return null;
    }


}
