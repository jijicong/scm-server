package org.trc.biz.impl.warehouseInfo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.trc.biz.warehouseInfo.IWarehousePriorityBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehousePriority;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.ValidStateEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.service.config.ILogInfoService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehousePriorityService;
import org.trc.util.AssertUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service("warehousePriorityBiz")
public class WarehousePriorityBiz implements IWarehousePriorityBiz {

    @Autowired
    private IWarehousePriorityService warehousePriorityService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private ILogInfoService logInfoService;

    @Override
    public List<WarehousePriority> warehousePriorityList() {
        Example example = new Example(WarehousePriority.class);
        example.orderBy("priority").asc();
        List<WarehousePriority> warehousePriorityList = warehousePriorityService.selectByExample(example);
        if(!CollectionUtils.isEmpty(warehousePriorityList)){
            List<String> warehouseCodes = new ArrayList<>();
            for(WarehousePriority priority: warehousePriorityList){
                warehouseCodes.add(priority.getWarehouseCode());
            }
            Example example2 = new Example(WarehouseInfo.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andIn("code", warehouseCodes);
            List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example2);
            if(!CollectionUtils.isEmpty(warehouseInfoList)){
                for(WarehousePriority priority: warehousePriorityList){
                    for(WarehouseInfo warehouseInfo: warehouseInfoList){
                        if(StringUtils.equals(priority.getWarehouseCode(), warehouseInfo.getCode())){
                            priority.setWarehouseName(warehouseInfo.getWarehouseName());
                            break;
                        }
                    }
                }
            }
        }
        return warehousePriorityList;
    }

    @Override
    public List<WarehouseInfo> queryWarehouseInfoList() {
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setIsValid(ValidStateEnum.ENABLE.getCode().toString());
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.select(warehouseInfo);
        List<WarehouseInfo> _warehouseInfoList = new ArrayList<>();
        //过滤已经添加过的仓库
        if(!CollectionUtils.isEmpty(warehouseInfoList)){
            WarehousePriority warehousePriority = new WarehousePriority();
            List<WarehousePriority> warehousePriorityList = warehousePriorityService.select(warehousePriority);
            if(!CollectionUtils.isEmpty(warehouseInfoList)){
                for(WarehouseInfo warehouseInfo2: warehouseInfoList){
                    boolean flag = false;
                    for(WarehousePriority priority: warehousePriorityList){
                        if(StringUtils.equals(warehouseInfo2.getCode(), priority.getWarehouseCode())){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        _warehouseInfoList.add(warehouseInfo2);
                    }
                }
            }
        }
        return _warehouseInfoList;
    }


    @Override
    public void saveWarehousePriority(String warehousePriorityInfo, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(warehousePriorityInfo, "保存仓库优先级信息参数warehousePriorityInfo不能为空");
        AssertUtil.notNull(aclUserAccreditInfo, "保存仓库优先级信息参数aclUserAccreditInfo不能为空");
        JSONArray jsonArray = JSONArray.parseArray(warehousePriorityInfo);
        List<WarehousePriority> warehousePriorityList = new ArrayList<>();
        List<WarehousePriority> addWaWarehousePrioritys = new ArrayList<>();
        List<WarehousePriority> updateWaWarehousePrioritys = new ArrayList<>();
        List<String> warehouseCodes = new ArrayList<>();
        for(Object obj: jsonArray){
            JSONObject _obj = (JSONObject)obj;
            WarehousePriority warehousePriority = new WarehousePriority();
            warehousePriority.setWarehouseCode(_obj.getString("warehouseCode"));
            warehousePriority.setWarehouseName(_obj.getString("warehouseName"));
            warehousePriority.setIsValid(_obj.getString("isValid"));
            warehousePriority.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            warehousePriorityList.add(warehousePriority);
            warehouseCodes.add(warehousePriority.getWarehouseCode());
            if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), _obj.getString("source"))){//新增的数据
                addWaWarehousePrioritys.add(warehousePriority);
            }
        }
        //设置新的仓库优先级
        int count = 1;
        for(WarehousePriority priority: warehousePriorityList){
            priority.setPriority(count);
            count++;
        }
        for(WarehousePriority priority: addWaWarehousePrioritys){
            for(WarehousePriority priority2: warehousePriorityList){
                if(StringUtils.equals(priority.getWarehouseCode(), priority2.getWarehouseCode())){
                    priority.setPriority(priority2.getPriority());
                    break;
                }
            }
        }
        //插入新增仓库优先级
        if(!CollectionUtils.isEmpty(addWaWarehousePrioritys)){
            warehousePriorityService.insertList(addWaWarehousePrioritys);
            //记录操作日志
            for(WarehousePriority priority: addWaWarehousePrioritys){
                logInfoService.recordLog(priority,priority.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.ADD_NEW_WAREHOUSE.getMessage(), null,null);
            }
        }

        Example example = new Example(WarehousePriority.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("warehouseCode", warehouseCodes);
        List<WarehousePriority> _warehousePriorityList = warehousePriorityService.selectByExample(example);
        //更新修改过状态的仓库优先级
        for(WarehousePriority priority: warehousePriorityList){
            for(WarehousePriority _priority: _warehousePriorityList){
                if(StringUtils.equals(priority.getWarehouseCode(), _priority.getWarehouseCode())){
                    if(!StringUtils.equals(priority.getIsValid(), _priority.getIsValid()) || priority.getPriority().intValue() != _priority.getPriority().intValue()){
                        priority.setId(_priority.getId());
                        warehousePriorityService.updateByPrimaryKeySelective(priority);
                        //记录操作日志
                        if(!StringUtils.equals(priority.getIsValid(), _priority.getIsValid())){
                            String validStr = "";
                            if(StringUtils.equals(ValidStateEnum.ENABLE.getCode().toString(), priority.getIsValid())){
                                validStr = "重新启用";
                            }else {
                                validStr = "取消启用";
                            }
                            String remark = String.format("%s: %s", priority.getWarehouseName(), validStr);
                            logInfoService.recordLog(priority,priority.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.UPDATE.getMessage(), remark,null);
                        }else{
                            logInfoService.recordLog(priority,priority.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.UPDATE.getMessage(), null,null);
                        }
                        break;
                    }
                }
            }
        }

    }
}
