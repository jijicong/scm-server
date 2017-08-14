package org.trc.biz.impl.trc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.requestFlow.IRequestFlowBiz;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constant.RequestFlowConstant;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.goods.*;
import org.trc.enums.*;
import org.trc.exception.TrcException;
import org.trc.form.TrcConfig;
import org.trc.form.TrcParam;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.model.BrandToTrcDO;
import org.trc.model.CategoryToTrcDO;
import org.trc.model.PropertyToTrcDO;
import org.trc.model.ToGlyResultDO;
import org.trc.service.ITrcService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.ISkuRelationService;
import org.trc.service.goods.ISkusService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * 泰然城交互
 * Created by hzdzf on 2017/6/7.
 */
@Service("trcBiz")
public class TrcBiz implements ITrcBiz {

    private Logger logger = LoggerFactory.getLogger(TrcBiz.class);

    @Autowired
    private ITrcService trcService;
    @Autowired
    private IRequestFlowService requestFlowService;
    @Autowired
    private ISkuRelationService skuRelationService;
    @Autowired
    private IExternalItemSkuService externalItemSkuService;
    @Resource
    private ISkusService skusService;
    @Autowired
    private TrcConfig trcConfig;
    @Autowired
    private IRequestFlowBiz requestFlowBiz;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void addRequestFlow(String requester, String responder, String type, String requestNum, String status, String requestParam, String responseParam, Date requestTime, String remark) throws Exception {
        RequestFlow requestFlow = new RequestFlow(requester, responder, type, requestNum, status, requestParam, responseParam, requestTime, remark);
        requestFlowService.insert(requestFlow);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendBrand(TrcActionTypeEnum action, Brand oldBrand, Brand brand, long operateTime) throws Exception {
        AssertUtil.notBlank(brand.getBrandCode(), "品牌编码不能为空");
        AssertUtil.notBlank(brand.getIsValid(), "是否停用不能为空");
        AssertUtil.notBlank(brand.getName(), "品牌名称不能为空");
        //判断是否通知
        if (!action.equals(TrcActionTypeEnum.ADD_BRAND)) {
            if (oldBrand.getName().equals(brand.getName()) && oldBrand.getIsValid().equals(brand.getIsValid())) {
                return new ToGlyResultDO("1", "无需通知品牌变更");
            }
        }
        BrandToTrcDO brandToTrc = new BrandToTrcDO();
        brandToTrc.setAlise(brand.getAlise());
        brandToTrc.setBrandCode(brand.getBrandCode());
        brandToTrc.setIsValid(brand.getIsValid());
        brandToTrc.setLogo(brand.getLogo());
        brandToTrc.setName(brand.getName());
        brandToTrc.setWebUrl(brand.getWebUrl());
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), action);
        JSONObject params = (JSONObject)JSONObject.toJSON(trcParam);
        params.put("brandToTrc", brandToTrc);
        logger.info("请求数据: " + params.toJSONString());
        //记录流水
        RequestFlow requestFlow = new RequestFlow();
        requestFlow.setRequester(RequestFlowConstant.GYL);
        requestFlow.setResponder(RequestFlowConstant.TRC);
        requestFlow.setType(RequestFlowTypeEnum.BRAND_UPDATE_NOTICE.getCode());
        requestFlow.setRequestTime(Calendar.getInstance().getTime());
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.GYL);
        requestFlow.setRequestNum(requestNum);
        requestFlow.setStatus(RequestFlowStatusEnum.SEND_INITIAL.getCode());
        requestFlow.setRequestParam(params.toJSONString());
        requestFlowService.insert(requestFlow);
        RequestFlow requestFlowUpdate = new RequestFlow();
        requestFlowUpdate.setRequestNum(requestNum);
        ToGlyResultDO toGlyResultDO = trcService.sendBrandNotice(trcConfig.getBrandUrl(), params.toJSONString());
        //保存请求流水
        requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
        if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("品牌%s更新通知渠道失败,渠道返回错误信息:%s", JSON.toJSONString(oldBrand), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("品牌%s更新通知渠道超时,渠道返回错误信息:%s", JSON.toJSONString(oldBrand), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("品牌%s更新通知渠道成功,渠道返回错误信息:%s", JSON.toJSONString(oldBrand), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("品牌%s更新通知渠道错误,渠道返回错误信息:%s", JSON.toJSONString(oldBrand), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_ERROR.getCode());
        }
        int count = requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
        if (count<=0){
            logger.error("时间："+ DateUtils.formatDateTime(Calendar.getInstance().getTime())+",失败原因：更新流水表状态失败！");
        }
        return toGlyResultDO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendProperty(TrcActionTypeEnum action, Property oldProperty, Property property, List<PropertyValue> oldValueList, List<PropertyValue> valueList, long operateTime) throws Exception {
        AssertUtil.notNull(property.getSort(), "属性排序不能为空");
        AssertUtil.notBlank(property.getName(), "属性名称不能为空");
        AssertUtil.notBlank(property.getIsValid(), "属性是否停用不能为空");
        AssertUtil.notBlank(property.getTypeCode(), "属性类型编码不能为空");
        AssertUtil.notBlank(property.getValueType(), "属性值类型不能为空");
        if (!action.equals(TrcActionTypeEnum.ADD_PROPERTY)) {//更新
            //判断是否通知
            if(!propertyNotice(oldProperty, property, oldValueList, valueList)){
                return new ToGlyResultDO(SuccessFailureEnum.SUCCESS.getCode(), "不需要通知渠道");
            }
        }
        PropertyToTrcDO propertyToTrc = new PropertyToTrcDO();
        propertyToTrc.setSort(property.getSort());
        propertyToTrc.setName(property.getName());
        propertyToTrc.setIsValid(property.getIsValid());
        propertyToTrc.setDescription(property.getDescription());
        propertyToTrc.setTypeCode(property.getTypeCode());
        propertyToTrc.setValueType(property.getValueType());
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), action);
        JSONObject params = (JSONObject)JSONObject.toJSON(trcParam);
        params.put("propertyToTrc", propertyToTrc);
        params.put("valueList", valueList);
        logger.info("请求数据: " + params.toJSONString());
        //记录流水
        RequestFlow requestFlow = new RequestFlow();
        requestFlow.setRequester(RequestFlowConstant.GYL);
        requestFlow.setResponder(RequestFlowConstant.TRC);
        requestFlow.setType(RequestFlowTypeEnum.PROPERTY_UPDATE_NOTICE.getCode());
        requestFlow.setRequestTime(Calendar.getInstance().getTime());
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.GYL);
        requestFlow.setRequestNum(requestNum);
        requestFlow.setStatus(RequestFlowStatusEnum.SEND_INITIAL.getCode());
        requestFlow.setRequestParam(params.toJSONString());
        requestFlowService.insert(requestFlow);
        RequestFlow requestFlowUpdate = new RequestFlow();
        requestFlowUpdate.setRequestNum(requestNum);
        ToGlyResultDO toGlyResultDO = trcService.sendPropertyNotice(trcConfig.getPropertyUrl(), params.toJSONString());
        //保存请求流水
        requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
        if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("属性%s更新通知渠道失败,渠道返回错误信息:%s", JSON.toJSONString(oldProperty), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("属性%s更新通知渠道超时,渠道返回错误信息:%s", JSON.toJSONString(oldProperty), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("属性%s更新通知渠道成功,渠道返回错误信息:%s", JSON.toJSONString(oldProperty), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("属性%s更新通知渠道错误,渠道返回错误信息:%s", JSON.toJSONString(oldProperty), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_ERROR.getCode());
        }
        int count = requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
        if (count<=0){
            logger.error("时间："+ DateUtils.formatDateTime(Calendar.getInstance().getTime())+",失败原因：更新流水表状态失败！");
        }
        return toGlyResultDO;
    }

    /**
     * 判断更新属性是否需要通知渠道
     * @param oldProperty
     * @param property
     * @param oldValueList
     * @param valueList
     * @return
     */
    private boolean propertyNotice(Property oldProperty, Property property, List<PropertyValue> oldValueList, List<PropertyValue> valueList){
        boolean flag = false;
        if (!oldProperty.getIsValid().equals(property.getIsValid()) || !oldProperty.getName().equals(property.getName())
                || !oldProperty.getValueType().equals(property.getValueType()) || !oldProperty.getTypeCode().equals(property.getTypeCode())) {
            flag = true;
        }else {
            if(oldValueList.size() != valueList.size()){
                flag = true;
            }{
                for(PropertyValue propertyValue: valueList){
                    boolean flag2 = false;
                    for(PropertyValue oldPropertyValue: oldValueList){
                        if(propertyValue.getId().longValue() == oldPropertyValue.getId().longValue()){
                            if(!StringUtils.equals(oldPropertyValue.getValue(), propertyValue.getValue()) ||
                                    !StringUtils.equals(oldPropertyValue.getIsValid(), propertyValue.getIsValid()) ||
                                    !StringUtils.equals(oldPropertyValue.getPicture(), propertyValue.getPicture())){
                                flag2 = true;
                                break;
                            }
                        }
                        if(flag2){
                            flag = true;
                            break;
                        }
                    }
                }
            }
        }
        return flag;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendCategory(TrcActionTypeEnum action, Category oldCategory, Category category, List<CategoryBrand> categoryBrandList, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception {
        if (action.getCode().equals(TrcActionTypeEnum.ADD_CATEGORY.getCode()) || action.getCode().equals(TrcActionTypeEnum.EDIT_CATEGORY.getCode())
                || action.getCode().equals(TrcActionTypeEnum.STOP_CATEGORY.getCode())) {
            return sendCategoryToTrc(action, oldCategory, category, operateTime);
        }
        if (action.getCode().equals(TrcActionTypeEnum.EDIT_CATEGORY_BRAND.getCode()) && !CollectionUtils.isEmpty(categoryBrandList)) {
            return sendCategoryBrandList(action, categoryBrandList, operateTime);
        }
        if (action.getCode().equals(TrcActionTypeEnum.EDIT_CATEGORY_PROPERTY.getCode()) && !CollectionUtils.isEmpty(categoryPropertyList)) {
            return sendCategoryPropertyList(action, categoryPropertyList, operateTime);
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendItem(TrcActionTypeEnum action, Items items, List<ItemNaturePropery> itemNaturePropery, List<ItemSalesPropery> itemSalesPropery, List<Skus> skus, Long operateTime) throws Exception {
        AssertUtil.notNull(items, "自采商品修改通知渠道商品信息参数items不能为空");
        AssertUtil.notEmpty(itemNaturePropery, "自采商品修改通知渠道商品自然属性参数itemNaturePropery不能为空");
        AssertUtil.notEmpty(itemSalesPropery, "自采商品修改通知渠道商品采购属性参数itemNaturePropery不能为空");
        AssertUtil.notEmpty(skus, "自采商品修改通知渠道商品sku参数skus不能为空");
        List<SkuRelation> skuRelationList = new ArrayList<SkuRelation>();
        for(Skus skus2: skus){
            SkuRelation skuRelation = new SkuRelation();
            skuRelation.setSpuCode(skus2.getSpuCode());
            skuRelation.setSkuCode(skus2.getSkuCode());
            skuRelation = skuRelationService.selectOne(skuRelation);
            if(null != skuRelation){
                skuRelationList.add(skuRelation);
            }
        }
        if(skuRelationList.size() == 0){
            return new ToGlyResultDO(SuccessFailureEnum.SUCCESS.getCode(), "商品修改无需同步");
        }
        //MD5加密
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), action);
        JSONObject params = (JSONObject)JSONObject.toJSON(trcParam);
        params.put("items", items);
        params.put("itemNaturePropery", itemNaturePropery);
        params.put("itemSalesPropery", itemSalesPropery);
        params.put("skus", skus);
        logger.info("请求数据: " + params.toJSONString());
        //记录流水
        RequestFlow requestFlow = new RequestFlow();
        requestFlow.setRequester(RequestFlowConstant.GYL);
        requestFlow.setResponder(RequestFlowConstant.TRC);
        requestFlow.setType(RequestFlowTypeEnum.ITEM_UPDATE_NOTICE.getCode());
        requestFlow.setRequestTime(Calendar.getInstance().getTime());
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.GYL);
        requestFlow.setRequestNum(requestNum);
        requestFlow.setStatus(RequestFlowStatusEnum.SEND_INITIAL.getCode());
        requestFlow.setRequestParam(params.toJSONString());
        requestFlowService.insert(requestFlow);
        RequestFlow requestFlowUpdate = new RequestFlow();
        requestFlowUpdate.setRequestNum(requestNum);
        ToGlyResultDO toGlyResultDO = trcService.sendItemsNotice(trcConfig.getItemUrl(), params.toJSONString());
        //保存请求流水
        requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
        if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("自采商品%s更新通知渠道失败,渠道返回错误信息:%s", JSON.toJSONString(items), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("自采商品%s更新通知渠道超时,渠道返回错误信息:%s", JSON.toJSONString(items), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("自采商品%s更新通知渠道成功,渠道返回错误信息:%s", JSON.toJSONString(items), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("自采商品%s更新通知渠道错误,渠道返回错误信息:%s", JSON.toJSONString(items), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_ERROR.getCode());
        }
        int count = requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
        if (count<=0){
            logger.error("时间："+ DateUtils.formatDateTime(Calendar.getInstance().getTime())+",失败原因：更新流水表状态失败！");
        }
        return toGlyResultDO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendExternalItemSkuUpdation(TrcActionTypeEnum action, List<ExternalItemSku> oldExternalItemSkuList,
               List<ExternalItemSku> externalItemSkuList, Long operateTime) throws Exception {
        AssertUtil.notEmpty(oldExternalItemSkuList, "代发商品旧更新列表不能为空");
        AssertUtil.notEmpty(externalItemSkuList, "代发商品更新列表不能为空");
        ToGlyResultDO toGlyResult = new ToGlyResultDO(SuccessFailureEnum.SUCCESS.getCode(), "同步成功");
        //需要推送的sku列表
        List<ExternalItemSku> sendList = new ArrayList<>();
        List<SkuRelation> skuRelationList = new ArrayList<SkuRelation>();
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            SkuRelation skuRelation = new SkuRelation();
            skuRelation.setSkuCode(externalItemSku.getSkuCode());
            skuRelation.setSupplierCode(externalItemSku.getSupplierCode());
            skuRelation = skuRelationService.selectOne(skuRelation);
            if(null != skuRelation){
                skuRelationList.add(skuRelation);
            }
        }
        if(skuRelationList.size() > 0){
            for(ExternalItemSku externalItemSku: oldExternalItemSkuList){
                boolean flag = false;
                for(SkuRelation skuRelation: skuRelationList){
                    if(StringUtils.equals(skuRelation.getSkuCode(), externalItemSku.getSkuCode())){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    oldExternalItemSkuList.remove(externalItemSku);
                }
            }
            for(ExternalItemSku externalItemSku: externalItemSkuList){
                boolean flag = false;
                for(SkuRelation skuRelation: skuRelationList){
                    if(StringUtils.equals(skuRelation.getSkuCode(), externalItemSku.getSkuCode())){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    externalItemSkuList.remove(externalItemSku);
                }
            }
        }else{
            return toGlyResult;
        }
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            for(ExternalItemSku externalItemSku2: oldExternalItemSkuList){
                if(StringUtils.equals(externalItemSku.getSkuCode(), externalItemSku2.getSkuCode())){
                    if(getLongVal(externalItemSku.getSupplierPrice()) != getLongVal(externalItemSku2.getSupplierPrice()) ||
                            getLongVal(externalItemSku.getSupplyPrice()) != getLongVal(externalItemSku2.getSupplyPrice()) ||
                            getLongVal(externalItemSku.getMarketReferencePrice()) != getLongVal(externalItemSku2.getMarketReferencePrice()) ||
                            getLongVal(externalItemSku.getStock()) != getLongVal(externalItemSku2.getStock()) ||
                            !StringUtils.equals(externalItemSku.getBarCode(), externalItemSku2.getBarCode()) ||
                            !StringUtils.equals(externalItemSku.getIsValid(), externalItemSku2.getIsValid())){
                        sendList.add(externalItemSku);
                    }
                }
            }
        }
        if(sendList.size() == 0){
            return toGlyResult;
        }
        //发送数据
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), action);
        JSONObject params = (JSONObject)JSONObject.toJSON(trcParam);
        params.put("externalItemSkuList", sendList);
        //记录流水
        RequestFlow requestFlow = new RequestFlow();
        requestFlow.setRequester(RequestFlowConstant.GYL);
        requestFlow.setResponder(RequestFlowConstant.TRC);
        requestFlow.setType(RequestFlowTypeEnum.EXTERNAL_ITEM_UPDATE_NOTICE.getCode());
        requestFlow.setRequestTime(Calendar.getInstance().getTime());
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.GYL);
        requestFlow.setRequestNum(requestNum);
        requestFlow.setStatus(RequestFlowStatusEnum.SEND_INITIAL.getCode());
        requestFlow.setRequestParam(params.toJSONString());
        requestFlowService.insert(requestFlow);
        RequestFlow requestFlowUpdate = new RequestFlow();
        requestFlowUpdate.setRequestNum(requestNum);
        ToGlyResultDO toGlyResultDO = trcService.sendPropertyNotice(trcConfig.getExternalItemSkuUpdateUrl(), params.toJSONString());
        //保存请求流水
        requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
        if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("代发商品%s更新通知渠道失败,渠道返回错误信息:%s", JSON.toJSONString(oldExternalItemSkuList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("代发商品%s更新通知渠道超时,渠道返回错误信息:%s", JSON.toJSONString(oldExternalItemSkuList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("代发商品%s更新通知渠道成功,渠道返回错误信息:%s", JSON.toJSONString(oldExternalItemSkuList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("代发商品%s更新通知渠道错误,渠道返回错误信息:%s", JSON.toJSONString(oldExternalItemSkuList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_ERROR.getCode());
        }
        int count = requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
        if (count<=0){
            logger.error("时间："+ DateUtils.formatDateTime(Calendar.getInstance().getTime())+",失败原因：更新流水表状态失败！");
        }
        return toGlyResultDO;
    }

    private long getLongVal(Long val){
        if(null == val)
            return 0;
        else{
            return val.longValue();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendLogistic(TrcActionTypeEnum action, String channelPlatformOrderCode, String channelShopOrderCode, String supplierCode, JSONArray jdLogistic, JSONArray waybillNumbers) throws Exception {
        //传值处理
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), action);
        JSONObject params = (JSONObject)JSONObject.toJSON(trcParam);
        params.put("channelPlatformOrderCode", channelPlatformOrderCode);//对应泰然成orderId
        params.put("channelShopOrderCode", channelShopOrderCode);//对应泰然成shopOrderId
        params.put("supplierCode", supplierCode);//供应商编码
        String result = null;
        switch (supplierCode) {
            case SupplyConstants.Order.SUPPLIER_JD_CODE:
                params.put("jdLogistic", jdLogistic);
                break;
            case SupplyConstants.Order.SUPPLIER_LY_CODE:
                params.put("waybillNumbers", waybillNumbers);
                break;
        }
        String remark = "调用方法-TrcBiz类中[通知物流信息接口sendLogistic]";
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_EXTERNALITEMSKU_UPDATE_EXCEPTION.getMessage());
            //存储请求记录
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    trcParam.getNoticeNum(), RequestFlowStatusEnum.SEND_TIME_OUT.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
            throw new TrcException(ExceptionEnum.TRC_EXTERNALITEMSKU_UPDATE_EXCEPTION, "Failure" + remark);
        }
        ToGlyResultDO toGlyResultDO = JSONObject.parseObject(result, ToGlyResultDO.class);
        //存储请求记录
        if (toGlyResultDO.getStatus().equals(ZeroToNineEnum.ZERO.getCode())) {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    trcParam.getNoticeNum(), RequestFlowStatusEnum.SEND_SUCCESS.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        } else {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    trcParam.getNoticeNum(), RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        }
        return toGlyResultDO;
    }

    @Override
    public Pagenation<ExternalItemSku> externalItemSkuPage(ExternalItemSkuForm queryModel, Pagenation<ExternalItemSku> page) throws Exception {

        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        if (queryModel.getSupplierCode() != null) {
            criteria.andEqualTo("supplierCode", queryModel.getSupplierCode());
        }
        if (!StringUtils.isEmpty(queryModel.getSkuCode())) {
            criteria.andEqualTo("skuCode", queryModel.getSkuCode());
        }
        if (!StringUtils.isEmpty(queryModel.getItemName())) {
            criteria.andEqualTo("itemName", queryModel.getItemName());
        }
        example.orderBy("supplierCode").desc();
        return externalItemSkuService.pagination(example, page, queryModel);
    }


    @Override
    public void updateRelation(String action, JSONArray relations) throws Exception {
        try {
            AssertUtil.notBlank(action, "动作参数不能为空");
            AssertUtil.notNull(relations, "关联列表不能为空");
            Boolean flag = action.equals(TrcActionTypeEnum.SKURELATION_REMOVE.getCode()) || action.equals(TrcActionTypeEnum.SKURELATION_EXTERNALSKU_ADD.getCode()) || action.equals(TrcActionTypeEnum.SKURELATION_SKU_ADD.getCode());
            AssertUtil.isTrue(flag, "动作参数类型错误");
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_PARAM_EXCEPTION, e.getMessage());
        }
        List<SkuRelation> skuRelationList = relations.toJavaList(SkuRelation.class);
        for (SkuRelation skuRelation : skuRelationList) {
            String jbo = JSON.toJSONString(skuRelation);
            AssertUtil.notBlank(skuRelation.getSkuCode(), String.format("参数%s中SKU编码skuCode为空", jbo));
            AssertUtil.notBlank(skuRelation.getChannelCode(), String.format("参数%s中渠道编码channelCode为空", jbo));
            AssertUtil.notBlank(skuRelation.getChannelSkuCode(), String.format("参数%s中渠道方SKU编码channelSkuCode为空", jbo));
        }
        //删除关联关系
        if (action.equals(TrcActionTypeEnum.SKURELATION_REMOVE.getCode())) {
            for (SkuRelation skuRelation : skuRelationList) {
                Example example = new Example(SkuRelation.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("skuCode", skuRelation.getSkuCode());
                criteria.andEqualTo("channelCode", skuRelation.getChannelCode());
                skuRelationService.deleteByExample(example);
            }
        }

        List<SkuRelation> skuRelations = new ArrayList<>();
        //一件代发商品批量关联
        if (action.equals(TrcActionTypeEnum.SKURELATION_EXTERNALSKU_ADD.getCode())) {
            Iterator<SkuRelation> iter = skuRelationList.iterator();
            while (iter.hasNext()) {
                SkuRelation skuRelation = iter.next();
                ExternalItemSku externalItemSku = new ExternalItemSku();
                externalItemSku.setSkuCode(skuRelation.getSkuCode());
                externalItemSku = externalItemSkuService.selectOne(externalItemSku);
                AssertUtil.notNull(externalItemSku, String.format("根据sku编码%s查询代发商品为空", skuRelation.getSkuCode()));
                skuRelation.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
                skuRelation.setSupplierCode(externalItemSku.getSupplierCode());
                //设置渠道skuCode为空，允许渠道可以多次添加同一个sku
                skuRelation.setChannelSkuCode(null);
                skuRelations.add(skuRelation);
            }
        }
        //自采商品批量关联
        if (action.equals(TrcActionTypeEnum.SKURELATION_SKU_ADD.getCode())) {
            Iterator<SkuRelation> iter = skuRelationList.iterator();
            while (iter.hasNext()) {
                SkuRelation skuRelation = iter.next();
                Skus skus = new Skus();
                skus.setSkuCode(skuRelation.getSkuCode());
                skus = skusService.selectOne(skus);
                AssertUtil.notNull(skus, String.format("根据sku编码%s查询商品sku为空", skuRelation.getSkuCode()));
                skuRelation.setSpuCode(skus.getSpuCode());
                //设置渠道skuCode为空，允许渠道可以多次添加同一个sku
                skuRelation.setChannelSkuCode(null);
                skuRelations.add(skuRelation);
            }
        }
        if (skuRelations.size() > 0){
            List<SkuRelation> skuRelations2 = new ArrayList<>();
            for(SkuRelation skuRelation: skuRelations){
                SkuRelation skuRelation2 = new SkuRelation();
                skuRelation2.setSkuCode(skuRelation.getSkuCode());
                skuRelation2.setChannelCode(skuRelation.getChannelCode());
                skuRelation2 = skuRelationService.selectOne(skuRelation2);
                if(null == skuRelation2){
                    skuRelations2.add(skuRelation);
                }
            }
            if(skuRelations2.size() > 0){
                skuRelationService.insertList(skuRelations2);
            }
        }
    }


    //发送分类属性改动
    public ToGlyResultDO sendCategoryPropertyList(TrcActionTypeEnum action, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception {
        AssertUtil.notEmpty(categoryPropertyList, "分类属性列表不能为空");
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), action);
        JSONObject params = (JSONObject)JSONObject.toJSON(trcParam);
        params.put("categoryPropertyList", categoryPropertyList);
        logger.info("请求数据: " + params.toJSONString());
        //记录流水
        RequestFlow requestFlow = new RequestFlow();
        requestFlow.setRequester(RequestFlowConstant.GYL);
        requestFlow.setResponder(RequestFlowConstant.TRC);
        requestFlow.setType(RequestFlowTypeEnum.CATEFORY_PROPERTY_UPDATE_NOTICE.getCode());
        requestFlow.setRequestTime(Calendar.getInstance().getTime());
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.GYL);
        requestFlow.setRequestNum(requestNum);
        requestFlow.setStatus(RequestFlowStatusEnum.SEND_INITIAL.getCode());
        requestFlow.setRequestParam(params.toJSONString());
        requestFlowService.insert(requestFlow);
        RequestFlow requestFlowUpdate = new RequestFlow();
        requestFlowUpdate.setRequestNum(requestNum);
        ToGlyResultDO toGlyResultDO = trcService.sendCategoryPropertyList(trcConfig.getCategoryPropertyUrl(), params.toJSONString());
        //保存请求流水
        requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
        if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类属性更新通知渠道失败,渠道返回错误信息:%s", JSON.toJSONString(categoryPropertyList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类属性%s更新通知渠道超时,渠道返回错误信息:%s", JSON.toJSONString(categoryPropertyList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类属性%s更新通知渠道成功,渠道返回错误信息:%s", JSON.toJSONString(categoryPropertyList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类属性%s更新通知渠道错误,渠道返回错误信息:%s", JSON.toJSONString(categoryPropertyList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_ERROR.getCode());
        }
        int count = requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
        if (count<=0){
            logger.error("时间："+ DateUtils.formatDateTime(Calendar.getInstance().getTime())+",失败原因：更新流水表状态失败！");
        }
        return toGlyResultDO;
    }

    //发送分类品牌改动
    public ToGlyResultDO sendCategoryBrandList(TrcActionTypeEnum action, List<CategoryBrand> categoryBrandList, long operateTime) throws Exception {
        AssertUtil.notEmpty(categoryBrandList, "分类品牌列表不能为空");
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), action);
        JSONObject params = (JSONObject)JSONObject.toJSON(trcParam);
        params.put("categoryBrandList", categoryBrandList);
        logger.info("请求数据: " + params.toJSONString());
        //记录流水
        RequestFlow requestFlow = new RequestFlow();
        requestFlow.setRequester(RequestFlowConstant.GYL);
        requestFlow.setResponder(RequestFlowConstant.TRC);
        requestFlow.setType(RequestFlowTypeEnum.CATEFORY_BRAND_UPDATE_NOTICE.getCode());
        requestFlow.setRequestTime(Calendar.getInstance().getTime());
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.GYL);
        requestFlow.setRequestNum(requestNum);
        requestFlow.setStatus(RequestFlowStatusEnum.SEND_INITIAL.getCode());
        requestFlow.setRequestParam(params.toJSONString());
        requestFlowService.insert(requestFlow);
        RequestFlow requestFlowUpdate = new RequestFlow();
        requestFlowUpdate.setRequestNum(requestNum);
        ToGlyResultDO toGlyResultDO = trcService.sendCategoryBrandList(trcConfig.getCategoryBrandUrl(), params.toJSONString());
        //保存请求流水
        requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
        if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类品牌%s更新通知渠道失败,渠道返回错误信息:%s", JSON.toJSONString(categoryBrandList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类品牌%s更新通知渠道超时,渠道返回错误信息:%s", JSON.toJSONString(categoryBrandList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类品牌%s更新通知渠道成功,渠道返回错误信息:%s", JSON.toJSONString(categoryBrandList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类品牌%s更新通知渠道错误,渠道返回错误信息:%s", JSON.toJSONString(categoryBrandList), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_ERROR.getCode());
        }
        int count = requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
        if (count<=0){
            logger.error("时间："+ DateUtils.formatDateTime(Calendar.getInstance().getTime())+",失败原因：更新流水表状态失败！");
        }
        return toGlyResultDO;
    }

    //发送分类改动
    public ToGlyResultDO sendCategoryToTrc(TrcActionTypeEnum action, Category oldCategory, Category category, long operateTime) throws Exception {
        AssertUtil.notBlank(category.getIsValid(), "是否停用不能为空");
        AssertUtil.notBlank(category.getName(), "分类名称不能为空");
        AssertUtil.notNull(category.getSort(), "分类排序不能为空");
        //判断是否通知
        if (!action.getCode().equals(TrcActionTypeEnum.ADD_CATEGORY.getCode())) {
            if (oldCategory.getName().equals(category.getName()) && oldCategory.getIsValid().equals(category.getIsValid())) {
                return new ToGlyResultDO("1", "无需通知分类变更");
            }
        }
        CategoryToTrcDO categoryToTrc = new CategoryToTrcDO();
        categoryToTrc.setIsValid(category.getIsValid());
        categoryToTrc.setName(category.getName());
        categoryToTrc.setClassifyDescribe(category.getClassifyDescribe());
        categoryToTrc.setSort(category.getSort());
        if (category.getParentId() != null) {
            categoryToTrc.setParentId(category.getParentId());
        }
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), action);
        JSONObject params = (JSONObject)JSONObject.toJSON(trcParam);
        params.put("categoryToTrc", categoryToTrc);
        logger.info("请求数据: " + params.toJSONString());
        //记录流水
        RequestFlow requestFlow = new RequestFlow();
        requestFlow.setRequester(RequestFlowConstant.GYL);
        requestFlow.setResponder(RequestFlowConstant.TRC);
        requestFlow.setType(RequestFlowTypeEnum.CATEFORY_UPDATE_NOTICE.getCode());
        requestFlow.setRequestTime(Calendar.getInstance().getTime());
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.GYL);
        requestFlow.setRequestNum(requestNum);
        requestFlow.setStatus(RequestFlowStatusEnum.SEND_INITIAL.getCode());
        requestFlow.setRequestParam(params.toJSONString());
        requestFlowService.insert(requestFlow);
        RequestFlow requestFlowUpdate = new RequestFlow();
        requestFlowUpdate.setRequestNum(requestNum);
        ToGlyResultDO toGlyResultDO = trcService.sendCategoryToTrc(trcConfig.getCategoryUrl(), params.toJSONString());
        //保存请求流水
        requestFlowUpdate.setResponseParam(JSONObject.toJSONString(toGlyResultDO));
        if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类%s变更通知渠道失败,渠道返回错误信息:%s", JSON.toJSONString(oldCategory), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类%s更新通知渠道超时,渠道返回错误信息:%s", JSON.toJSONString(oldCategory), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类%s更新通知渠道成功,渠道返回错误信息:%s", JSON.toJSONString(oldCategory), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("分类%s更新通知渠道错误,渠道返回错误信息:%s", JSON.toJSONString(oldCategory), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_ERROR.getCode());
        }
        int count = requestFlowService.updateRequestFlowByRequestNum(requestFlowUpdate);
        if (count<=0){
            logger.error("时间："+ DateUtils.formatDateTime(Calendar.getInstance().getTime())+",失败原因：更新流水表状态失败！");
        }
        return toGlyResultDO;
    }

}
