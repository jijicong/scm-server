package org.trc.biz.impl.purchase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.storage.model.DefaultPutRet;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.purchase.IPurchaseBoxInfoBiz;
import org.trc.config.BaseThumbnailSize;
import org.trc.constants.SupplyConstants;
import org.trc.domain.dict.Dict;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.purchase.*;
import org.trc.enums.*;
import org.trc.enums.purchase.PurchaseBoxInfoStatusEnum;
import org.trc.exception.FileException;
import org.trc.exception.PurchaseBoxInfoException;
import org.trc.service.IQinniuService;
import org.trc.service.config.IDictService;
import org.trc.service.impl.QinniuService;
import org.trc.service.impl.config.LogInfoService;
import org.trc.service.purchase.IPurchaseBoxInfoService;
import org.trc.service.purchase.IPurchaseDetailService;
import org.trc.service.purchase.IPurchaseOrderService;
import org.trc.util.AssertUtil;
import org.trc.util.CommonUtil;
import org.trc.util.ParamsUtil;
import org.trc.util.cache.PurchaseOrderCacheEvict;

import java.io.InputStream;
import java.util.*;

/**
 * Created by hzcyn on 2018/7/25.
 */
@Service("purchaseBoxInfoBiz")
public class PurchaseBoxInfoBiz implements IPurchaseBoxInfoBiz{

    private Logger logger = LoggerFactory.getLogger(PurchaseBoxInfoBiz.class);

    public static final String PACKING_TYPE = "packingType";

    @Autowired
    private IPurchaseBoxInfoService purchaseBoxInfoService;
    @Autowired
    private IPurchaseOrderService purchaseOrderService;
    @Autowired
    private LogInfoService logInfoService;
    @Autowired
    private IPurchaseDetailService purchaseDetailService;
    @Autowired
    private IDictService dictService;
    @Autowired
    private IQinniuService qinniuService;

    /**
     * 保存装箱信息
     * @param purchaseBoxInfoVO
     * @param status
     * @param aclUserAccreditInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @PurchaseOrderCacheEvict
    public void savePurchaseBoxInfo(PurchaseBoxInfoVO purchaseBoxInfoVO, String status, AclUserAccreditInfo aclUserAccreditInfo)  {
        //校验信息完整性
        AssertUtil.notNull(purchaseBoxInfoVO,"装箱信息对象为空");
        String purchaseOrderCode = purchaseBoxInfoVO.getPurchaseOrderCode();
        AssertUtil.notBlank(purchaseOrderCode, "采购单号为空");
        if(StringUtils.equals(PurchaseBoxInfoStatusEnum.FINISH.getCode(), status)){
            AssertUtil.notBlank(purchaseBoxInfoVO.getPackingType(),"装箱方式不能为空");
        }

        //获取采购单信息并
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderCode(purchaseOrderCode);
        purchaseOrder = purchaseOrderService.selectOne(purchaseOrder);
        if(purchaseOrder == null){
            String msg = String.format("根据采购单号[%s]查询不到对应的采购单", purchaseOrderCode);
            logger.error(msg);
            throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
        }

        //单据状态"为“暂存”、“提交审核”、“审核驳回”、“审核通过”时才允许操作
        String purchaseOrderStatus = purchaseOrder.getStatus();
        if(!(PurchaseOrderStatusEnum.HOLD.getCode().equals(purchaseOrderStatus) ||
                PurchaseOrderStatusEnum.AUDIT.getCode().equals(purchaseOrderStatus) ||
                PurchaseOrderStatusEnum.REJECT.getCode().equals(purchaseOrderStatus) ||
                PurchaseOrderStatusEnum.PASS.getCode().equals(purchaseOrderStatus))){
            String msg = "单据状态为“暂存”、“提交审核”、“审核驳回”、“审核通过”时才允许操作";
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
        int count = purchaseOrderService.updateByPrimaryKeySelective(purchaseOrderUpdate);
        if (count<1){
            String msg = "采购单保存,数据库操作失败";
            logger.error(msg);
            throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
        }

        //保存装箱信息详情
        String purchaseBoxInfoListJSON = purchaseBoxInfoVO.getPurchaseBoxInfoListJSON();
        List<PurchaseBoxInfo> purchaseBoxInfoList = new ArrayList<>();
        if(StringUtils.isNotEmpty(purchaseBoxInfoListJSON)){
            purchaseBoxInfoList = JSON.parseArray(purchaseBoxInfoListJSON, PurchaseBoxInfo.class);
        }
        if(StringUtils.equals(PurchaseBoxInfoStatusEnum.FINISH.getCode(), status)){
            this.checkInfo(purchaseBoxInfoList);
            this.checkPurchaseBoxInfoDetail(purchaseBoxInfoList, purchaseOrderCode);
            this.savePackingType(purchaseBoxInfoVO.getPackingType(), purchaseOrder.getCreateOperator());
        }
        PurchaseBoxInfo purchaseBoxInfoOld = new PurchaseBoxInfo();
        purchaseBoxInfoOld.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        purchaseBoxInfoOld.setPurchaseOrderCode(purchaseOrderCode);
        List<PurchaseBoxInfo> purchaseBoxInfoListOld = purchaseBoxInfoService.select(purchaseBoxInfoOld);
        this.savePurchaseBoxInfoDetail(purchaseBoxInfoList, purchaseBoxInfoListOld, purchaseOrder.getCreateOperator(),
                purchaseOrderCode);

        //保存操作日志
        String userId= aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(purchaseOrder, purchaseOrder.getId().toString(), userId,
                LogOperationEnum.PURCHASE_BOX_INFO.getMessage(),null, ZeroToNineEnum.ZERO.getCode());
    }

    /**
     * 获取包装方式
     * @return
     */
    @Override
    public List<Dict> findPackingType() {
        Dict dict = new Dict();
        dict.setTypeCode(PACKING_TYPE);
        return dictService.select(dict);
    }

    /**
     * 获取装箱信息
     * @param code
     * @return
     */
    @Override
    public PurchaseBoxInfoResultVO findPackingBoxInfo(String code) {
        //校验信息完整性
        AssertUtil.notBlank(code,"采购单编码为空");

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderCode(code);
        purchaseOrder = purchaseOrderService.selectOne(purchaseOrder);
        if (purchaseOrder == null){
            String msg = "采购单不存在";
            logger.error(msg);
            throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
        }

        PurchaseBoxInfoResultVO purchaseBoxInfoResultVO = new PurchaseBoxInfoResultVO();
        purchaseBoxInfoResultVO.setPackingType(purchaseOrder.getPackingType());
        purchaseBoxInfoResultVO.setLogisticsCorporationName(purchaseOrder.getLogisticsCorporationName());

        //获取装箱信息
        PurchaseBoxInfo purchaseBoxInfo = new PurchaseBoxInfo();
        purchaseBoxInfo.setPurchaseOrderCode(code);
        purchaseBoxInfo.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        List<PurchaseBoxInfo> purchaseBoxInfoList = purchaseBoxInfoService.select(purchaseBoxInfo);
        Map<String, List<PurchaseBoxInfo>> boxInfoMap = new HashMap<>();
        if(purchaseBoxInfoList != null && purchaseBoxInfoList.size() > 0){
            for(PurchaseBoxInfo purchaseBoxInfoTemp : purchaseBoxInfoList) {
                String skuCode = purchaseBoxInfoTemp.getSkuCode();
                if (boxInfoMap.containsKey(skuCode)) {
                    List<PurchaseBoxInfo> purchaseBoxInfoListTemp = boxInfoMap.get(skuCode);
                    purchaseBoxInfoListTemp.add(purchaseBoxInfoTemp);
                    boxInfoMap.put(skuCode, purchaseBoxInfoListTemp);
                } else {
                    List<PurchaseBoxInfo> purchaseBoxInfoListNew = new ArrayList<>();
                    purchaseBoxInfoListNew.add(purchaseBoxInfoTemp);
                    boxInfoMap.put(skuCode, purchaseBoxInfoListNew);
                }
            }
        }

        PurchaseDetail purchaseDetail = new PurchaseDetail();
        purchaseDetail.setPurchaseOrderCode(code);
        List<PurchaseDetail> purchaseDetailList = purchaseDetailService.select(purchaseDetail);
        List<PurchaseBoxInfoDetailResultVO> purchaseBoxInfoDetailResultVOList = new ArrayList<>();
        if(purchaseDetailList != null && purchaseDetailList.size() > 0){
            for(PurchaseDetail purchaseDetailTemp : purchaseDetailList){
                PurchaseBoxInfoDetailResultVO purchaseBoxInfoDetailResultVO = new PurchaseBoxInfoDetailResultVO();
                String skuCode = purchaseDetailTemp.getSkuCode();
                purchaseBoxInfoDetailResultVO.setBarCode(purchaseDetailTemp.getBarCode());
                purchaseBoxInfoDetailResultVO.setSkuCode(skuCode);
                purchaseBoxInfoDetailResultVO.setSkuName(purchaseDetailTemp.getSkuName());
                purchaseBoxInfoDetailResultVO.setPurchasingQuantity(
                        purchaseDetailTemp.getPurchasingQuantity()==null?"0":purchaseDetailTemp.getPurchasingQuantity().toString());
                purchaseBoxInfoDetailResultVO.setSpecNatureInfo(purchaseDetailTemp.getSpecNatureInfo());
                if(boxInfoMap.containsKey(skuCode)){
                    purchaseBoxInfoDetailResultVO.setPurchaseBoxInfoList(boxInfoMap.get(skuCode));
                }
                purchaseBoxInfoDetailResultVOList.add(purchaseBoxInfoDetailResultVO);
            }
        }

        purchaseBoxInfoResultVO.setPurchaseBoxInfoDetailResultVOList(purchaseBoxInfoDetailResultVOList);

        return purchaseBoxInfoResultVO;
    }

    /**
     * 保存包装方式
     * @param packingType
     * @param createOperator
     */
    private void savePackingType(String packingType,String createOperator){
        Dict dict = new Dict();
        dict.setTypeCode(PACKING_TYPE);
        List<Dict> dictList = dictService.select(dict);
        boolean flag = true;
        for(Dict dictTemp : dictList){
            if(StringUtils.equals(packingType, dictTemp.getName())){
                flag = false;
            }
        }

        if(flag){
            Dict save = new Dict();
            ParamsUtil.setBaseDO(save);
            save.setTypeCode(PACKING_TYPE);
            save.setValue(packingType);
            save.setName(packingType);
            save.setCreateOperator(createOperator);
            dictService.insert(save);
        }
    }

    private void checkInfo(List<PurchaseBoxInfo> purchaseBoxInfoList){
        if(purchaseBoxInfoList.isEmpty()){
            String msg = "装箱信息不能为空";
            logger.error(msg);
            throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
        }

        for(PurchaseBoxInfo purchaseBoxInfo : purchaseBoxInfoList){
            if (StringUtils.isEmpty(purchaseBoxInfo.getBoxNumber())){
                String msg = "箱号不能为空";
                logger.error(msg);
                throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
            }

            if (StringUtils.isEmpty(purchaseBoxInfo.getGrossWeight())){
                String msg = "毛重不能为空";
                logger.error(msg);
                throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
            }

            if (StringUtils.isEmpty(purchaseBoxInfo.getCartonSize())){
                String msg = "外箱尺寸不能为空";
                logger.error(msg);
                throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
            }

            if (StringUtils.isEmpty(purchaseBoxInfo.getVolume())){
                String msg = "体积不能为空";
                logger.error(msg);
                throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
            }
        }
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
            purchaseDetail = purchaseDetailService.selectOne(purchaseDetail);

            if(purchaseDetail == null ){
                String msg = String.format("装箱信息中采购单详情sku为[%s]信息不存在", entry.getKey());
                logger.error(msg);
                throw new PurchaseBoxInfoException(ExceptionEnum.PURCHASE_PURCHASE_BOX_INFO_SAVE_EXCEPTION, msg);
            }else{
                if((purchaseDetail.getPurchasingQuantity()==null?0L:purchaseDetail.getPurchasingQuantity().longValue())
                        != entry.getValue().longValue()){
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
    private void savePurchaseBoxInfoDetail(List<PurchaseBoxInfo> purchaseBoxInfoList,
                                           List<PurchaseBoxInfo> purchaseBoxInfoListOld, String createOperator,
                                           String purchaseOrderCode){
        List<PurchaseBoxInfo> saveInfo = new ArrayList<>(purchaseBoxInfoList);

        for(PurchaseBoxInfo purchaseBoxInfoOld : purchaseBoxInfoListOld){
            boolean isDelete = true;

            for(PurchaseBoxInfo purchaseBoxInfoNew : purchaseBoxInfoList){
                if(purchaseBoxInfoNew.getId() != null &&
                        purchaseBoxInfoNew.getId().longValue() == purchaseBoxInfoOld.getId().longValue()){
                    isDelete = false;
                    if(this.checkIsUpdate(purchaseBoxInfoOld, purchaseBoxInfoNew)){
                        purchaseBoxInfoNew.setUpdateTime(Calendar.getInstance().getTime());
                        purchaseBoxInfoService.updateByPrimaryKeySelective(purchaseBoxInfoNew);
                    }
                    saveInfo.remove(purchaseBoxInfoNew);
                }
            }

            if(isDelete){
                PurchaseBoxInfo delete = new PurchaseBoxInfo();
                delete.setId(purchaseBoxInfoOld.getId());
                delete.setIsDeleted(ZeroToNineEnum.ONE.getCode());
                delete.setUpdateTime(Calendar.getInstance().getTime());
                purchaseBoxInfoService.updateByPrimaryKeySelective(delete);
            }
        }

        for (PurchaseBoxInfo purchaseBoxInfo : saveInfo) {
            ParamsUtil.setBaseDO(purchaseBoxInfo);
            purchaseBoxInfo.setCreateOperator(createOperator);
            purchaseBoxInfo.setPurchaseOrderCode(purchaseOrderCode);
            purchaseBoxInfoService.insert(purchaseBoxInfo);
        }
    }

    private boolean checkIsUpdate(PurchaseBoxInfo oldInfo, PurchaseBoxInfo newInfo){
        if((oldInfo.getAmountPerBox() == null ? 0 : oldInfo.getAmountPerBox().longValue()) !=
                (newInfo.getAmountPerBox() == null ? 0 : newInfo.getAmountPerBox().longValue())){
            return true;
        }
        if(!StringUtils.equals(oldInfo.getBoxNumber(), newInfo.getBoxNumber())){
            return true;
        }
        if((oldInfo.getBoxAmount() == null ? 0 : oldInfo.getBoxAmount().longValue()) !=
                (newInfo.getBoxAmount() == null ? 0 : newInfo.getBoxAmount().longValue())){
            return true;
        }
        if((oldInfo.getAmount() == null ? 0 : oldInfo.getAmount().longValue()) !=
                (newInfo.getAmount() == null ? 0 : newInfo.getAmount().longValue())){
            return true;
        }
        if(!StringUtils.equals(oldInfo.getGrossWeight(), newInfo.getGrossWeight())){
            return true;
        }
        if(!StringUtils.equals(oldInfo.getCartonSize(), newInfo.getCartonSize())){
            return true;
        }
        if(!StringUtils.equals(oldInfo.getVolume(), newInfo.getVolume())){
            return true;
        }
        if(!StringUtils.equals(oldInfo.getRemark(), newInfo.getRemark())){
            return true;
        }
        return false;
    }
}
