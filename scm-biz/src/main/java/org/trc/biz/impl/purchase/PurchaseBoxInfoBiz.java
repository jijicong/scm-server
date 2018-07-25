package org.trc.biz.impl.purchase;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.purchase.IPurchaseBoxInfoBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseBoxInfo;
import org.trc.domain.purchase.PurchaseBoxInfoVO;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.purchase.PurchaseBoxInfoStatusEnum;
import org.trc.exception.PurchaseBoxInfoException;
import org.trc.service.purchase.IPurchaseBoxInfoService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.util.AssertUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hzcyn on 2018/7/25.
 */
@Service("purchaseBoxInfoBiz")
public class PurchaseBoxInfoBiz implements IPurchaseBoxInfoBiz{

    private Logger logger = LoggerFactory.getLogger(PurchaseBoxInfoBiz.class);

    @Autowired
    private IPurchaseBoxInfoService purchaseBoxInfoService;
    @Autowired
    private IPurchaseOrderService purchaseOrderService;

    //保存采购单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePurchaseBoxInfo(PurchaseBoxInfoVO purchaseBoxInfoVO, String status, AclUserAccreditInfo aclUserAccreditInfo)  {
        //校验信息完整性
        AssertUtil.notNull(purchaseBoxInfoVO,"装箱信息对象为空");
        String purchaseOrderCode = purchaseBoxInfoVO.getPurchaseOrderCode();
        AssertUtil.notBlank(purchaseOrderCode, "采购单号为空");

        //获取采购单信息并
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderCode(purchaseOrderCode);
        purchaseOrder = purchaseOrderService.selectOne(purchaseOrder);
        if(purchaseOrder == null){
            String msg = String.format("根据采购单号[%s]查询不到对应的采购单", purchaseOrderCode);
            logger.error(msg);
            throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
        }

        int count = 0;
        //保存采购单中装箱信息信息
        purchaseOrder.setLogisticsCorporationName(purchaseBoxInfoVO.getLogisticsCorporationName());
        purchaseOrder.setLogisticsCode(purchaseBoxInfoVO.getLogisticsCode());
        purchaseOrder.setPackingType(purchaseBoxInfoVO.getPackingType());
        purchaseOrder.setBoxInfoStatus(status);
        purchaseOrder.setUpdateTime(Calendar.getInstance().getTime());
        count = purchaseOrderService.updateByPrimaryKey(purchaseOrder);
        if (count<1){
            String msg = "采购单保存,数据库操作失败";
            logger.error(msg);
            throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
        }

        //保存装箱信息详情
//        List<PurchaseBoxInfo> purchaseBoxInfoList = purchaseBoxInfoVO.getPurchaseBoxInfoList();
//        BigDecimal totalPrice = savePurchaseDetail(purchaseOrderStrs,orderId,code,purchaseOrder.getCreateOperator(),status);//保存采购商品
//        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(status)){//提交审核做金额校验
//            if(totalPrice.compareTo(purchaseOrder.getTotalFeeD()) != 0){//比较实际采购价格与页面传输的价格是否相等
//                String msg = "采购单保存,采购商品的总价与页面的总价不相等";
//                LOGGER.error(msg);
//                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
//            }
//        }
//        //保存操作日志
//        String userId= aclUserAccreditInfo.getUserId();
//        PurchaseOrder purchaseOrderLog = new PurchaseOrder();
//        purchaseOrderLog.setCreateTime(purchaseOrder.getCreateTime());
//        logInfoService.recordLog(purchaseOrderLog,purchaseOrder.getId().toString(),userId,LogOperationEnum.ADD.getMessage(),null,ZeroToNineEnum.ZERO.getCode());
//
//        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(status)){ //保存提交审核
//            savePurchaseOrderAudit(purchaseOrder,aclUserAccreditInfo);
//        }
    }


    private BigDecimal savePurchaseBoxInfoDetail(List<PurchaseBoxInfo> purchaseBoxInfoList,String code,String createOperator,String status) {
        BigDecimal totalPrice = new BigDecimal(0);
        return null;
//        for (PurchaseBoxInfo purchaseBoxInfo : purchaseBoxInfoList) {
//            if(purchaseDetail.getTotalPurchaseAmountD() != null && purchaseDetail.getTotalPurchaseAmountD().compareTo(BigDecimal.ZERO) >= 0){
//                totalPrice = totalPrice.add(purchaseDetail.getTotalPurchaseAmountD());
//                BigDecimal bd = purchaseDetail.getPurchasePriceD().multiply(new BigDecimal(100));
//                //设置采购价格*100
//                purchaseDetail.setPurchasePrice(bd.longValue());
//            }else {
//                //设置采购价格*100
//                purchaseDetail.setPurchasePrice(null);
//            }
//            if(purchaseDetail.getTotalPurchaseAmountD()!=null){
//                //设置单品的总采购价*100
//                purchaseDetail.setTotalPurchaseAmount(purchaseDetail.getTotalPurchaseAmountD().multiply(new BigDecimal(100)).longValue());
//            } else{
//                //设置单品的总采购价*100
//                purchaseDetail.setTotalPurchaseAmount(null);
//            }
//            purchaseDetail.setPurchaseId(orderId);
//            purchaseDetail.setPurchaseOrderCode(code);
//            purchaseDetail.setCreateOperator(createOperator);
//            this.checkPurchaseDetail(purchaseDetail);
//            ParamsUtil.setBaseDO(purchaseDetail);
//        }
//        int count = 0;
//        count = purchaseDetailService.insertList(purchaseDetailList);
//        if (count<1){
//            String msg = "采购商品保存,数据库操作失败";
//            LOGGER.error(msg);
//            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
//        }
//        return totalPrice;
    }
}
