package org.trc.biz.impl.allocateOrder;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.allocateOrder.IAllocateOutOrderBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.allocateOrder.AllocateOutOrder;
import org.trc.domain.allocateOrder.AllocateSkuDetail;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.AllocateOutOrderException;
import org.trc.form.AllocateOrder.AllocateOutOrderForm;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOutOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.config.ILogInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateCheckUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;
import org.trc.util.cache.AllocateOrderCacheEvict;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static javafx.beans.binding.Bindings.select;

@Service("allocateOutOrderBiz")
public class AllocateOutOrderBiz implements IAllocateOutOrderBiz {

    private Logger logger = LoggerFactory.getLogger(AllocateOutOrderBiz.class);
    @Autowired
    private IAllocateOutOrderService allocateOutOrderService;
    @Autowired
    private IAllocateSkuDetailService allocateSkuDetailService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IAllocateOrderExtService allocateOrderExtService;

    /**
     * 调拨单分页查询
     */
    @Override
    //@Cacheable(value = SupplyConstants.Cache.ALLOCATE_OUT_ORDER)
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
            allocateOrderExtService.setCreateOperator(form.getCreateOperatorName(), criteria);
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

        allocateOrderExtService.setIsTimeOut(pagenation);

        allocateOrderExtService.setAllocateOrderOtherNames(page);
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

            if(!StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.WAIT_NOTICE.getCode()) &&
                    !StringUtils.equals(allocateOutOrder.getStatus(), AllocateOrderEnum.AllocateOutOrderStatusEnum.OUT_RECEIVE_FAIL.getCode())){
                String msg = "调拨出库通知单状态必须为出库仓接收失败或待通知出库!";
                logger.error(msg);
                throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
            }

            //修改状态
            this.updateDetailStatus(AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode(),
                    allocateOutOrder.getAllocateOrderCode(), AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.WAIT_OUT.getCode(), false);
            allocateOrderExtService.updateOrderCancelInfo(allocateOutOrder, remark, true,
                    AllocateOrderEnum.AllocateOutOrderStatusEnum.CANCEL.getCode());

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

            if(DateCheckUtil.checkDate(allocateOutOrder.getUpdateTime())){
                String msg = "调拨出库通知单已经超过7天，不允许取消关闭!";
                logger.error(msg);
                throw new AllocateOutOrderException(ExceptionEnum.ALLOCATE_OUT_ORDER_CLOSE_EXCEPTION, msg);
            }

            //修改状态
            this.updateDetailStatus("", allocateOutOrder.getAllocateOrderCode(),
                    AllocateOrderEnum.AllocateOrderSkuOutStatusEnum.WAIT_OUT.getCode(), true);
            allocateOrderExtService.updateOrderCancelInfoExt(allocateOutOrder, true);

            String userId = aclUserAccreditInfo.getUserId();
            logInfoService.recordLog(allocateOutOrder, String.valueOf(allocateOutOrder.getId()), userId,"取消关闭", "",null);
            return ResultUtil.createSuccessResult("取消关闭成功！", "");
        }catch(Exception e){
            String msg = e.getMessage();
            logger.error(msg, e);
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), msg, "");
        }
    }

    @Override
    public AllocateOutOrder queryDetail(Long id) {
        AssertUtil.notNull(id, "查询调拨出库单详情信息参数调拨单id不能为空");
        AllocateOutOrder allocateOutOrder = allocateOutOrderService.selectByPrimaryKey(id);
        allocateOrderExtService.setArea(allocateOutOrder);
        return allocateOutOrder;
    }

    //修改详情状态
    private void updateDetailStatus(String code, String allocateOrderCode, String allocateStatus, boolean cancel){
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateOrderCode(allocateOrderCode);
        List<AllocateSkuDetail> allocateSkuDetails = allocateSkuDetailService.select(allocateSkuDetail);
        List<AllocateSkuDetail> allocateSkuDetailsUpdate = new ArrayList<>();

        if(allocateSkuDetails != null && allocateSkuDetails.size() > 0){
            for(AllocateSkuDetail allocateSkuDetailTemp : allocateSkuDetails){
                if(cancel){
                    allocateSkuDetailTemp.setOutStatus(allocateSkuDetailTemp.getOldOutStatus());
                    allocateSkuDetailTemp.setOldOutStatus("");
                }else{
                    allocateSkuDetailTemp.setOldOutStatus(allocateSkuDetailTemp.getOutStatus());
                    allocateSkuDetailTemp.setOutStatus(code);
                }
                allocateSkuDetailTemp.setAllocateOutStatus(allocateStatus);
                allocateSkuDetailTemp.setUpdateTime(Calendar.getInstance().getTime());
                allocateSkuDetailsUpdate.add(allocateSkuDetailTemp);
            }
            allocateSkuDetailService.updateSkuDetailList(allocateSkuDetailsUpdate);
        }
    }

}
