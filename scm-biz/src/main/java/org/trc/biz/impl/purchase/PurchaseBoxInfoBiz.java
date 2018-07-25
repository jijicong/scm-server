package org.trc.biz.impl.purchase;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.purchase.IPurchaseBoxInfoBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.PurchaseBoxInfoVO;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.service.purchase.IPurchaseBoxInfoService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.util.AssertUtil;

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
//            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
        }

//        int count = 0;
//        //根据用户的id查询渠道
//        purchaseOrder.setChannelCode(aclUserAccreditInfo.getChannelCode());
//        purchaseOrder.setPurchaseOrderCode(code);
//        purchaseOrder.setIsValid(ValidEnum.VALID.getCode());
//        purchaseOrder.setStatus(status);//设置状态
//        //如果提交采购单的方式为提交审核--则校验必须的数据
//        if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(status)){
//            assertArgs(purchaseOrder);
//        }
////        assertArgs(purchaseOrder);
//        if(purchaseOrder.getTotalFeeD() != null){
//            purchaseOrder.setTotalFee(purchaseOrder.getTotalFeeD().multiply(new BigDecimal(100)).longValue());//设置总价格*100
//        }
//        BigDecimal paymentProportion = purchaseOrder.getPaymentProportion();
//        if(paymentProportion!=null){
//            BigDecimal bd = new BigDecimal("100");
//            paymentProportion=paymentProportion.divide(bd);
//            if(paymentProportion.doubleValue()>1 || paymentProportion.doubleValue()<=0){ //范围校验
//                String msg = "采购单保存,付款比例超出范围";
//                LOGGER.error(msg);
//                throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
//            }
//            purchaseOrder.setPaymentProportion(paymentProportion);
//        }
//        //格式化时间
//        this.formatDate(purchaseOrder);
//
//        count = purchaseOrderService.insert(purchaseOrder);
//        if (count<1){
//            String msg = "采购单保存,数据库操作失败";
//            LOGGER.error(msg);
//            throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
//        }
//        String purchaseOrderStrs = purchaseOrder.getGridValue();//采购商品详情的字符串
//
//        Long orderId = purchaseOrder.getId();
//
//        code = purchaseOrder.getPurchaseOrderCode();
//
//        if(StringUtils.isNotBlank(purchaseOrderStrs) && !"[]".equals(purchaseOrderStrs)){
//            BigDecimal totalPrice = savePurchaseDetail(purchaseOrderStrs,orderId,code,purchaseOrder.getCreateOperator(),status);//保存采购商品
//            if(PurchaseOrderStatusEnum.AUDIT.getCode().equals(status)){//提交审核做金额校验
//                if(totalPrice.compareTo(purchaseOrder.getTotalFeeD()) != 0){//比较实际采购价格与页面传输的价格是否相等
//                    String msg = "采购单保存,采购商品的总价与页面的总价不相等";
//                    LOGGER.error(msg);
//                    throw new PurchaseOrderException(ExceptionEnum.PURCHASE_PURCHASE_ORDER_SAVE_EXCEPTION, msg);
//                }
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
}
