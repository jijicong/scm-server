package org.trc.biz.impl.system;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.system.IWarehouseBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.util.Serial;
import org.trc.enums.*;
import org.trc.exception.ConfigException;
import org.trc.exception.ParamValidException;
import org.trc.exception.WarehouseException;
import org.trc.form.system.WarehouseForm;
import org.trc.service.System.IWarehouseService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import javax.ws.rs.container.ContainerRequestContext;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by sone on 2017/5/5.
 */
@Service("warehouseBiz")
public class WarehouseBiz implements IWarehouseBiz {

    private Logger logger = LoggerFactory.getLogger(ChannelBiz.class);

    private final static String  SERIALNAME="CK";

    private final static Integer LENGTH=5;

    @Resource
    private IWarehouseService warehouseService;

    @Resource
    private IUserNameUtilService userNameUtilService;

    @Resource
    private ISerialUtilService serialUtilService;




    @Override
    public Pagenation<Warehouse> warehousePage(WarehouseForm form, Pagenation<Warehouse> page){

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
        userNameUtilService.handleUserName(pagenation.getResult());
        return pagenation;

    }

    @Override
    public List<Warehouse> findWarehouseValid(){
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
    public void saveWarehouse(Warehouse warehouse,ContainerRequestContext requestContext){

        AssertUtil.notNull(warehouse,"仓库管理模块保存仓库信息失败，仓库信息为空");
        Warehouse tmp = findWarehouseByName(warehouse.getName());
        AssertUtil.isNull(tmp,String.format("仓库名称[name=%s]的数据已存在,请使用其他名称",warehouse.getName()));
        ParamsUtil.setBaseDO(warehouse);
        warehouse.setCode(serialUtilService.generateCode(LENGTH,SERIALNAME));
        int count = warehouseService.insert(warehouse);
        if(count==0){
            String msg = "仓库保存,数据库操作失败";
            logger.error(msg);
            throw new WarehouseException(ExceptionEnum.SYSTEM_WAREHOUSE_SAVE_EXCEPTION, msg);
        }

    }

    @Override
    public Warehouse findWarehouseByName(String name){

        AssertUtil.notBlank(name,"根据渠道名称查询渠道的参数name为空");
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        return warehouseService.selectOne(warehouse);

    }

    @Override
    public void updateWarehouseState(Warehouse warehouse, ContainerRequestContext requestContext){

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
            String msg = String.format("修改仓库%s数据库操作失败",JSON.toJSONString(warehouse));
            logger.error(msg);
            throw new WarehouseException(ExceptionEnum.SYSTEM_WAREHOUSE_UPDATE_EXCEPTION, msg);
        }

    }

    @Override
    public Warehouse findWarehouseById(Long id){

        AssertUtil.notNull(id,"根据ID查询仓库参数ID为空");
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse = warehouseService.selectOne(warehouse);
        AssertUtil.notNull(warehouse,String.format("根据主键ID[id=%s]查询仓库为空",id.toString()));
        return warehouse;

    }

    @Override
    public void updateWarehouse(Warehouse warehouse, ContainerRequestContext requestContext){

        AssertUtil.notNull(warehouse.getId(),"根据ID修改仓库参数ID为空");
        Warehouse tmp = findWarehouseByName(warehouse.getName());
        if(tmp!=null){
            if(!tmp.getId().equals(warehouse.getId())){
                throw new WarehouseException(ExceptionEnum.SYSTEM_WAREHOUSE_UPDATE_EXCEPTION, "其它的仓库已经使用该仓库名称");
            }
        }
        warehouse.setUpdateTime(Calendar.getInstance().getTime());
        int count = warehouseService.updateByPrimaryKeySelective(warehouse);
        if (count == 0) {
            String msg = String.format("修改仓库%s数据库操作失败",JSON.toJSONString(warehouse));
            logger.error(msg);
            throw new WarehouseException(ExceptionEnum.SYSTEM_WAREHOUSE_UPDATE_EXCEPTION, msg);
        }

    }

}
