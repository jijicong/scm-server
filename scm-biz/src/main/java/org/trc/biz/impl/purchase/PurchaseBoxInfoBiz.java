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
import org.trc.domain.purchase.PurchaseDetail;
import org.trc.domain.purchase.PurchaseOrder;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.enums.purchase.PurchaseBoxInfoStatusEnum;
import org.trc.exception.PurchaseBoxInfoException;
import org.trc.service.impl.config.LogInfoService;
import org.trc.service.purchase.IPurchaseBoxInfoService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.util.AssertUtil;
import org.trc.util.ParamsUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private LogInfoService logInfoService;
    @Autowired
    private IPurchaseDetailService purchaseDetailService;

    /**
     * 保存装箱信息
     * @param purchaseBoxInfoVO
     * @param status
     * @param aclUserAccreditInfo
     */
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

        //保存采购单中装箱信息信息
        PurchaseOrder purchaseOrderUpdate = new PurchaseOrder();
        purchaseOrderUpdate.setId(purchaseOrder.getId());
        purchaseOrderUpdate.setLogisticsCorporationName(purchaseBoxInfoVO.getLogisticsCorporationName());
        purchaseOrderUpdate.setLogisticsCode(purchaseBoxInfoVO.getLogisticsCode());
        purchaseOrderUpdate.setPackingType(purchaseBoxInfoVO.getPackingType());
        purchaseOrderUpdate.setBoxInfoStatus(status);
        purchaseOrderUpdate.setUpdateTime(Calendar.getInstance().getTime());
        int count = purchaseOrderService.updateByPrimaryKey(purchaseOrderUpdate);
        if (count<1){
            String msg = "采购单保存,数据库操作失败";
            logger.error(msg);
            throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
        }

        //保存装箱信息详情
        List<PurchaseBoxInfo> purchaseBoxInfoList = purchaseBoxInfoVO.getPurchaseBoxInfoList();
        if(StringUtils.equals(PurchaseBoxInfoStatusEnum.FINISH.getCode(), status)){
            this.checkPurchaseBoxInfoDetail(purchaseBoxInfoList, purchaseOrderCode);
        }
        this.savePurchaseBoxInfoDetail(purchaseBoxInfoList, purchaseOrder.getCreateOperator());

        //保存操作日志
        String userId= aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(purchaseOrder, purchaseOrder.getId().toString(), userId,
                LogOperationEnum.PURCHASE_BOX_INFO.getMessage(),null, ZeroToNineEnum.ZERO.getCode());
    }

    /**
     * 校验装箱信息是否符合规则
     * @param purchaseBoxInfoList
     */
    private void checkPurchaseBoxInfoDetail(List<PurchaseBoxInfo> purchaseBoxInfoList, String code){
        Map<String, Long> amountMap = new HashMap<>();
        for (PurchaseBoxInfo purchaseBoxInfo : purchaseBoxInfoList) {
            String skuCode = purchaseBoxInfo.getSkuCode();
            if(amountMap.containsKey(skuCode)){
                amountMap.put(skuCode, amountMap.get(skuCode) + purchaseBoxInfo.getAmount());
            }else{
                amountMap.put(skuCode, purchaseBoxInfo.getAmount());
            }
        }

        for (Map.Entry<String, Long> entry : amountMap.entrySet()) {
            PurchaseDetail purchaseDetail = new PurchaseDetail();
            purchaseDetail.setSkuCode(entry.getKey());
            purchaseDetail.setPurchaseOrderCode(code);
            try {
                purchaseDetail = purchaseDetailService.selectOne(purchaseDetail);
            }catch(Exception e){
                String msg = String.format("装箱信息中采购单详情sku为[%s]信息错误，存在多条信息", entry.getKey());
                logger.error(msg);
                throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
            }

            if(purchaseDetail == null ){
                String msg = String.format("装箱信息中采购单详情sku为[%s]信息不存在", entry.getKey());
                logger.error(msg);
                throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
            }else{
                if(purchaseDetail.getPurchasingQuantity().longValue() != entry.getValue().longValue()){
                    String msg = String.format("操作失败，[%s]装箱信息中的合计数量必须等于其采购数量！", entry.getKey());
                    logger.error(msg);
                    throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
                }
            }
        }
    }

    /**
     * 保存装箱信息详情
     * @param purchaseBoxInfoList
     * @param createOperator
     */
    private void savePurchaseBoxInfoDetail(List<PurchaseBoxInfo> purchaseBoxInfoList, String createOperator){
        for (PurchaseBoxInfo purchaseBoxInfo : purchaseBoxInfoList) {
            purchaseBoxInfo.setCreateOperator(createOperator);
            ParamsUtil.setBaseDO(purchaseBoxInfo);
        }
        int count = purchaseBoxInfoService.insertList(purchaseBoxInfoList);
        if (count<1){
            String msg = "装箱信息保存,数据库操作失败";
            logger.error(msg);
            throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
        }
    }
}
