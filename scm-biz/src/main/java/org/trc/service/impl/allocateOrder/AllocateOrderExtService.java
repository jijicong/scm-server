package org.trc.service.impl.allocateOrder;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.allocateOrder.AllocateOrderBase;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("allocateOrderExtService")
public class AllocateOrderExtService implements IAllocateOrderExtService {

    @Autowired
    private IAclUserAccreditInfoService aclUserAccreditInfoService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;

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

}
