package org.trc.biz.impl.outbound;

import com.qimen.api.request.DeliveryorderConfirmRequest;
import com.qimen.api.request.EntryorderConfirmRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.outbuond.IOutBoundOrderBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.order.OutboundDetail;
import org.trc.domain.order.OutboundDetailLogistics;
import org.trc.domain.order.OutboundOrder;
import org.trc.enums.OutboundDetailStatusEnum;
import org.trc.enums.OutboundOrderStatusEnum;
import org.trc.enums.QimenDeliveryEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.outbound.OutBoundOrderForm;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailLogisticsService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.XmlUtil;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("outBoundOrderBiz")
public class OutBoundOrderBiz implements IOutBoundOrderBiz {

    private Logger logger = LoggerFactory.getLogger(OutBoundOrderBiz.class);

    @Autowired
    private IOutBoundOrderService outBoundOrderService;
    @Autowired
    private IOutboundDetailService outboundDetailService;
    @Autowired
    private IOutboundDetailLogisticsService outboundDetailLogisticsService;

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
        AssertUtil.notBlank(outboundOrderCode, "发货单编号不能为空!");
        OutboundOrder outboundOrder = new OutboundOrder();
        outboundOrder.setOutboundOrderCode(outboundOrderCode);
        outboundOrder = outBoundOrderService.selectOne(outboundOrder);

        //获取所有包裹内商品详情

        if(StringUtils.equals(status, QimenDeliveryEnum.DELIVERED.getCode())){

        }
    }

    private void updateOutboundDetail(List<DeliveryorderConfirmRequest.Package> packageList, String outboundOrderCode){
        List<DeliveryorderConfirmRequest.Item> itemList = null;
        OutboundDetail outboundDetail = null;
        OutboundDetailLogistics outboundDetailLogistics = null;
        List<OutboundDetailLogistics> outboundDetailLogisticsList = null;
        for(DeliveryorderConfirmRequest.Package packageD : packageList){
            itemList = packageD.getItems();
            if(itemList != null){
                for(DeliveryorderConfirmRequest.Item item : itemList){
                    Long sentNum = item.getQuantity();
                    outboundDetail = new OutboundDetail();
                    outboundDetail.setOutboundOrderCode(outboundOrderCode);
                    outboundDetail.setSkuCode(item.getItemCode());
                    outboundDetail = outboundDetailService.selectOne(outboundDetail);

                    outboundDetailLogistics = new OutboundDetailLogistics();
                    outboundDetailLogistics.setOutboundDetailId(outboundDetail.getId());
                    outboundDetailLogisticsList = outboundDetailLogisticsService.select(outboundDetailLogistics);
//                    if(){
//
//                    }
                }
            }
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
