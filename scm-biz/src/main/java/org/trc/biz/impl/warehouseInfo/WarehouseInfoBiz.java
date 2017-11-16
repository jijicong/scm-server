package org.trc.biz.impl.warehouseInfo;

import com.alibaba.dubbo.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.warehouseInfo.IWarehouseInfoBiz;
import org.trc.domain.System.Warehouse;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidStateEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.WarehouseInfoException;
import org.trc.form.warehouseInfo.WarehouseInfoForm;
import org.trc.form.warehouseInfo.WarehouseInfoResult;
import org.trc.service.System.IWarehouseService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
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
@Service("warehouseInfoBiz")
public class WarehouseInfoBiz implements IWarehouseInfoBiz {

    private Logger log = LoggerFactory.getLogger(WarehouseInfoBiz.class);

    @Autowired
    private IWarehouseService warehouseService;

    @Autowired
    private IWarehouseInfoService warehouseInfoService;

    @Override
    public Response saveWarehouse(String qimenWarehouseCode) {
        AssertUtil.notBlank(qimenWarehouseCode,"奇门仓库编号不能为空");
        log.info("查询符合条件的仓库=====》");
        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qimenWarehouseCode", qimenWarehouseCode);
        List<Warehouse> list = warehouseService.selectByExample(example);
        if (list.size()>1){
            log.info("一个奇门仓库编号取到多条数据");
        }
        Warehouse warehouse = list.get(0);
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setWarehouseName(warehouse.getName());
        warehouseInfo.setType(warehouse.getWarehouseTypeCode());
        warehouseInfo.setQimenWarehouseCode(warehouse.getQimenWarehouseCode());

        /*warehouseInfo.setSkuNum();
        warehouseInfo.setOwnerId();
        warehouseInfo.setWarehouseOwnerId();
        warehouseInfo.setOwnerName();
        warehouseInfo.setOwnerWarehouseState();
        warehouseInfo.setIsDelete();*/
        log.info("保存仓库到数据库=====》");
        int count = warehouseInfoService.insert(warehouseInfo);
        if (count == 0) {
            String msg = "仓库信息管理添加新仓库到数据库失败";
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
        log.info("《===========保存到数据库成功");
        return ResultUtil.createSuccessResult("获取仓库名称成功","success");
    }

    @Override
    public Response selectWarehouseNotInLocation() {
        //1、首先查出本地存在的仓库
        log.info("查询符合条件的仓库===》");
        Example example1 = new Example(WarehouseInfo.class);
        Example.Criteria criteria1 = example1.createCriteria();
        List<WarehouseInfo> resultList = warehouseInfoService.selectByExample(example1);
        List<String> warehouseCodeList = new ArrayList<>();
        for (WarehouseInfo warehouseInfo:resultList){
            warehouseCodeList.add(warehouseInfo.getQimenWarehouseCode());
        }
        //2、查出我们未被添加的仓库
        log.info("去除已经添加的仓库=========》");
        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        if (warehouseCodeList.size()!=0){
            criteria.andNotIn("qimenWarehouseCode",warehouseCodeList);
        }
        List<Warehouse> list = warehouseService.selectByExample(example);
        List<Map<String,String>> rev = new ArrayList<>();
        for (Warehouse warehouse:list){
            Map<String,String> map = new HashMap<>();
            map.put(warehouse.getName(),warehouse.getQimenWarehouseCode());
            rev.add(map);
        }
        log.info("《==========返回符合条件的仓库名称");
        return ResultUtil.createSuccessResult("获取仓库名称成功",rev);
    }

    @Override
    public Response selectWarehouse() {
        //1、首先查出所有的启动仓库
        log.info("开始查询启用的仓库====》");
        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        List<Warehouse> list = warehouseService.selectByExample(example);
        List<Map<String,String>> rev = new ArrayList<>();
        for (Warehouse warehouse:list){
            Map<String,String> map = new HashMap<>();
            map.put(warehouse.getName(),warehouse.getQimenWarehouseCode());
            rev.add(map);
        }
        log.info("<======返回仓库名称");
        return ResultUtil.createSuccessResult("获取仓库名称成功",rev);
    }

    @Override
    public Pagenation<WarehouseInfoResult> selectWarehouseInfoByPage(WarehouseInfoForm query, Pagenation<WarehouseInfo> page) {
        AssertUtil.notNull(page.getPageNo(),"分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(),"分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(),"分页查询参数start不能为空");
        log.info("开始查询符合条件的仓库信息===========》");
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isBlank(query.getWarehouseName())){
            criteria.andLike("warehouseName","%"+query.getWarehouseName()+"%");
        }
        example.orderBy("createTime").desc();
        Pagenation<WarehouseInfo> pagenation = warehouseInfoService.pagination(example,page,query);
        log.info("《==========查询结束，开始组装返回结果");
        List<WarehouseInfo> list = pagenation.getResult();
        List<WarehouseInfoResult> newList = new ArrayList<>();
        for (WarehouseInfo warehouseInfo:list){
            WarehouseInfoResult result = new WarehouseInfoResult();
            result.setWarehouseName(warehouseInfo.getWarehouseName());
            result.setType(warehouseInfo.getType());
            result.setQimenWarehouseCode(warehouseInfo.getQimenWarehouseCode());
            result.setSkuNum(warehouseInfo.getSkuNum());
            String state = convertWarehouseState(warehouseInfo.getOwnerWarehouseState());
            result.setOwnerWarehouseState(state);
            result.setCreateTime(DateUtils.formatDateTime(warehouseInfo.getCreateTime()));
            result.setUpdateTime(DateUtils.formatDateTime(warehouseInfo.getUpdateTime()));
            result.setIsDelete(convertDeleteState(warehouseInfo));
            result.setOwnerId(warehouseInfo.getOwnerId());
            result.setOwnerName(warehouseInfo.getOwnerName());
            result.setWarehouseOwnerId(warehouseInfo.getWarehouseOwnerId());
            result.setRemark(warehouseInfo.getRemark());
            newList.add(result);
        }

        Pagenation<WarehouseInfoResult> resultPagenation = new Pagenation<>();
        resultPagenation.setResult(newList);
        resultPagenation.setPageNo(pagenation.getPageNo());
        resultPagenation.setPageSize(pagenation.getPageSize());
        resultPagenation.setTotalCount(pagenation.getTotalCount());
        resultPagenation.setStart(pagenation.getStart());
        log.info("组装数据完成《=============");
        return resultPagenation;
    }

    private String convertWarehouseState(String ownerWarehouseState){
        String state = null;
        if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.ZERO.getCode())){
            state = "待通知";
        }else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.ONE.getCode())){
            state = "通知成功";
        }else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.TWO.getCode())){
            state = "通知失败";
        }
        return state;
    }

    private Integer convertDeleteState(WarehouseInfo warehouseInfo){
        Integer count = 0;
        if (StringUtils.isEquals(warehouseInfo.getOwnerWarehouseState(), ZeroToNineEnum.ZERO.getCode()) ||
                StringUtils.isEquals(warehouseInfo.getOwnerWarehouseState(), ZeroToNineEnum.TWO.getCode())){
            count = 1;
        }
        if (warehouseInfo.getSkuNum() == 0 ){
            count = 1;
        }
        return count;
    }
}
