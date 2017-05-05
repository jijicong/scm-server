package org.trc.biz.impl;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.biz.IWarehouseBiz;
import org.trc.domain.System.Warehouse;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.form.system.WarehouseForm;
import org.trc.service.System.IWarehouseService;
import org.trc.util.CommonUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import org.trc.util.serialUtil;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by sone on 2017/5/5.
 */
@Service
public class WarehouseBiz implements IWarehouseBiz{

    private final static Logger log = LoggerFactory.getLogger(ChannelBiz.class);

    @Resource
    private IWarehouseService warehouseService;

    @Override
    public Pagenation<Warehouse> warehousePage(WarehouseForm form, Pagenation<Warehouse> page) throws Exception {
        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if(StringUtil.isNotEmpty(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        example.orderBy("updateTime").desc();
        Pagenation<Warehouse> pagenation = warehouseService.pagination(example,page,form);
        return pagenation;
    }

    @Override
    public int saveWarehouse(Warehouse warehouse) throws Exception {
        Warehouse tmp = findWarehouseByName(warehouse.getName());
        if(null != tmp){
            String msg = CommonUtil.joinStr("仓库名称[name=",warehouse.getName(),"]的数据已存在,请使用其他名称").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_WAREHOUSE_SAVE_EXCEPTION, msg);
        }
        //查询当前的序列位置
        int dataLen = warehouseService.select(new Warehouse()).size();//TODO
        if(dataLen>99999){
            throw new RuntimeException("程序需要升级");
        }
        warehouse.setCode(serialUtil.getMoveOrderNo("CK",5,dataLen));//TODO
        ParamsUtil.setBaseDO(warehouse);
        int count=0;
        count=warehouseService.insert(warehouse);
        if(count == 0){
            String msg = CommonUtil.joinStr("保存仓库", JSON.toJSONString(warehouse),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_WAREHOUSE_SAVE_EXCEPTION, msg);
        }
        return count;
    }
    @Override
    public Warehouse findWarehouseByName(String name) throws Exception {
        if(StringUtil.isEmpty(name) || name==null){
            String msg = CommonUtil.joinStr("根据渠道名称查询渠道的参数name为空").toString();
            log.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Warehouse warehouse=new Warehouse();
        warehouse.setName(name);
        return warehouseService.selectOne(warehouse);
    }
    @Override
    public int updateWarehouseState(Warehouse warehouse) throws Exception {
        Long id = warehouse.getId();
        String state = warehouse.getIsValid();
        int stateInt = Integer.parseInt(state);
        if(stateInt==1){
            warehouse.setIsValid(0+"");
        }else if(stateInt==0){
            warehouse.setIsValid(1+"");
        }else {
            String msg = CommonUtil.joinStr("参数的状态值不符合要求").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Warehouse updateWarehouse=new Warehouse();
        updateWarehouse.setId(warehouse.getId());
        updateWarehouse.setIsValid(warehouse.getIsValid());
        int count=warehouseService.updateByPrimaryKeySelective(updateWarehouse);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改仓库",JSON.toJSONString(warehouse),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_WAREHOUSE_UPDATE_EXCEPTION, msg);
        }
        return count;
    }

    @Override
    public Warehouse findWarehouseById(Long id) throws Exception {
        return null;
    }

    @Override
    public int updateWarehouse(Warehouse warehouse, Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("修改仓库参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        int count = 0;
        warehouse.setId(id);
        warehouse.setUpdateTime(new Date());
        count = warehouseService.updateByPrimaryKeySelective(warehouse);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改仓库",JSON.toJSONString(warehouse),"数据库操作失败").toString();
            log.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_CHANNEL_UPDATE_EXCEPTION, msg);
        }
        return count;
    }
}
