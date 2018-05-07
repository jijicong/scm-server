package org.trc.biz.impl.allocateOrder;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.allocateOrder.IAllocateOrderBiz;
import org.trc.biz.allocateOrder.IAllocateOutOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateOrder;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.OutboundOrder;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.*;
import org.trc.exception.AllocateOrderException;
import org.trc.exception.AllocateOutOrderException;
import org.trc.exception.OutboundOrderException;
import org.trc.form.AllocateOrder.AllocateOrderForm;
import org.trc.form.AllocateOrder.AllocateOutOrderForm;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;
import org.trc.util.cache.AllocateOrderCacheEvict;
import org.trc.util.cache.OutboundOrderCacheEvict;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("allocateOutOrderBiz")
public class AllocateOutOrderBiz implements IAllocateOutOrderBiz {

    private Logger logger = LoggerFactory.getLogger(AllocateOutOrderBiz.class);
    @Autowired
    private IAllocateOutOrderService allocateOutOrderService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IAllocateSkuDetailService allocateSkuDetailService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IAllocateOrderService allocateOrderService;

    /**
     * 调拨单分页查询
     */
    @Override
    @Cacheable(value = SupplyConstants.Cache.ALLOCATE_OUT_ORDER)
    public Pagenation<AllocateOutOrder> allocateOutOrderPage(AllocateOutOrderForm form,
															 Pagenation<AllocateOutOrder> page) {

        AssertUtil.notNull(page.getPageNo(),"分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(),"分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(),"分页查询参数start不能为空");

        Example example = new Example(AllocateOutOrder.class);
        Example.Criteria criteria = example.createCriteria();

        //调拨单编号
        if (!StringUtils.isBlank(form.getAllocateOrderCode())) {
            criteria.andLike("allocateOrderCode","%"+form.getAllocateOrderCode()+"%");
        }

        //调拨出库单编号
        if (!StringUtils.isBlank(form.getAllocateOutOrderCode())) {
            criteria.andLike("allocateOutOrderCode","%"+form.getAllocateOutOrderCode()+"%");
        }

        //单据创建人
        if (!StringUtils.isBlank(form.getCreateOperatorName())) {
            criteria.andLike("createOperatorName","%"+form.getCreateOperatorName()+"%");
        }

        //调出仓库
        if (!StringUtils.isBlank(form.getOutWarehouseCode())) {
            criteria.andEqualTo("outWarehouseCode", form.getOutWarehouseCode());
        }

        //出入库状态
        if (!StringUtils.isBlank(form.getStatus())) {
            criteria.andEqualTo("status", form.getStatus());
        }

        //创建日期开始
        if (!StringUtils.isBlank(form.getStartDate())) {
            criteria.andGreaterThanOrEqualTo("createTime", form.getStartDate());
        }

        //创建日期结束
        if (!StringUtils.isBlank(form.getEndDate())) {
            criteria.andLessThanOrEqualTo("createTime", form.getEndDate());
        }

        example.setOrderByClause("field(status,0,2,1,3,4,5)");
        example.orderBy("updateTime").desc();

        Pagenation<AllocateOutOrder> pagenation = allocateOutOrderService.pagination(example, page, form);

        List<AllocateOutOrder> allocateOutOrders = pagenation.getResult();
        for(AllocateOutOrder allocateOutOrder : allocateOutOrders){
            AssertUtil.notNull(allocateOutOrder.getAllocateOrderCode(),"调拨单号不能为空,id="+allocateOutOrder.getId());
            AllocateOrder allocateOrder = new AllocateOrder();
            allocateOrder.setAllocateOrderCode(allocateOutOrder.getAllocateOrderCode());
            allocateOrder = allocateOrderService.selectOne(allocateOrder);

            if((StringUtils.equals(allocateOutOrder.getIsCancel(), ZeroToNineEnum.ONE.getCode())
                    || StringUtils.equals(allocateOutOrder.getIsClose(), ZeroToNineEnum.ONE.getCode())) &&
                    this.checkDate(allocateOutOrder.getUpdateTime())){
                allocateOutOrder.setIsTimeOut(ZeroToNineEnum.ONE.getCode());
            }else if(StringUtils.equals(allocateOrder.getOrderStatus(), AllocateOrderEnum.AllocateOrderStatusEnum.DROP.getCode())){
                    allocateOutOrder.setIsTimeOut(ZeroToNineEnum.ONE.getCode());
            }else{
                allocateOutOrder.setIsTimeOut(ZeroToNineEnum.ZERO.getCode());
            }
        }

        return pagenation;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @AllocateOrderCacheEvict
    public Response close(Long id, String remark, AclUserAccreditInfo aclUserAccreditInfo) {
        try{
            AssertUtil.notNull(id, "调拨出库单主键不能为空");
            AssertUtil.notBlank(remark, "关闭原因不能为空");

            //获取出库单信息
            AllocateOutOrder allocateOutOrder = allocateOutOrderService.selectByPrimaryKey(id);

            if(!StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode()) ||
                    !StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode())){
                String msg = "调拨出库通知单状态必须为出库仓接收失败或待通知出库!";
                logger.error(msg);
                throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
            }

            //修改状态
            this.updateDetailStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode(),
                    allocateOutOrder.getAllocateOrderCode(), AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.WAIT_OUT.getCode());
            this.updateOrderCancelInfo(allocateOutOrder, remark, true);

            //仓库接受失败插入一条日志
            String userId = aclUserAccreditInfo.getUserId();
            logInfoService.recordLog(allocateOutOrder, String.valueOf(allocateOutOrder.getId()),userId,"手工关闭", remark,null);
            return ResultUtil.createSuccessResult("调拨出库通知单关闭成功！", "");
        }catch(Exception e){
            String msg = e.getMessage();
            logger.error(msg, e);
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), msg, "");
        }
    }

    @Override
    @AllocateOrderCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response cancelClose(Long id, AclUserAccreditInfo aclUserAccreditInfo) {
        try{
            AssertUtil.notNull(id, "调拨出库单主键不能为空");

            //获取出库单信息
            AllocateOutOrder allocateOutOrder = allocateOutOrderService.selectByPrimaryKey(id);

            if(!StringUtils.equals(allocateOutOrder.getIsClose(), ZeroToNineEnum.ONE.getCode())){
                String msg = "调拨出库通知单没有关闭!";
                logger.error(msg);
                throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
            }

            if(this.checkDate(allocateOutOrder.getUpdateTime())){
                String msg = "调拨出库通知单已经超过7天，不允许取消关闭!";
                logger.error(msg);
                throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
            }

            //修改状态
            this.updateDetailStatus(allocateOutOrder.getOldtatus(), allocateOutOrder.getAllocateOrderCode(),
                    AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.WAIT_OUT.getCode());
            this.updateOrderCancelInfoExt(allocateOutOrder, true);

            String userId = aclUserAccreditInfo.getUserId();
            logInfoService.recordLog(allocateOutOrder, String.valueOf(allocateOutOrder.getId()), userId,"取消关闭", "",null);
            return ResultUtil.createSuccessResult("取消关闭成功！", "");
        }catch(Exception e){
            String msg = e.getMessage();
            logger.error(msg, e);
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), msg, "");
        }
    }

    //修改详情状态
    private void updateDetailStatus(String code, String allocateOrderCode, String allocateStatus){
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setOutStatus(code);
        allocateSkuDetail.setAllocateOutStatus(allocateStatus);
        allocateSkuDetail.setUpdateTime(Calendar.getInstance().getTime());
        Example exampleOrder = new Example(AllocateSkuDetail.class);
        Example.Criteria criteriaOrder = exampleOrder.createCriteria();
        criteriaOrder.andEqualTo("allocateOrderCode", allocateOrderCode);
        allocateSkuDetailService.updateByExampleSelective(allocateSkuDetail, exampleOrder);
    }

    //修改调拨出库单信息
    private void updateOrderCancelInfo(AllocateOutOrder allocateOutOrder, String remark, boolean isClose){
        allocateOutOrder.setOldtatus(allocateOutOrder.getStatus());
        allocateOutOrder.setStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode());
        if(isClose){
            allocateOutOrder.setIsClose(ZeroToNineEnum.ONE.getCode());
        }else {
            allocateOutOrder.setIsCancel(ZeroToNineEnum.ONE.getCode());
        }
        allocateOutOrder.setUpdateTime(Calendar.getInstance().getTime());
        allocateOutOrder.setMemo(remark);
        allocateOutOrderService.updateByPrimaryKey(allocateOutOrder);
    }

    private void updateOrderCancelInfoExt(AllocateOutOrder allocateOutOrder, boolean isClose){
        allocateOutOrder.setStatus(allocateOutOrder.getOldtatus());
        allocateOutOrder.setOldtatus("");
        if(isClose){
            allocateOutOrder.setIsClose(ZeroToNineEnum.ZERO.getCode());
        }else{
            allocateOutOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
        }
        allocateOutOrder.setUpdateTime(Calendar.getInstance().getTime());
        allocateOutOrder.setMemo("");
        allocateOutOrderService.updateByPrimaryKey(allocateOutOrder);
    }

    private boolean checkDate(Date updateTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updateTime);
        calendar.add(Calendar.DATE, Integer.parseInt(ZeroToNineEnum.SEVEN.getCode()));
        if(calendar.compareTo(Calendar.getInstance()) == 1){
            return false;
        }
        return true;
    }
}
