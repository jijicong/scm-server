package org.trc.biz.impl.trc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constant.RequestFlowConstant;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.*;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.goods.*;
import org.trc.enums.*;
import org.trc.exception.TrcException;
import org.trc.form.TrcConfig;
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

    /*@Value("${trc.key}")
    private String trcConfig.getKey();

    @Value("${trc.brand.url}")
    private String BRAND_URL;

    @Value("${trc.property.url}")
    private String PROPERTY_URL;

    @Value("${trc.category.url}")
    private String CATEGORY_URL;

    @Value("${trc.category.brand.url}")
    private String CATEGORY_BRAND_URL;

    @Value("${trc.category.property.url}")
    private String CATEGORY_PROPERTY_URL;

    @Value("${trc.item.url}")
    private String ITEMS_URL;

    @Value("${trc.externalItemSku.update.information.url}")
    private String EXTERNALITEMSKU_UPDATE_INFROMATION_URL;

    @Value("{trc.send.logistic.url}")
    private String SEND_LOGISTIC_URL;*/

    private static final String OR = "|";

    private static final String UNDER_LINE = "_";

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void addRequestFlow(String requester, String responder, String type, String requestNum, String status, String requestParam, String responseParam, Date requestTime, String remark) throws Exception {
        RequestFlow requestFlow = new RequestFlow(requester, responder, type, requestNum, status, requestParam, responseParam, requestTime, remark);
        requestFlowService.insert(requestFlow);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendBrand(TrcActionTypeEnum action, Brand oldBrand, Brand brand, long operateTime) throws Exception {
        //AssertUtil.notBlank(brand.getAlise(), "品牌别名不能为空");
        AssertUtil.notBlank(brand.getBrandCode(), "品牌编码不能为空");
        AssertUtil.notBlank(brand.getIsValid(), "是否停用不能为空");
        //AssertUtil.notBlank(brand.getLogo(), "图片路径不能为空");
        AssertUtil.notBlank(brand.getName(), "品牌名称不能为空");
        //AssertUtil.notBlank(brand.getWebUrl(), "品牌网址不能为空");
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
        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(trcConfig.getKey()).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
                append(brandToTrc.getAlise()).append(OR).append(brandToTrc.getBrandCode()).append(OR).append(brandToTrc.getIsValid()).append(OR).
                append(brandToTrc.getLogo()).append(OR).append(brandToTrc.getName()).append(OR).append(brandToTrc.getWebUrl());

        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("brandToTrc", brandToTrc);
        logger.info("请求数据: " + params.toJSONString());
        String result = trcService.sendBrandNotice(trcConfig.getBrandUrl(), params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知品牌变更接口sendBrand]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_BRAND_EXCEPTION.getMessage());
            //存储请求记录
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_TIME_OUT.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
            throw new TrcException(ExceptionEnum.TRC_BRAND_EXCEPTION, "Failure:" + remark);
        }
        ToGlyResultDO toGlyResultDO = JSONObject.parseObject(result, ToGlyResultDO.class);
        if (toGlyResultDO.getStatus().equals(ZeroToNineEnum.ZERO.getCode())) {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        } else {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_SUCCESS.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        }
        return toGlyResultDO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendProperty(TrcActionTypeEnum action, Property oldProperty, Property property, List<PropertyValue> oldValueList, List<PropertyValue> valueList, long operateTime) throws Exception {
        AssertUtil.notNull(property.getSort(), "属性排序不能为空");
        AssertUtil.notBlank(property.getName(), "属性名称不能为空");
        AssertUtil.notBlank(property.getIsValid(), "属性是否停用不能为空");
        //AssertUtil.notBlank(property.getDescription(), "属性描述不能为空");
        AssertUtil.notBlank(property.getTypeCode(), "属性类型编码不能为空");
        AssertUtil.notBlank(property.getValueType(), "属性值类型不能为空");
        if (!action.equals(TrcActionTypeEnum.ADD_PROPERTY)) {//更新
            //判断是否通知
            if(!propertyNotice(action, oldProperty, property, oldValueList, valueList)){
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

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(trcConfig.getKey()).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
                append(propertyToTrc.getDescription()).append(OR).append(propertyToTrc.getIsValid()).append(OR).
                append(propertyToTrc.getName()).append(OR).append(propertyToTrc.getSort()).append(OR).append(propertyToTrc.getTypeCode()).
                append(OR).append(propertyToTrc.getValueType());
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("propertyToTrc", propertyToTrc);
        params.put("valueList", valueList);
        logger.info("请求数据: " + params.toJSONString());
        String result = trcService.sendPropertyNotice(trcConfig.getPropertyUrl(), params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知属性变更接口sendProperty]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_PROPERTY_EXCEPTION.getMessage());
            //存储请求记录
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_TIME_OUT.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
            throw new TrcException(ExceptionEnum.TRC_PROPERTY_EXCEPTION, "Failure" + remark);
        }
        ToGlyResultDO toGlyResultDO = JSONObject.parseObject(result, ToGlyResultDO.class);
        //存储请求记录
        if (toGlyResultDO.getStatus().equals(ZeroToNineEnum.ZERO.getCode())) {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        } else {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_SUCCESS.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        }
        return toGlyResultDO;
    }

    /**
     * 判断更新属性是否需要通知渠道
     * @param action
     * @param oldProperty
     * @param property
     * @param oldValueList
     * @param valueList
     * @return
     */
    private boolean propertyNotice(TrcActionTypeEnum action, Property oldProperty, Property property, List<PropertyValue> oldValueList, List<PropertyValue> valueList){
        boolean flag = false;
        if (oldProperty.getIsValid().equals(property.getIsValid()) && oldProperty.getName().equals(property.getName())
                && oldProperty.getValueType().equals(property.getValueType()) && oldProperty.getTypeCode().equals(property.getTypeCode())
                && valueList == null) {
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
        if (action.equals(TrcActionTypeEnum.ADD_CATEGORY) || action.equals(TrcActionTypeEnum.EDIT_CATEGORY)
                || action.equals(TrcActionTypeEnum.STOP_CATEGORY)) {
            return sendCategoryToTrc(action, oldCategory, category, operateTime);
        }
        if (action.equals(TrcActionTypeEnum.EDIT_CATEGORY_BRAND)) {
            return sendCategoryBrandList(action, categoryBrandList, operateTime);
        }
        if (action.equals(TrcActionTypeEnum.EDIT_CATEGORY_PROPERTY)) {
            return sendCategoryPropertyList(action, categoryPropertyList, operateTime);
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendItem(TrcActionTypeEnum action, Items items, List<ItemNaturePropery> itemNaturePropery, List<ItemSalesPropery> itemSalesPropery, List<Skus> skus, Long operateTime) throws Exception {

        //TODO 判断通知，暂时觉得都得通知

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(trcConfig.getKey()).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime);
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("items", items);
        params.put("itemNaturePropery", itemNaturePropery);
        params.put("itemSalesPropery", itemSalesPropery);
        params.put("skus", skus);
        logger.info("请求数据: " + params.toJSONString());
        String result = trcService.sendItemsNotice(trcConfig.getItemUrl(), params.toJSONString());

        String remark = "调用方法-TrcBiz类中[通知商品变更接口sendItem]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_ITEMS_EXCEPTION.getMessage());
            //存储请求记录
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_TIME_OUT.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
            throw new TrcException(ExceptionEnum.TRC_ITEMS_EXCEPTION, "Failure" + remark);
        }
        ToGlyResultDO toGlyResultDO = JSONObject.parseObject(result, ToGlyResultDO.class);
        //存储请求记录
        if (toGlyResultDO.getStatus().equals(ZeroToNineEnum.ZERO.getCode())) {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        } else {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_SUCCESS.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        }
        return toGlyResultDO;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ToGlyResultDO sendExternalItemSkuUpdation(TrcActionTypeEnum action, List<ExternalItemSku> oldExternalItemSkuList, List<ExternalItemSku> externalItemSkuList, Long operateTime) throws Exception {
        AssertUtil.notEmpty(externalItemSkuList, "代发商品更新列表不能为空");
        ToGlyResultDO toGlyResult = new ToGlyResultDO(SuccessFailureEnum.SUCCESS.getCode(), "同步成功");
        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(trcConfig.getKey()).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime);
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        //需要推送的sku列表
        List<ExternalItemSku> sendList = new ArrayList<>();
        if(CollectionUtils.isEmpty(oldExternalItemSkuList)){
            sendList = externalItemSkuList;
        }else{
            Set<String> skuCodeList = new HashSet<>();
            for(ExternalItemSku externalItemSku: externalItemSkuList){
                skuCodeList.add(externalItemSku.getSkuCode());
            }
            Example example = new Example(ExternalItemSku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("skuCode", skuCodeList);
            List<SkuRelation> skuRelationList = skuRelationService.selectByExample(example);
            if(!CollectionUtils.isEmpty(skuRelationList)){
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
                                !StringUtils.equals(externalItemSku.getIsValid(), externalItemSku2.getIsValid())){
                            sendList.add(externalItemSku);
                        }
                    }
                }
            }
        }
        if(sendList.size() == 0){
            return toGlyResult;
        }
        //发送数据
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("externalItemSkuList", sendList);
        String result = trcService.sendPropertyNotice(trcConfig.getExternalItemSkuUpdateUrl(), params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知一件代发商品变更接口sendExternalItemSkuUpdation]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_EXTERNALITEMSKU_UPDATE_EXCEPTION.getMessage());
            //存储请求记录
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_TIME_OUT.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
            throw new TrcException(ExceptionEnum.TRC_EXTERNALITEMSKU_UPDATE_EXCEPTION, "Failure" + remark);
        }
        ToGlyResultDO toGlyResultDO = JSONObject.parseObject(result, ToGlyResultDO.class);
        //存储请求记录
        if (toGlyResultDO.getStatus().equals(ZeroToNineEnum.ZERO.getCode())) {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        } else {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_SUCCESS.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
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
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        JSONObject params = new JSONObject();
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
        //TODO 传输方式需要与渠道方确定 URL注入  SEND_LOGISTIC_URL
        //result = trcService.s
        String remark = "调用方法-TrcBiz类中[通知物流信息接口sendLogistic]";
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_EXTERNALITEMSKU_UPDATE_EXCEPTION.getMessage());
            //存储请求记录
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_TIME_OUT.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
            throw new TrcException(ExceptionEnum.TRC_EXTERNALITEMSKU_UPDATE_EXCEPTION, "Failure" + remark);
        }
        ToGlyResultDO toGlyResultDO = JSONObject.parseObject(result, ToGlyResultDO.class);
        //存储请求记录
        if (toGlyResultDO.getStatus().equals(ZeroToNineEnum.ZERO.getCode())) {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_SUCCESS.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        } else {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
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
        //删除关联关系
        if (action.equals(TrcActionTypeEnum.SKURELATION_REMOVE.getCode())) {
            for (SkuRelation skuRelation : skuRelationList) {
                Example example = new Example(SkuRelation.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("skuCode", skuRelation.getSkuCode());
                criteria.andEqualTo("channelSkuCode", skuRelation.getChannelSkuCode());
                skuRelationService.deleteByExample(example);
            }
        }
        //一件代发商品批量关联
        if (action.equals(TrcActionTypeEnum.SKURELATION_EXTERNALSKU_ADD.getCode())) {
            Iterator<SkuRelation> iter = skuRelationList.iterator();
            List<SkuRelation> skuRelationList1 = new ArrayList<>();
            while (iter.hasNext()) {
                SkuRelation skuRelation = iter.next();
                ExternalItemSku externalItemSku = new ExternalItemSku();
                externalItemSku.setSkuCode(skuRelation.getSkuCode());
                externalItemSku = externalItemSkuService.selectOne(externalItemSku);
                skuRelation.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
                skuRelation.setSupplierCode(externalItemSku.getSupplierCode());
                skuRelationList1.add(skuRelation);
            }
            skuRelationService.insertList(skuRelationList1);
        }
        //自采商品批量关联
        if (action.equals(TrcActionTypeEnum.SKURELATION_SKU_ADD.getCode())) {
            Iterator<SkuRelation> iter = skuRelationList.iterator();
            while (iter.hasNext()) {
                SkuRelation skuRelation = iter.next();
                Skus skus = new Skus();
                skus.setSpuCode(skuRelation.getSpuCode());
                skus.setSkuCode(skuRelation.getSkuCode());
                skus = skusService.selectOne(skus);
                //TODO 自采商品表结构未完善，后续再写
            }
        }
    }


    //发送分类属性改动
    public ToGlyResultDO sendCategoryPropertyList(TrcActionTypeEnum action, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception {
        AssertUtil.notEmpty(categoryPropertyList, "分类属性列表不能为空");
        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(trcConfig.getKey()).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime);
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("categoryPropertyList", categoryPropertyList);
        logger.info("请求数据: " + params.toJSONString());
        String result = trcService.sendCategoryPropertyList(trcConfig.getCategoryPropertyUrl(), params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知分类属性变更接口sendCategoryPropertyList]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_CATEGORY_PROPERTY_EXCEPTION.getMessage());
            //存储请求记录
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_TIME_OUT.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
            throw new TrcException(ExceptionEnum.TRC_CATEGORY_PROPERTY_EXCEPTION, "Failure" + remark);
        }
        ToGlyResultDO toGlyResultDO = JSONObject.parseObject(result, ToGlyResultDO.class);
        //存储请求记录
        if (toGlyResultDO.getStatus().equals(ZeroToNineEnum.ZERO.getCode())) {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        } else {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_SUCCESS.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);

        }
        return toGlyResultDO;
    }

    //发送分类品牌改动
    public ToGlyResultDO sendCategoryBrandList(TrcActionTypeEnum action, List<CategoryBrand> categoryBrandList, long operateTime) throws Exception {
        AssertUtil.notEmpty(categoryBrandList, "分类品牌列表不能为空");
        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(trcConfig.getKey()).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime);
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("categoryBrandList", categoryBrandList);
        logger.info("请求数据: " + params.toJSONString());
        String result = trcService.sendCategoryBrandList(trcConfig.getCategoryBrandUrl(), params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知分类品牌变更接口sendCategoryBrandList]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_CATEGORY_BRAND_EXCEPTION.getMessage());
            //存储请求记录
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_TIME_OUT.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
            throw new TrcException(ExceptionEnum.TRC_CATEGORY_BRAND_EXCEPTION, "Failure" + remark);
        }
        ToGlyResultDO toGlyResultDO = JSONObject.parseObject(result, ToGlyResultDO.class);
        //存储请求记录
        if (toGlyResultDO.getStatus().equals(ZeroToNineEnum.ZERO.getCode())) {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        } else {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        }
        return toGlyResultDO;
    }

    //发送分类改动
    public ToGlyResultDO sendCategoryToTrc(TrcActionTypeEnum action, Category oldCategory, Category category, long operateTime) throws Exception {
        AssertUtil.notBlank(category.getIsValid(), "是否停用不能为空");
        AssertUtil.notBlank(category.getName(), "分类名称不能为空");
        AssertUtil.notBlank(category.getClassifyDescribe(), "分类描述不能为空");
        AssertUtil.notNull(category.getSort(), "分类排序不能为空");
        //判断是否通知
        if (!action.equals(TrcActionTypeEnum.ADD_CATEGORY.getCode())) {
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
        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(trcConfig.getKey()).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
                append(categoryToTrc.getClassifyDescribe()).append(OR).append(categoryToTrc.getIsValid()).append(OR).append(categoryToTrc.getName()).append(OR).
                append(categoryToTrc.getParentId()).append(OR).append(categoryToTrc.getSort());
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("categoryToTrc", categoryToTrc);
        logger.info("请求数据: " + params.toJSONString());
        String result = trcService.sendCategoryToTrc(trcConfig.getCategoryUrl(), params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知分类品牌变更接口sendCategoryToTrc]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_CATEGORY_EXCEPTION.getMessage());
            //存储请求记录
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_TIME_OUT.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
            throw new TrcException(ExceptionEnum.TRC_CATEGORY_EXCEPTION, "Failure" + remark);
        }
        ToGlyResultDO toGlyResultDO = JSONObject.parseObject(result, ToGlyResultDO.class);
        //存储请求记录
        if (toGlyResultDO.getStatus().equals(ZeroToNineEnum.ZERO.getCode())) {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_FAILED.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        } else {
            addRequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                    noticeNum, RequestFlowStatusEnum.SEND_SUCCESS.getCode(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        }
        return toGlyResultDO;
    }


    public static void main(String[] args) {
        try {
            String action = "delete";
            String noticeNum = GuidUtil.getNextUid(action + UNDER_LINE);
            BrandToTrcDO brandToTrc = new BrandToTrcDO();
            brandToTrc.setWebUrl("wqeqeqr");
            brandToTrc.setAlise("qwqwedqdeqd");
            brandToTrc.setName("wdad");
            brandToTrc.setBrandCode("vdfgdghd");
            long operateTime = System.currentTimeMillis();
            //model中字段以字典序排序

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("gyl-tairan").append(OR).append(action).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
                    append(brandToTrc.getAlise()).append(OR).append(brandToTrc.getBrandCode()).append(OR).append(brandToTrc.getIsValid()).append(OR).
                    append(brandToTrc.getLogo()).append(OR).append(brandToTrc.getName()).append(OR).append(brandToTrc.getWebUrl());
            //MD5加密
            System.out.println(stringBuilder.toString());
            String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
            JSONObject params = new JSONObject();
            params.put("action", action);
            params.put("operateTime", operateTime);
            params.put("noticeNum", noticeNum);
            params.put("sign", sign);
            params.put("brandToTrc", brandToTrc);
            System.out.println(params.toJSONString());
            String result = HttpClientUtil.httpPostJsonRequest("http://ddd.www.trc.com/api/supply/sync/brands", params.toJSONString(), 10000);
            System.out.println("********返回值********");
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
