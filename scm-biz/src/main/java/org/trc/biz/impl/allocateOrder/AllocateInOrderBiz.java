package org.trc.biz.impl.allocateOrder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.allocateOrder.IAllocateInOrderBiz;
import org.trc.domain.allocateOrder.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.AllocateOrderEnum;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.enums.allocateOrder.AllocateInOrderStatusEnum;
import org.trc.exception.ParamValidException;
import org.trc.form.AllocateOrder.AllocateInOrderForm;
import org.trc.form.AllocateOrder.AllocateInOrderParamForm;
import org.trc.form.wms.WmsAllocateDetailRequest;
import org.trc.form.wms.WmsAllocateOutInRequest;
import org.trc.service.allocateOrder.IAllocateInOrderService;
import org.trc.service.allocateOrder.IAllocateOrderExtService;
import org.trc.service.allocateOrder.IAllocateOrderService;
import org.trc.service.allocateOrder.IAllocateSkuDetailService;
import org.trc.service.config.ILogInfoService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.ws.rs.core.Response;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("allocateInOrderBiz")
public class AllocateInOrderBiz implements IAllocateInOrderBiz {

    @Autowired
    private IAllocateOrderExtService allocateOrderExtService;
    @Autowired
    private IAllocateInOrderService allocateInOrderService;
    @Autowired
    private IAllocateSkuDetailService allocateSkuDetailService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IAllocateOrderService allocateOrderService;

    @Override
    public Pagenation<AllocateInOrder> allocateInOrderPage(AllocateInOrderForm form, Pagenation<AllocateInOrder> page) {
        Example example = new Example(AllocateInOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtil.isNotEmpty(form.getAllocateOrderCode())) {//调拨单编号
            criteria.andLike("allocateOrderCode", "%" + form.getAllocateOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(form.getAllocateInOrderCode())) {//调拨入库单号
            criteria.andLike("allocateInOrderCode", "%" + form.getAllocateInOrderCode() + "%");
        }
        if (StringUtil.isNotEmpty(form.getInWarehouseCode())) {//调入仓库
            criteria.andEqualTo("inWarehouseCode", form.getInWarehouseCode() );
        }
        if (StringUtil.isNotEmpty(form.getStatus())) {//入库单状态
            criteria.andEqualTo("status", form.getStatus());
        }
        if (StringUtil.isNotEmpty(form.getCreateOperatorName())) {//入库单创建人
            allocateOrderExtService.setCreateOperator(form.getCreateOperatorName(), criteria);
        }
        if (StringUtil.isNotEmpty(form.getStartDate())) {//开始日期
            criteria.andGreaterThanOrEqualTo("createTime", DateUtils.parseDate(form.getStartDate()));
        }
        if (StringUtil.isNotEmpty(form.getEndDate())) {//截止日期
            Date endDate = DateUtils.parseDate(form.getEndDate());
            criteria.andLessThan("createTime", DateUtils.addDays(endDate, 1));
        }
        example.setOrderByClause("instr('0,2,1,4',`status`) DESC");
        example.orderBy("createTime").desc();
        page = allocateInOrderService.pagination(example, page, form);
        allocateOrderExtService.setAllocateOrderOtherNames(page);
        allocateOrderExtService.setIsTimeOut(page);
        return page;
    }

    @Override
    public AllocateInOrder queryDetail(String allocateOrderCode) {
        AssertUtil.notBlank(allocateOrderCode, "查询调拨入库单明细信息参数调拨单编码allocateOrderCode不能为空");
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        allocateInOrder = allocateInOrderService.selectOne(allocateInOrder);
        AssertUtil.notNull(allocateInOrder, String.format("查询调拨单[%s]信息为空", allocateOrderCode));
        AllocateSkuDetail record = new AllocateSkuDetail();
        record.setAllocateOrderCode(allocateOrderCode);
        List<AllocateSkuDetail> allocateSkuDetailList = allocateSkuDetailService.select(record);
        AssertUtil.notEmpty(allocateSkuDetailList, String.format("查询调拨单[%s]明细为空", allocateOrderCode));
        allocateInOrder.setSkuDetailList(allocateSkuDetailList);
        AllocateOrderBase allocateOrderBase = allocateInOrder;
        List<AllocateOrderBase> allocateOrderBaseList = new ArrayList<>();
        allocateOrderBaseList.add(allocateOrderBase);
        allocateOrderExtService.setAllocateOrderOtherNames(allocateOrderBaseList);
        allocateOrderExtService.setArea(allocateOrderBase);
        return allocateInOrder;
    }

    @Override
    public void orderCancel(String allocateOrderCode, String flag, String cancelReson, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(allocateOrderCode, "参数调拨单号allocateOrderCode不能为空");
        AssertUtil.notBlank(flag, "参数操作类型flag不能为空");
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//取消收货操作
            AssertUtil.notBlank(cancelReson, "参数关闭原因cancelReson不能为空");
        }
        AllocateInOrderParamForm form = allocateOrderExtService.updateAllocateInOrderByCancel(allocateOrderCode, ZeroToNineEnum.ONE.getCode(), flag, cancelReson);
        LogOperationEnum logOperationEnum = null;
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//取消收货
            logOperationEnum = LogOperationEnum.CANCEL_RECIVE_GOODS;
        }else if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), flag)){//重新收货
            logOperationEnum = LogOperationEnum.RE_RECIVE_GOODS;
        }
        //记录操作日志
        logInfoService.recordLog(form.getAllocateInOrder(),form.getAllocateInOrder().getId().toString(), aclUserAccreditInfo.getUserId(), logOperationEnum.getMessage(), cancelReson,null);
    }

    @Override
    public void orderClose(String allocateOrderCode, String flag, String cancelReson, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(allocateOrderCode, "参数调拨单号allocateOrderCode不能为空");
        AssertUtil.notBlank(flag, "参数操作类型flag不能为空");
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//关闭操作
            AssertUtil.notBlank(cancelReson, "参数关闭原因cancelReson不能为空");
        }
        AllocateInOrderParamForm form = allocateOrderExtService.updateAllocateInOrderByCancel(allocateOrderCode, ZeroToNineEnum.ZERO.getCode(), flag, cancelReson);
        LogOperationEnum logOperationEnum = null;
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), flag)){//关闭
            logOperationEnum = LogOperationEnum.HAND_CLOSE;
        }else if(StringUtils.equals(ZeroToNineEnum.ONE.getCode(), flag)){//取消关闭
            logOperationEnum = LogOperationEnum.CANCEL_CLOSE;
        }
        //记录操作日志
        logInfoService.recordLog(form.getAllocateInOrder(),form.getAllocateInOrder().getId().toString(), aclUserAccreditInfo.getUserId(), logOperationEnum.getMessage(), cancelReson,null);
    }

    @Override
    public void noticeReciveGoods(String allocateOrderCode, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(allocateOrderCode, "参数调拨单号allocateOrderCode不能为空");
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        allocateInOrder = allocateInOrderService.selectOne(allocateInOrder);
        AssertUtil.notNull(allocateInOrder, String.format("根据调拨单号%s查询调拨入库单信息为空", allocateInOrder));
        //校验订单是否已经是取消状态
        if(StringUtils.equals(AllocateInOrderStatusEnum.CANCEL.getCode().toString(), allocateInOrder.getStatus())){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "调拨单当前已经是取消状态！请刷新页面查看最新数据！");
        }
        //FIXME 调用仓库接口逻辑待实现

        //记录操作日志
        logInfoService.recordLog(allocateInOrder,allocateInOrder.getId().toString(), aclUserAccreditInfo.getUserId(), LogOperationEnum.NOTICE_RECIVE_GOODS.getMessage(), "",null);
    }

    @Override
    public Response inFinishCallBack(WmsAllocateOutInRequest req) {
        AssertUtil.notNull(req, "调拨入库回调信息不能为空");
        String allocateOrderCode = req.getAllocateOrderCode();
        //获取所有调拨出库详情明细
        AllocateSkuDetail allocateSkuDetail = new AllocateSkuDetail();
        allocateSkuDetail.setAllocateOrderCode(allocateOrderCode);
        List<AllocateSkuDetail> allocateSkuDetails = allocateSkuDetailService.select(allocateSkuDetail);

        List<WmsAllocateDetailRequest> wmsAllocateDetailRequests = req.getWmsAllocateDetailRequests();
        if(wmsAllocateDetailRequests != null && wmsAllocateDetailRequests.size() > 0){
            for(WmsAllocateDetailRequest detailRequest : wmsAllocateDetailRequests){
                for(AllocateSkuDetail skuDetail : allocateSkuDetails){
                    if(StringUtils.equals(detailRequest.getSkuCode(), skuDetail.getSkuCode())){
                        skuDetail.setDefectInNum(detailRequest.getDefectInNum());
                        skuDetail.setNornalInNum(detailRequest.getNornalInNum());
                        Long realInNum = detailRequest.getNornalInNum() + detailRequest.getDefectInNum();
                        skuDetail.setRealInNum(realInNum);
                        if(realInNum == skuDetail.getPlanAllocateNum()){
                            skuDetail.setAllocateInStatus(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.IN_NORMAL.getCode());
                            skuDetail.setInStatus(String.valueOf(AllocateInOrderStatusEnum.IN_WMS_FINISH.getCode()));
                        }else{
                            skuDetail.setAllocateInStatus(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.IN_EXCEPTION.getCode());
                            skuDetail.setInStatus(String.valueOf(AllocateInOrderStatusEnum.IN_WMS_EXCEPTION.getCode()));
                        }
                    }
                }
            }
        }
        allocateSkuDetailService.updateSkuDetailList(allocateSkuDetails);
        //更新调拨入库单状态
        AllocateInOrder allocateInOrder = new AllocateInOrder();
        allocateInOrder.setAllocateOrderCode(allocateOrderCode);
        List<AllocateInOrder> allocateInOrders = allocateInOrderService.select(allocateInOrder);
        allocateInOrder = allocateInOrders.get(0);
        if(StringUtils.isNotEmpty(getAllocateInOrderStatusByDetail(allocateSkuDetails))){
            allocateInOrder.setStatus(getAllocateInOrderStatusByDetail(allocateSkuDetails));
        }
        allocateInOrderService.updateByPrimaryKey(allocateInOrder);
        //更新调拨单状态
        AllocateOrder allocateOrder = new AllocateOrder();
        allocateOrder.setAllocateOrderCode(allocateOrderCode);
        List<AllocateOrder> allocateOrders = allocateOrderService.select(allocateOrder);
        allocateOrder = allocateOrders.get(0);
        if(StringUtils.equals(allocateInOrder.getStatus(), String.valueOf(AllocateInOrderStatusEnum.IN_WMS_EXCEPTION.getCode()))){
            allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.IN_EXCEPTION.getCode());
        }else if(StringUtils.equals(allocateInOrder.getStatus(), String.valueOf(AllocateInOrderStatusEnum.IN_WMS_FINISH.getCode()))){
            allocateOrder.setInOutStatus(AllocateOrderEnum.AllocateOrderInOutStatusEnum.IN_NORMAL.getCode());
        }
        allocateOrderService.updateByPrimaryKey(allocateOrder);
        return ResultUtil.createSuccessResult("反填调拨入库信息成功！", "");
    }

    //获取状态
    private String getAllocateInOrderStatusByDetail(List<AllocateSkuDetail> allocateSkuDetails){
        int inFinishNum = 0;//出库完成数
        int inExceptionNum = 0;//出库异常数
        for(AllocateSkuDetail detail : allocateSkuDetails){
            if(StringUtils.equals(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.IN_NORMAL.getCode(), detail.getOutStatus()))
                inFinishNum++;
            else if(StringUtils.equals(AllocateOrderEnum.AllocateOrderSkuInStatusEnum.IN_EXCEPTION.getCode(), detail.getOutStatus())){
                inExceptionNum++;
            }
        }
        //入库异常：存在“入库异常”的商品时，此处就为入库异常；
        if(inExceptionNum > 0){
            return String.valueOf(AllocateInOrderStatusEnum.IN_WMS_EXCEPTION.getCode());
        }
        //入库完成：所有商品的“入库状态”均为“入库完成”，此处就更新为入库完成
        if(inFinishNum == allocateSkuDetails.size()){
            return String.valueOf(AllocateInOrderStatusEnum.IN_WMS_FINISH.getCode());
        }
        return  "";
    }
}
