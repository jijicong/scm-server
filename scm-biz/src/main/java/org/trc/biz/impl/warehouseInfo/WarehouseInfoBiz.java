package org.trc.biz.impl.warehouseInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.warehouseInfo.IWarehouseInfoBiz;
import org.trc.domain.System.Warehouse;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.ValidStateEnum;
import org.trc.service.System.IWarehouseService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.ResultUtil;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangyz on 2017/11/15.
 */
public class WarehouseInfoBiz implements IWarehouseInfoBiz {

    @Autowired
    private IWarehouseService warehouseService;

    @Autowired
    private IWarehouseInfoService warehouseInfoService;

    @Override
    public Response saveWarehouse(String qimenWarehouseCode) {
        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qimenWarehouseCode", qimenWarehouseCode);

        return null;
    }

    @Override
    public Response selectWarehouse() {
        //1、首先查出所有的启动仓库
        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        List<Warehouse> list = warehouseService.selectByExample(criteria);
        List<String> warehouseCodeList = new ArrayList<>();
        for (Warehouse warehouse:list){
            warehouseCodeList.add(warehouse.getQimenWarehouseCode());
        }
        //2、查出我们未被添加的仓库
        Example example1 = new Example(WarehouseInfo.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andNotIn("qimenWarehouseCode",warehouseCodeList);
        List<WarehouseInfo> resultList = warehouseInfoService.selectByExample(criteria1);
        List<Map<String,String>> rev = new ArrayList<>();
        for (WarehouseInfo warehouseInfo:resultList){
            Map<String,String> map = new HashMap<>();
            map.put(warehouseInfo.getWarehouseName(),warehouseInfo.getQimenWarehouseCode());
            rev.add(map);
        }
        return ResultUtil.createSuccessResult("获取仓库名称成功",rev);
    }
}
