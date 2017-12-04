package org.trc.biz.impl.outbound;

import com.alibaba.fastjson.JSON;
import com.qimen.api.request.DeliveryorderConfirmRequest;
import com.qimen.api.request.DeliveryorderCreateRequest;
import com.qimen.api.request.OrderCancelRequest;
import com.qimen.api.response.OrderCancelResponse;
import com.qimen.api.request.EntryorderConfirmRequest;
import com.qimen.api.response.DeliveryorderCreateResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.outbuond.IOutBoundOrderBiz;
import org.trc.cache.CacheEvit;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Warehouse;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundDetailLogistics;
import org.trc.domain.order.OutboundOrder;
import org.trc.enums.*;
import org.trc.exception.OutboundOrderException;
import org.trc.enums.*;
import org.trc.exception.GoodsException;
import org.trc.exception.OutboundOrderException;
import org.trc.enums.OutboundDetailStatusEnum;
import org.trc.enums.OutboundOrderStatusEnum;
import org.trc.enums.QimenDeliveryEnum;
import org.trc.form.outbound.OutBoundOrderForm;
import org.trc.service.IQimenService;
import org.trc.service.System.IWarehouseService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailLogisticsService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("outBoundOrderBiz")
public class OutBoundOrderBiz implements IOutBoundOrderBiz {

    public final static String SUCCESS = "200";
    private Logger logger = LoggerFactory.getLogger(OutBoundOrderBiz.class);
    @Autowired
    private IOutBoundOrderService outBoundOrderService;
    @Autowired
    private IWarehouseService warehouseService;
    @Autowired
    private IOutboundDetailService outboundDetailService;
    @Autowired
    private IOutboundDetailLogisticsService outboundDetailLogisticsService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IQimenService qimenService;

    @Override
    public Pagenation<OutboundOrder> outboundOrderPage(OutBoundOrderForm form, Pagenation<OutboundOrder> page, AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notNull(aclUserAccreditInfo, "获取用户信息失败!");
        //获得业务线编码
        String channelCode = aclUserAccreditInfo.getChannelCode();
        AssertUtil.notBlank(channelCode, "业务线编码为空!");

        //创建查询条件
        Example example = new Example(OutboundOrder.class);
        this.setQueryParam(example, form);

        //查询数据
        Pagenation<OutboundOrder> pagenation = outBoundOrderService.pagination(example, page, form);

        return pagenation;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateOutboundDetail(String requestText) throws Exception {
        AssertUtil.notBlank(requestText, "获取奇门返回信息为空!");
        DeliveryorderConfirmRequest confirmRequest = (DeliveryorderConfirmRequest) XmlUtil.xmlStrToBean(requestText, DeliveryorderConfirmRequest.class);
        //包裹信息
        List<DeliveryorderConfirmRequest.Package> packageList = confirmRequest.getPackages();
        //发货单信息
        DeliveryorderConfirmRequest.DeliveryOrder deliveryOrder = confirmRequest.getDeliveryOrder();

        //获取发货单
        String outboundOrderCode = deliveryOrder.getDeliveryOrderCode();
        String status = deliveryOrder.getStatus();
        String operateTime = deliveryOrder.getOperateTime();
        AssertUtil.notBlank(outboundOrderCode, "发货单编号不能为空!");
        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setOutboundOrderCode(outboundOrderCode);
        outboundOrder = outBoundOrderService.selectOne(outboundOrder);

        //更新发货单信息
        this.updateOutboundDetailAndLogistics(packageList, outboundOrderCode, status, operateTime);

        //更新发货单状态
        this.setOutboundOrderStatus(outboundOrderCode, outboundOrder);

    }

    //更新发货单状态
    private void setOutboundOrderStatus(String outboundOrderCode, OutboundOrder outboundOrder){
        List<OutboundDetail> outboundDetailList = null;
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setOutboundOrderCode(outboundOrderCode);
        outboundDetailList = outboundDetailService.select(outboundDetail);
        String outboundOrderStatus = this.getOutboundOrderStatusByDetail(outboundDetailList);
        outboundOrder.setStatus(outboundOrderStatus);
        outBoundOrderService.updateByPrimaryKey(outboundOrder);
    }

    //更新发货单
    private void updateOutboundDetailAndLogistics(List<DeliveryorderConfirmRequest.Package> packageList, String outboundOrderCode, String status, String operateTime) throws Exception{
        List<DeliveryorderConfirmRequest.Item> itemList = null;
        OutboundDetail outboundDetail = null;
        OutboundDetailLogistics outboundDetailLogistics = null;
        List<OutboundDetailLogistics> outboundDetailLogisticsList = null;
        //遍历包裹
        for(DeliveryorderConfirmRequest.Package packageD : packageList){
            itemList = packageD.getItems();
            if(itemList != null){
                //遍历包裹内商品，更新物流和发货单详情
                for(DeliveryorderConfirmRequest.Item item : itemList){
                    Long sentNum = item.getQuantity();
                    //获取发货详情
                    outboundDetail = new OutboundDetail();
                    outboundDetail.setOutboundOrderCode(outboundOrderCode);
                    outboundDetail.setSkuCode(item.getItemCode());
                    outboundDetail = outboundDetailService.selectOne(outboundDetail);

                    //获取当前商品物流
                    outboundDetailLogistics = new OutboundDetailLogistics();
                    outboundDetailLogistics.setOutboundDetailId(outboundDetail.getId());
                    outboundDetailLogistics.setWaybillNumber(packageD.getExpressCode());
                    outboundDetailLogisticsList = outboundDetailLogisticsService.select(outboundDetailLogistics);

                    //判断是否已存储物流信息，如果没有新增
                    if(outboundDetailLogisticsList != null && outboundDetailLogisticsList.size() > 0){
                        outboundDetailLogistics = outboundDetailLogisticsList.get(0);
                        outboundDetailLogistics.setUpdateTime(Calendar.getInstance().getTime());
                        //更新发货商品详情
                        this.updateOutboundDetail(status, outboundDetailLogistics, outboundDetail, operateTime, sentNum);
                        //保存信息
                        outboundDetailLogisticsService.updateByPrimaryKey(outboundDetailLogistics);
                        outboundDetailService.updateByPrimaryKey(outboundDetail);
                    }else{
                        outboundDetailLogistics = new OutboundDetailLogistics();
                        outboundDetailLogistics.setOutboundDetailId(outboundDetail.getId());
                        outboundDetailLogistics.setLogisticsCorporation(packageD.getLogisticsName());
                        outboundDetailLogistics.setLogisticsCode(packageD.getLogisticsCode());
                        outboundDetailLogistics.setItemNum(sentNum);
                        outboundDetailLogistics.setWaybillNumber(packageD.getExpressCode());
                        outboundDetailLogistics.setCreateTime(Calendar.getInstance().getTime());
                        outboundDetailLogistics.setUpdateTime(Calendar.getInstance().getTime());
                        //更新发货商品详情
                        this.updateOutboundDetail(status, outboundDetailLogistics, outboundDetail, operateTime, sentNum);
                        //保存信息
                        outboundDetailLogisticsService.insert(outboundDetailLogistics);
                        outboundDetailService.updateByPrimaryKey(outboundDetail);
                    }
                }
            }
        }
    }

    //更新发货商品明细
    private void updateOutboundDetail(String status, OutboundDetailLogistics outboundDetailLogistics,
                                      OutboundDetail outboundDetail, String operateTime, Long sentNum) throws Exception{
        if(StringUtils.equals(status, QimenDeliveryEnum.DELIVERED.getCode())){
            if(outboundDetailLogistics.getDeliverTime() == null ||
                    this.compareDeliverTime(outboundDetailLogistics.getDeliverTime(), operateTime)){
                outboundDetailLogistics.setDeliverTime(this.getTime(operateTime));
            }
            outboundDetail.setStatus(OutboundDetailStatusEnum.ALL_GOODS.getCode());
            outboundDetail.setUpdateTime(Calendar.getInstance().getTime());
            outboundDetail.setRealSentItemNum(sentNum);
        }
        if(StringUtils.equals(status, QimenDeliveryEnum.PARTDELIVERED.getCode())){
            outboundDetail.setStatus(OutboundDetailStatusEnum.PART_OF_SHIPMENT.getCode());
            outboundDetail.setUpdateTime(Calendar.getInstance().getTime());
            outboundDetail.setRealSentItemNum(sentNum);
        }
    }

    //获取时间
    private Date getTime(String operateTime) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(operateTime);
    }

    //比较时间
    private boolean compareDeliverTime(Date oldDate, String newTime) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = sdf.parse(newTime);
        if(oldDate.getTime() > newDate.getTime()){
            return false;
        }
        return true;
    }

    @Override
    public String createOutbound(String outboundOrderId,AclUserAccreditInfo aclUserAccreditInfo) throws Exception {
        AssertUtil.notBlank(outboundOrderId,"ID不能为空");
        //根据id获取到发货通知单
        OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(Long.valueOf(outboundOrderId));
        Long id = outboundOrder.getId();
        AssertUtil.notNull(outboundOrder,"根据发货通知单id获取发货通知单记录为空");
        Long warehouseId = outboundOrder.getWarehouseId();
        Warehouse warehouse = warehouseService.selectByPrimaryKey(warehouseId);
        Example example = new Example(OutboundDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("outboundOrderCode",outboundOrder.getOutboundOrderCode());
        List<OutboundDetail> outboundDetails = outboundDetailService.selectByExample(example);
        AssertUtil.isTrue(outboundDetails.size()!=0,"发货通知单详情记录不能为空");
        //参数校验
        logger.info("发货通知单验参开始------->");
        verifyParam(warehouse,outboundOrder,outboundDetails);
        logger.info("发货通知单验参完成，开始给参数赋值------->");
        //设置发货通知单参数
        DeliveryorderCreateRequest request = setParam(warehouse,outboundOrder,outboundDetails);
        logger.info("请求参数赋值完成，开始调用奇门接口-------->");
        AppResult<DeliveryorderCreateResponse> result = qimenService.deliveryOrderCreate(request);
        logger.info("调用奇门接口结束<--------");
        String code = result.getAppcode();
        String msg = result.getDatabuffer();
        //调用重新发货接口插入一条日志记录
        String userId= aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(),userId,"重新发送",null,null);
        if (StringUtils.equals(code,SUCCESS)){
            updateOutboundDetailState(outboundOrder.getOutboundOrderCode(),OutboundDetailStatusEnum.WAITING.getCode(),id);
        }else {
            //仓库接受失败插入一条日志
            logInfoService.recordLog(outboundOrder,outboundOrder.getId().toString(),userId,"仓库接收失败",msg,null);
            updateOutboundDetailState(outboundOrder.getOutboundOrderCode(),OutboundDetailStatusEnum.RECEIVE_FAIL .getCode(),id);
            logger.error(msg);
            throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
        }
        return msg;
    }

    @Override
    public Response getOutboundOrderDetail(Long id) {
        try{
            AssertUtil.notNull(id, "发货单主键不能为空!");
            OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(id);
            OutboundDetail outboundDetail = new OutboundDetail();
            outboundDetail.setOutboundOrderCode(outboundOrder.getOutboundOrderCode());
            List<OutboundDetail> outboundDetailList = outboundDetailService.select(outboundDetail);
            for(OutboundDetail detail : outboundDetailList){
                OutboundDetailLogistics outboundDetailLogistics = new OutboundDetailLogistics();
                outboundDetailLogistics.setOutboundDetailId(detail.getId());
                List<OutboundDetailLogistics> outboundDetailLogisticsList = outboundDetailLogisticsService.select(outboundDetailLogistics);
                detail.setOutboundDetailLogisticsList(outboundDetailLogisticsList);
            }
            outboundOrder.setOutboundDetailList(outboundDetailList);
            return ResultUtil.createSuccessResult("获取发货通知单详情成功！", outboundOrder);
        }catch(Exception e){
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "获取发货通知单详情失败！", "");
        }
    }

    @Override
    public Response close(Long id, String remark) {
        try{
            AssertUtil.notNull(id, "发货单主键不能为空");
            AssertUtil.notBlank(remark, "关闭不能为空");

            //获取发货单信息
            OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(id);

            if(!StringUtils.equals(outboundOrder.getStatus(), OutboundOrderStatusEnum.RECEIVE_FAIL.getCode())){
                String msg = "发货通知单状态必须为仓库接收失败!";
                logger.error(msg);
                throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
            }

            //修改状态
            this.updateDetailStatus(OutboundDetailStatusEnum.CANCELED.getCode(), outboundOrder.getOutboundOrderCode());
            this.updateOrderCancelInfo(outboundOrder, remark, true);
            return ResultUtil.createSuccessResult("发货通知单关闭成功！", "");
        }catch(Exception e){
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "发货通知单关闭失败！", "");
        }
    }

    @Override
    public Response cancelClose(Long id) {
        try{
            AssertUtil.notNull(id, "发货单主键不能为空");

            //获取发货单信息
            OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(id);

            if(!StringUtils.equals(outboundOrder.getIsClose(), ZeroToNineEnum.ONE.getCode())){
                String msg = "发货通知单没有关闭!";
                logger.error(msg);
                throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
            }

            if(this.checkDate(outboundOrder.getUpdateTime())){
                String msg = "发货通知单已经超过7天，不允许取消关闭!";
                logger.error(msg);
                throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
            }

            //修改状态
            this.updateDetailStatus(OutboundDetailStatusEnum.RECEIVE_FAIL.getCode(), outboundOrder.getOutboundOrderCode());
            this.updateOrderCancelInfoExt(outboundOrder, true, OutboundOrderStatusEnum.RECEIVE_FAIL.getCode());
            return ResultUtil.createSuccessResult("取消关闭成功！", "");
        }catch(Exception e){
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "取消关闭失败！", "");
        }
    }

    private void updateOrderCancelInfoExt(OutboundOrder outboundOrder, boolean isClose, String code){
        outboundOrder.setStatus(code);
        if(isClose){
            outboundOrder.setIsClose(ZeroToNineEnum.ZERO.getCode());
        }else{
            outboundOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
        }
        outboundOrder.setUpdateTime(Calendar.getInstance().getTime());
        outboundOrder.setRemark("");
        outBoundOrderService.updateByPrimaryKey(outboundOrder);
    }

    private boolean checkDate(Date updateTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updateTime);
        calendar.add(Calendar.DATE, Integer.parseInt(ZeroToNineEnum.SEVEN.getCode()));
        if(calendar.compareTo(Calendar.getInstance()) > 1){
            return true;
        }
        return false;
    }

    @Override
    public void isTimeOutTimer() {
        logger.info("检查出库通知单是否超过七天的定时任务启动----->");
        Example example = new Example(OutboundOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", OutboundOrderStatusEnum.CANCELED.getCode());
        List<OutboundOrder> list = outBoundOrderService.selectByExample(example);
        if (list.size() == 0) {
            logger.info("没有超过七天的出库通知单");
            return;
        }
        for (OutboundOrder outboundOrder : list) {
            //比较时间是否超过7天

            //超过7天的则将is_timeOut更新为1
            OutboundOrder update = new OutboundOrder();
//            update.setIsTimeOut();
//            outBoundOrderService.updateByPrimaryKeySelective()

        }
    }


    private void updateOutboundDetailState(String outboundOrderCode,String state,Long id){
        logger.info("开始更新发货通知单详情表状态");
        Example example = new Example(OutboundDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("outboundOrderCode",outboundOrderCode);
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setStatus(state);
        int count = outboundDetailService.updateByExampleSelective(outboundDetail,example);
        if (count == 0){
            String msg = String.format("创建发货单%s后，更新发货通知单详情表状态失败",outboundOrderCode);
            logger.error(msg);
            throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
        }
        logger.info("更新仓库详情表状态完成<---------");
        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setId(id);
        outboundOrder.setIsCancel(ZeroToNineEnum.ZERO.getCode());
        count = outBoundOrderService.updateByPrimaryKeySelective(outboundOrder);
        if (count == 0){
            String msg = String.format("创建发货单%s后，更新发货通知表取消状态失败",outboundOrderCode);
            logger.error(msg);
            throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
        }
        //找出发货通知单编号下所有记录，更新出库通知单状态
        List<OutboundDetail> list = outboundDetailService.selectByExample(example);
        getOutboundOrderStatusByDetail(list);
    }

    private DeliveryorderCreateRequest setParam(Warehouse warehouse,OutboundOrder outboundOrder,List<OutboundDetail> outboundDetails){
        DeliveryorderCreateRequest request = new DeliveryorderCreateRequest();
        DeliveryorderCreateRequest.DeliveryOrder deliveryOrder =  new DeliveryorderCreateRequest.DeliveryOrder();
        deliveryOrder.setDeliveryOrderCode(outboundOrder.getOutboundOrderCode());
        deliveryOrder.setOrderType(outboundOrder.getOrderType());
        deliveryOrder.setWarehouseCode(outboundOrder.getWarehouseCode());
        deliveryOrder.setCreateTime(DateUtils.formatDateTime(outboundOrder.getCreateTime()));
        deliveryOrder.setPlaceOrderTime(DateUtils.formatDateTime(outboundOrder.getPayTime()));
        deliveryOrder.setOperateTime(DateUtils.formatDateTime(outboundOrder.getCreateTime()));
        deliveryOrder.setShopNick(outboundOrder.getShopName());
        deliveryOrder.setSourcePlatformCode(SupplyConstants.SourcePlatformCodeType.OTHER);
        DeliveryorderCreateRequest.SenderInfo senderInfo = new DeliveryorderCreateRequest.SenderInfo();
        senderInfo.setName(warehouse.getName());
        senderInfo.setMobile(warehouse.getSenderPhoneNumber());
        senderInfo.setProvince(warehouse.getProvince());
        senderInfo.setCity(warehouse.getCity());
        senderInfo.setDetailAddress(warehouse.getAddress());
        deliveryOrder.setSenderInfo(senderInfo);
        DeliveryorderCreateRequest.ReceiverInfo receiverInfo = new DeliveryorderCreateRequest.ReceiverInfo();
        receiverInfo.setName(outboundOrder.getReceiverName());
        receiverInfo.setMobile(outboundOrder.getReceiverPhone());
        receiverInfo.setProvince(outboundOrder.getReceiverProvince());
        receiverInfo.setCity(outboundOrder.getReceiverCity());
        receiverInfo.setDetailAddress(outboundOrder.getReceiverAddress());
        deliveryOrder.setReceiverInfo(receiverInfo);
        deliveryOrder.setSellerMessage(outboundOrder.getSellerMessage());
        deliveryOrder.setBuyerMessage(outboundOrder.getBuyerMessage());
        List<DeliveryorderCreateRequest.OrderLine> orderLines = new ArrayList<>();
        for (OutboundDetail outboundDetail : outboundDetails){
            DeliveryorderCreateRequest.OrderLine orderLine = new DeliveryorderCreateRequest.OrderLine();
            orderLine.setOwnerCode(outboundOrder.getChannelCode());
            orderLine.setItemCode(outboundDetail.getSkuCode());
            orderLine.setInventoryType(outboundDetail.getInventoryType());
            orderLine.setPlanQty(String.valueOf(outboundDetail.getShouldSentItemNum()));
            orderLine.setActualPrice(String.valueOf(CommonUtil.fenToYuan(outboundDetail.getActualAmount())));
            orderLines.add(orderLine);
        }
        deliveryOrder.setOrderLines(orderLines);
        request.setDeliveryOrder(deliveryOrder);
        return request;
    }

    @Override
    @CacheEvit
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Response orderCancel(Long id, String remark) {
        AssertUtil.notNull(id, "发货单主键不能为空");
        AssertUtil.notBlank(remark, "取消原因不能为空");

        //获取发货单信息
        OutboundOrder outboundOrder = outBoundOrderService.selectByPrimaryKey(id);

        if(!StringUtils.equals(outboundOrder.getStatus(), OutboundOrderStatusEnum.WAITING.getCode())){
            String msg = "发货通知单状态必须为等待仓库发货!";
            logger.error(msg);
            throw new OutboundOrderException(ExceptionEnum.OUTBOUND_ORDER_EXCEPTION, msg);
        }

        //获取仓库信息
        Warehouse warehouse =warehouseService.selectByPrimaryKey(outboundOrder.getWarehouseId());

        //组装请求
        OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        orderCancelRequest.setCancelReason(remark);
        orderCancelRequest.setOrderCode(outboundOrder.getOutboundOrderCode());
        orderCancelRequest.setWarehouseCode(warehouse.getQimenWarehouseCode());

        //调用奇门接口
        AppResult<OrderCancelResponse> appResult = qimenService.orderCancel(orderCancelRequest);

        //处理信息
        if (StringUtils.equals(appResult.getAppcode(), SUCCESS)) { // 成功
            this.updateDetailStatus(OutboundDetailStatusEnum.CANCELED.getCode(), outboundOrder.getOutboundOrderCode());
            this.updateOrderCancelInfo(outboundOrder, remark,false);
            return ResultUtil.createSuccessResult("发货通知单取消成功！", "");
        } else {
            return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "发货通知单取消失败！", "");
        }
    }

    //修改详情状态
    private void updateDetailStatus(String code, String outboundOrderCode){
        OutboundDetail outboundDetail = new OutboundDetail();
        outboundDetail.setStatus(code);
        outboundDetail.setUpdateTime(Calendar.getInstance().getTime());
        Example exampleOrder = new Example(OutboundDetail.class);
        Example.Criteria criteriaOrder = exampleOrder.createCriteria();
        criteriaOrder.andEqualTo("outboundOrderCode", outboundOrderCode);
        outboundDetailService.updateByExampleSelective(outboundDetail, exampleOrder);
    }

    //修改取消发货单信息
    private void updateOrderCancelInfo(OutboundOrder outboundOrder, String remark, boolean isClose){
        outboundOrder.setStatus(OutboundOrderStatusEnum.CANCELED.getCode());
        if(isClose){
            outboundOrder.setIsClose(ZeroToNineEnum.ONE.getCode());
        }else {
            outboundOrder.setIsCancel(ZeroToNineEnum.ONE.getCode());
        }
        outboundOrder.setUpdateTime(Calendar.getInstance().getTime());
        outboundOrder.setRemark(remark);
        outBoundOrderService.updateByPrimaryKey(outboundOrder);
    }

    private void verifyParam(Warehouse warehouse,OutboundOrder outboundOrder,List<OutboundDetail> outboundDetails){
        AssertUtil.notBlank(outboundOrder.getOutboundOrderCode(),"出库通知单编号不能为空");
        AssertUtil.notBlank(outboundOrder.getOrderType(),"出库单类型不能为空");
        AssertUtil.notBlank(warehouse.getCode(),"仓库编码不能为空");
        AssertUtil.notNull(outboundOrder.getCreateTime(),"发货单创建时间不能为空");
        AssertUtil.notNull(outboundOrder.getPayTime(),"付款时间不能为空");
        AssertUtil.notBlank(outboundOrder.getShopName(),"店铺名称不能为空");
        AssertUtil.notBlank(warehouse.getName(),"发货仓库名称不能为空");
        AssertUtil.notBlank(warehouse.getSenderPhoneNumber(),"运单发件人手机号不能为空");
        AssertUtil.notBlank(warehouse.getProvince(),"发货仓库省份不能为空");
        AssertUtil.notBlank(warehouse.getCity(),"发货仓库城市不能为空");
        AssertUtil.notBlank(warehouse.getAddress(),"发货仓库的详细地址不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverName(),"收件人姓名不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverPhone(),"收件人联系方式不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverProvince(),"收件人省份不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverCity(),"收件人城市不能为空");
        AssertUtil.notBlank(outboundOrder.getReceiverAddress(),"收件人详细地址不能为空");
        AssertUtil.notBlank(outboundOrder.getBuyerMessage(),"买家留言不能为空");
        AssertUtil.notBlank(outboundOrder.getSellerMessage(),"卖家留言不能为空");
        AssertUtil.notBlank(outboundOrder.getChannelCode(),"业务线编码不能为空");
        for (OutboundDetail outboundDetail : outboundDetails){
            AssertUtil.notBlank(outboundDetail.getSkuCode(),"商品sku编号不能为空");
            AssertUtil.notBlank(outboundDetail.getInventoryType(),"库存类型不能为空");
            AssertUtil.notNull(outboundDetail.getShouldSentItemNum(),"应发商品数量不能为空");
            AssertUtil.notNull(outboundDetail.getActualAmount(),"实付总金额不能为空");
        }
    }

    public void setQueryParam(Example example, OutBoundOrderForm form) {
        Example.Criteria criteria = example.createCriteria();
        //发货通知单编号
        if (!StringUtils.isBlank(form.getOutboundOrderCode())) {
            criteria.andLike("outboundOrderCode", "%" + form.getOutboundOrderCode() + "%");

        }
        //店铺订单编号
        if (!StringUtils.isBlank(form.getShopOrderCode())) {
            criteria.andLike("shopOrderCode", "%" + form.getShopOrderCode() + "%");

        }
        //发货仓库id
        if (!StringUtils.isBlank(form.getWarehouseId())) {
            criteria.andEqualTo("warehouseId", form.getWarehouseId());
        }
        //状态
        if (!StringUtils.isBlank(form.getStatus())) {
            criteria.andEqualTo("status", String.valueOf(form.getStatus()));
        }
        //收货人
        if (!StringUtils.isBlank(form.getReceiverName())) {
            criteria.andLike("receiverName", "%" + form.getReceiverName() + "%");

        }
        //付款时间
        if (!StringUtils.isBlank(form.getStartPayDate())) {
            criteria.andGreaterThan("payTime", form.getStartPayDate());
        }
        if (!StringUtils.isBlank(form.getEndPayDate())) {
            criteria.andLessThan("payTime", DateUtils.formatDateTime(DateUtils.addDays(form.getEndPayDate(), DateUtils.NORMAL_DATE_FORMAT, 1)));
        }
        //平台订单编号
        if (!StringUtils.isBlank(form.getPlatformOrderCode())) {
            criteria.andLike("platformOrderCode", "%" + form.getPlatformOrderCode() + "%");

        }
        //发货单创建日期
        if (!StringUtils.isBlank(form.getStartCreateDate())) {
            criteria.andGreaterThan("createTime", form.getStartCreateDate());
        }
        if (!StringUtils.isBlank(form.getEndCreateDate())) {
            criteria.andLessThan("createTime", DateUtils.formatDateTime(DateUtils.addDays(form.getEndCreateDate(), DateUtils.NORMAL_DATE_FORMAT, 1)));
        }
        example.orderBy("status").asc();
        example.orderBy("createTime").desc();
    }

    //获取状态
    private String getOutboundOrderStatusByDetail(List<OutboundDetail> outboundDetailList){
        int failureNum = 0;//仓库接收失败数
        int waitDeliverNum = 0;//等待发货数
        int allDeliverNum = 0;//全部发货数
        int partsDeliverNum = 0;//部分发货数
        int cancelNum = 0;//已取消数
        for(OutboundDetail detail : outboundDetailList){
            if(StringUtils.equals(OutboundDetailStatusEnum.RECEIVE_FAIL.getCode(), detail.getStatus()))
                failureNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.WAITING.getCode(), detail.getStatus()))
                waitDeliverNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.ALL_GOODS.getCode(), detail.getStatus()))
                allDeliverNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.PART_OF_SHIPMENT.getCode(), detail.getStatus()))
                partsDeliverNum++;
            else if(StringUtils.equals(OutboundDetailStatusEnum.CANCELED.getCode(), detail.getStatus()))
                cancelNum++;
        }
        //已取消：所有商品的发货状态均更新为“已取消”时，发货单的状态就更新为“已取消”；
        if(cancelNum == outboundDetailList.size()){
            return OutboundOrderStatusEnum.CANCELED.getCode();
        }
        //仓库接收失败：所有商品的发货状态均为“仓库接收失败”时，发货单的状态就为“仓库接收失败”
        if(failureNum == outboundDetailList.size()){
            return OutboundOrderStatusEnum.RECEIVE_FAIL.getCode();
        }
        //全部发货：所有商品的发货状态均为“全部发货”时，发货单的状态就为“全部发货”
        if(allDeliverNum == outboundDetailList.size()){
            return OutboundOrderStatusEnum.ALL_GOODS.getCode();
        }
        //部分发货：存在发货状态为“部分发货”的商品或者同时存在待发货和已发货(部分发货或全部发货)的商品，发货单的状态就为“部分发货”
        if(partsDeliverNum > 0 || (waitDeliverNum > 0 && (partsDeliverNum > 0 || allDeliverNum > 0))){
            return OutboundOrderStatusEnum.PART_OF_SHIPMENT.getCode();
        }
        return OutboundOrderStatusEnum.WAITING.getCode();
    }


}
