package org.trc.biz.impl.system;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.system.IWarehouseBiz;
import org.trc.domain.System.Warehouse;
import org.trc.domain.util.Serial;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.exception.WarehouseException;
import org.trc.form.system.WarehouseForm;
import org.trc.service.System.IWarehouseService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by sone on 2017/5/5.
 */
@Service("warehouseBiz")
public class WarehouseBiz implements IWarehouseBiz {

    private Logger  LOGGER = LoggerFactory.getLogger(ChannelBiz.class);

    private final static String  SERIALNAME="CK";

    private final static Integer LENGTH=5;

    @Resource
    private IWarehouseService warehouseService;

    @Resource
    private ISerialUtilService serialUtilService;

    @Override
    public Pagenation<Warehouse> warehousePage(WarehouseForm form, Pagenation<Warehouse> page) throws Exception {

        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();

        if (!StringUtils.isBlank(form.getName())) {
            criteria.andLike("name", "%" + form.getName() + "%");
        }
        if (!StringUtils.isBlank(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        example.orderBy("updateTime").desc();
        Pagenation<Warehouse> pagenation = warehouseService.pagination(example, page, form);
        return pagenation;

    }

    @Override
    public List<Warehouse> findWarehouseValid() throws Exception {
        Warehouse warehouse = new Warehouse();
        warehouse.setIsValid(ValidEnum.VALID.getCode());
        List<Warehouse> warehouseList = warehouseService.select(warehouse);
        if(warehouseList==null){
            warehouseList = new ArrayList<Warehouse>();
        }
        return warehouseList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveWarehouse(Warehouse warehouse) throws Exception {

        AssertUtil.notNull(warehouse,"仓库管理模块保存仓库信息失败，仓库信息为空");
        Warehouse tmp = findWarehouseByName(warehouse.getName());
        if (null != tmp) {
            String msg = CommonUtil.joinStr("仓库名称[name=", warehouse.getName(), "]的数据已存在,请使用其他名称").toString();
            LOGGER.error(msg);
            throw new ConfigException(ExceptionEnum.SYSTEM_WAREHOUSE_SAVE_EXCEPTION, msg);
        }
        ParamsUtil.setBaseDO(warehouse);
        warehouse.setCode(serialUtilService.generateCode(LENGTH,SERIALNAME));
        try {
            warehouseService.insert(warehouse);
        }catch (Exception e){
            String msg = CommonUtil.joinStr("仓库保存,数据库操作失败").toString();
            LOGGER.error(msg);
            throw new WarehouseException(ExceptionEnum.SYSTEM_WAREHOUSE_SAVE_EXCEPTION, msg);
        }
    }

    @Override
    public Warehouse findWarehouseByName(String name) throws Exception {

        if(StringUtils.isBlank(name)){
            String msg = CommonUtil.joinStr("根据渠道名称查询渠道的参数name为空").toString();
            LOGGER.error(msg);
            throw  new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        return warehouseService.selectOne(warehouse);

    }

    @Override
    public void updateWarehouseState(Warehouse warehouse) throws Exception {

        AssertUtil.notNull(warehouse,"仓库管理模块修改仓库信息失败，仓库信息为空");
        Warehouse updateWarehouse = new Warehouse();
        updateWarehouse.setId(warehouse.getId());
        if (warehouse.getIsValid().equals(ValidEnum.VALID.getCode())) {
            updateWarehouse.setIsValid(ValidEnum.NOVALID.getCode());
        } else {
            updateWarehouse.setIsValid(ValidEnum.VALID.getCode());
        }
        updateWarehouse.setUpdateTime(Calendar.getInstance().getTime());
        int count = warehouseService.updateByPrimaryKeySelective(updateWarehouse);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改仓库", JSON.toJSONString(warehouse), "数据库操作失败").toString();
            LOGGER.error(msg);
            throw new WarehouseException(ExceptionEnum.SYSTEM_WAREHOUSE_UPDATE_EXCEPTION, msg);
        }

    }

    @Override
    public Warehouse findWarehouseById(Long id) throws Exception {

        AssertUtil.notNull(id,"根据ID查询仓库参数ID为空");
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse = warehouseService.selectOne(warehouse);
        if (null == warehouse) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]查询仓库为空").toString();
            throw new WarehouseException(ExceptionEnum.SYSTEM_WAREHOUSE_QUERY_EXCEPTION, msg);
        }
        return warehouse;

    }

    @Override
    public void updateWarehouse(Warehouse warehouse) throws Exception {

        AssertUtil.notNull(warehouse.getId(),"根据ID修改仓库参数ID为空");
        warehouse.setUpdateTime(Calendar.getInstance().getTime());
        int count = warehouseService.updateByPrimaryKeySelective(warehouse);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改仓库", JSON.toJSONString(warehouse), "数据库操作失败").toString();
            LOGGER.error(msg);
            throw new WarehouseException(ExceptionEnum.SYSTEM_CHANNEL_UPDATE_EXCEPTION, msg);
        }

    }


}
