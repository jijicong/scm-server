package org.trc.biz.impl.trc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import javafx.scene.control.Pagination;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.impl.goods.GoodsBiz;
import org.trc.biz.impl.supplier.SupplierBiz;
import org.trc.biz.impl.trc.model.Skus2;
import org.trc.biz.impl.trc.model.SkusProperty;
import org.trc.biz.requestFlow.IRequestFlowBiz;
import org.trc.biz.trc.ITrcBiz;
import org.trc.cache.Cacheable;
import org.trc.constant.RequestFlowConstant;
import org.trc.constants.SupplyConstants;
import org.trc.domain.BaseDO;
import org.trc.domain.System.Channel;
import org.trc.domain.category.*;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.forTrc.PropertyValueForTrc;
import org.trc.domain.goods.*;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierApply;
import org.trc.domain.supplier.SupplierApplyAudit;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.enums.*;
import org.trc.exception.ParamValidException;
import org.trc.exception.TrcException;
import org.trc.form.TrcConfig;
import org.trc.form.TrcParam;
import org.trc.form.category.BrandForm;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.ItemsForm;
import org.trc.form.goods.SkusForm;
import org.trc.form.supplier.SupplierForm;
import org.trc.form.trc.BrandForm2;
import org.trc.form.trc.CategoryForm2;
import org.trc.form.trcForm.PropertyFormForTrc;
import org.trc.form.trc.ItemsForm2;
import org.trc.model.BrandToTrcDO;
import org.trc.model.CategoryToTrcDO;
import org.trc.model.PropertyToTrcDO;
import org.trc.model.ToGlyResultDO;
import org.trc.service.ITrcService;
import org.trc.service.category.ICategoryService;
import org.trc.service.category.IPropertyService;
import org.trc.service.category.IPropertyValueService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.goods.*;
import org.trc.service.impl.category.BrandService;
import org.trc.service.impl.system.ChannelService;
import org.trc.service.supplier.ISupplierApplyService;
import org.trc.service.supplier.ISupplierBrandService;
import org.trc.service.supplier.ISupplierService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * 泰然城交互
 * Created by hzdzf on 2017/6/7.
 */
@Service("trcBiz")
public class TrcBiz implements ITrcBiz {

    private Logger logger = LoggerFactory.getLogger(TrcBiz.class);

    //中文逗号
    public final static String COMMA_ZH = "，";

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
    private IPropertyService propertyService;
    @Autowired
    private IPropertyValueService propertyValueService;
    @Autowired
    private ISupplierService supplierService;
    @Autowired
    private ISupplierBrandService supplierBrandService;
    @Autowired
    private ISupplierApplyService supplierApplyService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private IItemsService itemsService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ISkuStockService skuStockService;
    @Autowired
    private IItemSalesProperyService itemSalesProperyService;

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
    public ToGlyResultDO sendItem(TrcActionTypeEnum action, Items items, List<ItemNaturePropery> itemNaturePropery,
                                  List<ItemSalesPropery> itemSalesPropery, List<Skus> updateSkus, Long operateTime) throws Exception {
        AssertUtil.notNull(items, "自采商品修改通知渠道商品信息参数items不能为空");
        //AssertUtil.notEmpty(itemNaturePropery, "自采商品修改通知渠道商品自然属性参数itemNaturePropery不能为空");
        AssertUtil.notEmpty(itemSalesPropery, "自采商品修改通知渠道商品采购属性参数itemNaturePropery不能为空");
        List<SkuRelation> skuRelationList = new ArrayList<SkuRelation>();
        if(updateSkus.size() > 0){
            //设置sku库存
            setSkuStock(updateSkus);
        }
        List<Skus> noticeSkus = new ArrayList<Skus>();
        for(Skus skus2: updateSkus){
            SkuRelation skuRelation = new SkuRelation();
            skuRelation.setChannelCode(RequestFlowConstant.TRC);
            skuRelation.setSpuCode(skus2.getSpuCode());
            skuRelation.setSkuCode(skus2.getSkuCode());
            skuRelation = skuRelationService.selectOne(skuRelation);
            if(null != skuRelation){
                noticeSkus.add(skus2);
            }
        }
        if(noticeSkus.size() == 0){
            return new ToGlyResultDO(SuccessFailureEnum.SUCCESS.getCode(), "商品修改无需同步");
        }
        //MD5加密
        TrcParam trcParam = ParamsUtil.generateTrcSign(trcConfig.getKey(), action);
        JSONObject params = (JSONObject)JSONObject.toJSON(trcParam);
        params.put("items", items);
        params.put("itemNaturePropery", itemNaturePropery);
        params.put("itemSalesPropery", itemSalesPropery);
        params.put("skus", noticeSkus);
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
                        if(null == externalItemSku.getStock())
                            externalItemSku.setStock(0L);
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
        if (StringUtils.isNotBlank(queryModel.getSupplierCode())) {
            Supplier supplier = getSupplier(queryModel.getSupplierCode());
            if(null != supplier){
                if(StringUtils.equals(SupplierBiz.SUPPLIER_ONE_AGENT_SELLING, supplier.getSupplierKindCode())){//一件代发供应商
                    criteria.andEqualTo("supplierCode", supplier.getSupplierInterfaceId());
                }else{
                    criteria.andEqualTo("supplierCode", queryModel.getSupplierCode());
                }
            }else{
               return page;
            }
        }
        if (StringUtils.isNotBlank(queryModel.getSkuCode())) {//商品SKU编号
            criteria.andLike("skuCode", "%" + queryModel.getSkuCode() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getSupplierSkuCode())) {//供应商SKU编号
            criteria.andLike("supplierSkuCode", "%" + queryModel.getSupplierSkuCode() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getItemName())) {//商品名称
            criteria.andLike("itemName", "%" + queryModel.getItemName() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getWarehouse())) {//仓库名称
            criteria.andLike("warehouse", "%" + queryModel.getWarehouse() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getBrand())) {//品牌
            criteria.andLike("brand", "%" + queryModel.getBrand() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getBarCode())) {//条形码
            criteria.andLike("barCode", "%" + queryModel.getBarCode() + "%");
        }
        example.orderBy("supplierCode").desc();
        page = externalItemSkuService.pagination(example, page, queryModel);
        setMoneyWeight(page.getResult());
        return page;
    }

    /**
     * 设置金额和重量
     * @param externalItemSkuList
     */
    private void setMoneyWeight(List<ExternalItemSku> externalItemSkuList){
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            if(null != externalItemSku.getSupplierPrice())
                externalItemSku.setSupplierPrice(CommonUtil.getMoneyLong(externalItemSku.getSupplierPrice()));
            if(null != externalItemSku.getSupplyPrice())
                externalItemSku.setSupplyPrice(CommonUtil.getMoneyLong(externalItemSku.getSupplyPrice()));
            if(null != externalItemSku.getMarketReferencePrice())
                externalItemSku.setMarketReferencePrice(CommonUtil.getMoneyLong(externalItemSku.getMarketReferencePrice()));
            if(null != externalItemSku.getWeight())
                externalItemSku.setWeight(CommonUtil.getWeightLong(externalItemSku.getWeight()));
        }
    }

    /**
     * 根据供应商编码查询供应商
     * @param supplierCode
     * @return
     */
    private Supplier getSupplier(String supplierCode){
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(supplierCode);
        return supplierService.selectOne(supplier);
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
            //AssertUtil.notBlank(skuRelation.getChannelSkuCode(), String.format("参数%s中渠道方SKU编码channelSkuCode为空", jbo));
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
            for(SkuRelation skuRelation: skuRelationList){
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
            for(SkuRelation skuRelation: skuRelationList){
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
                    skuRelation.setIsValid(ValidEnum.VALID.getCode());
                    skuRelations2.add(skuRelation);
                }
            }
            if(skuRelations2.size() > 0){
                skuRelationService.insertList(skuRelations2);
            }
        }
    }

    @Override
    public Pagenation<Supplier> supplierPage(SupplierForm queryModel, Pagenation<Supplier> page) throws Exception {
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(queryModel.getSupplierName())) {//供应商名称
            criteria.andLike("supplierName", "%" + queryModel.getSupplierName() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getSupplierCode())) {//供应商编码
            criteria.andLike("supplierCode", "%" + queryModel.getSupplierCode() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getContact())) {//联系人
            criteria.andLike("contact", "%" + queryModel.getContact() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getSupplierKindCode())) {//供应商性质
            criteria.andEqualTo("supplierKindCode", queryModel.getSupplierKindCode());
        }
        if (StringUtils.isNotBlank(queryModel.getStartDate())) {//开始日期
            criteria.andGreaterThanOrEqualTo("updateTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if (StringUtils.isNotBlank(queryModel.getEndDate())) {//截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("updateTime", DateUtils.addDays(endDate, 1));
        }
        if (StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("isValid").desc();
        example.orderBy("updateTime").desc();
        page = supplierService.pagination(example, page, queryModel);
        handlerSupplierPage(page);
        //分页查询
        return page;
    }

    @Override
    public Pagenation<Skus2> skusPage(SkusForm form, Pagenation<Skus> page) {
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(form.getSpuCode())){
            criteria.andEqualTo("spuCode",form.getSpuCode());
        }
        if (StringUtils.isNotBlank(form.getSkuCode())){
            criteria.andEqualTo("skuCode",form.getSkuCode());
        }
        if (StringUtils.isNotBlank(form.getIsValid())){
            criteria.andEqualTo("isValid",form.getIsValid());
        }
        Set<String> spus = getSkusQueryConditonRelateSpus(form);
        if(null != spus){
            if(spus.size() > 0){
                criteria.andIn("spuCode", spus);
            }else{
                return new Pagenation<Skus2>();
            }
        }
        example.orderBy("spuCode").desc();
        page = skusService.pagination(example,page,form);
        //设置库存
        setSkuStock(page.getResult());
        Pagenation<Skus2> page2 = new Pagenation<Skus2>();
        BeanUtils.copyProperties(page, page2,"result");
        List<Skus2> skus2List = new ArrayList<>();
        for(Skus skus: page.getResult()){
            Skus2 skus2 = new Skus2();
            BeanUtils.copyProperties(skus, skus2);
            skus2List.add(skus2);
        }
        //设置SPU商品信息
        setSpuInfo(skus2List);
        //设置SKU属性信息
        setSkuPropertyInfo(skus2List);
        page2.setResult(skus2List);
        return page2;

    }

    /**
     *设置品牌名称
     * @param items
     */
    private void setBrandName(List<Items> items){
        List<Long> brandIds = new ArrayList<Long>();
        for(Items items2: items){
            brandIds.add(items2.getBrandId());
        }
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", brandIds);
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        List<Brand> brands = brandService.selectByExample(example);
        AssertUtil.notEmpty(brands,String.format("查询商品品牌ID为[%s]的品牌信息为空", CommonUtil.converCollectionToString(brandIds)));
        for(Items items2 : items){
            for(Brand c : brands){
                if(items2.getBrandId().longValue() == c.getId().longValue()){
                    items2.setBrandName(c.getName());
                    break;
                }
            }
        }
    }

    /**
     *设置分类名称
     * @param items
     */
    private void setCategoryName(List<Items> items){
        List<Long> categoryIds = new ArrayList<Long>();
        for(Items items2: items){
            categoryIds.add(items2.getCategoryId());
        }
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", categoryIds);
        List<Category> thridCategories = categoryService.selectByExample(example);
        AssertUtil.notEmpty(thridCategories,String.format("查询商品所属分类ID为[%s]的分类信息为空", CommonUtil.converCollectionToString(categoryIds)));
        /**
         * 将分类的全路径ID(full_path_id)取出来，然后从中取到从第一级到第三季的所有分类ID
         * 放到分类ID列表categoryIds中
         */
        for(Category c : thridCategories){
            String[] tmps = c.getFullPathId().split("\\"+GoodsBiz.CATEGORY_ID_SPLIT_SYMBOL);
            for(String s : tmps){
                categoryIds.add(Long.parseLong(s));
            }
        }
        List<Category> categories = categoryService.selectByExample(example);
        //获取三级分类对应的全路径名称
        Map<Long, String> map = getThirdCategoryFullPathName(thridCategories, categories);
        for(Items items2 : items){
            items2.setCategoryName(map.get(items2.getCategoryId()));
        }
    }

    /**
     * 获取第三级分类全路径名称
     * @param thirdCategories 第三级分类列表
     * @param categories 当前相关所有分类列表
     * @return
     */
    private Map<Long, String> getThirdCategoryFullPathName(List<Category> thirdCategories, List<Category> categories){
        Map<Long, String> map = new HashMap<Long, String>();
        for(Category c : thirdCategories){
            String[] tmps = c.getFullPathId().split("\\"+GoodsBiz.CATEGORY_ID_SPLIT_SYMBOL);
            StringBuilder sb = new StringBuilder();
            //第一级分类名称
            for(Category c2 : categories){
                if(Long.parseLong(tmps[0]) == c2.getId()){
                    sb.append(c2.getName());
                    break;
                }
            }
            //第二级分类名称
            for(Category c2 : categories){
                if(Long.parseLong(tmps[1]) == c2.getId()){
                    sb.append(GoodsBiz.CATEGORY_NAME_SPLIT_SYMBOL).append(c2.getName());
                    break;
                }
            }
            //第三级分类名称
            for(Category c2 : categories){
                if(c.getId() == c2.getId()){
                    sb.append(GoodsBiz.CATEGORY_NAME_SPLIT_SYMBOL).append(c2.getName());
                    break;
                }
            }
            map.put(c.getId(), sb.toString());
        }
        return map;
    }

    /**
     * 获取SKU查询条件相关的SPU
     * @param queryModel
     * @return
     */
    private Set<String> getSkusQueryConditonRelateSpus(SkusForm queryModel){
        if(StringUtil.isNotEmpty(queryModel.getItemName()) || null != queryModel.getCategoryId() ||
                null != queryModel.getBrandId() || StringUtil.isNotEmpty(queryModel.getTradeType()) ){
            Example example = new Example(Items.class);
            Example.Criteria criteria = example.createCriteria();
            if(StringUtils.isNotBlank(queryModel.getItemName())){
                criteria.andLike("name", "%" + queryModel.getItemName() + "%");
            }
            if(null != queryModel.getCategoryId()){
                criteria.andEqualTo("categoryId", queryModel.getCategoryId());
            }
            if(null != queryModel.getBrandId()){
                criteria.andEqualTo("brandId", queryModel.getBrandId());
            }
            if (StringUtil.isNotEmpty(queryModel.getTradeType())) {//贸易类型
                criteria.andEqualTo("tradeType", queryModel.getTradeType());
            }
            List<Items> items = itemsService.selectByExample(example);
            Set<String> spus = new HashSet<String>();
            for(Items item: items){
                spus.add(item.getSpuCode());
            }
            return spus;
        }else{
            return null;
        }
    }

    /**
     * 设置SPU信息
     * @param skusList
     */
    private void setSpuInfo(List<Skus2> skusList){
        StringBuilder sb = new StringBuilder();
        for(Skus2 skus: skusList){
            sb.append("\"").append(skus.getSpuCode()).append("\"").append(SupplyConstants.Symbol.COMMA);
        }
        if(sb.length() > 0){
            Example example = new Example(Items.class);
            Example.Criteria criteria = example.createCriteria();
            String ids = sb.substring(0, sb.length()-1);
            String condition = String.format("spu_code in (%s)", ids);
            criteria.andCondition(condition);
            List<Items> itemsList = itemsService.selectByExample(example);
            if(itemsList.size() > 0){
                //设置品牌名称
                setBrandName(itemsList);
                //设置分类名称
                setCategoryName(itemsList);
                for(Skus2 skus: skusList){
                    for(Items items: itemsList){
                        if(StringUtils.equals(skus.getSpuCode(), items.getSpuCode())){
                            skus.setName(items.getName());
                            skus.setBrandId(items.getBrandId());
                            skus.setBrandName(items.getBrandName());
                            skus.setCategoryId(items.getCategoryId());
                            skus.setCategoryName(items.getCategoryName());
                            skus.setItemNo(items.getItemNo());
                            skus.setProducer(items.getProducer());
                            skus.setTradeType(items.getTradeType());
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置自采商品SKU库存
     * @param skusList
     */
    private void setSkuStock(List<Skus> skusList){
        StringBuilder sb = new StringBuilder();
        for(Skus skus: skusList){
            sb.append("\"").append(skus.getSkuCode()).append("\"").append(SupplyConstants.Symbol.COMMA);
        }
        if(sb.length() > 0){
            Example example = new Example(SkuStock.class);
            Example.Criteria criteria = example.createCriteria();
            String ids = sb.substring(0, sb.length()-1);
            String condition = String.format("sku_code in (%s)", ids);
            criteria.andCondition(condition);
            List<SkuStock> skuStockList = skuStockService.selectByExample(example);
            for(Skus skus: skusList){
                for(SkuStock skuStock: skuStockList){
                    if(StringUtils.equals(skus.getSkuCode(), skuStock.getSkuCode())){
                        skus.setStock(skuStock.getAvailableInventory());
                    }
                }
            }
        }
    }

    /**
     * 设置SKU属性信息
     * @param skusList
     */
    private void setSkuPropertyInfo(List<Skus2> skusList){
        StringBuilder sb = new StringBuilder();
        for(Skus2 skus: skusList){
            sb.append("\"").append(skus.getSpuCode()).append("\"").append(SupplyConstants.Symbol.COMMA);
        }
        if(sb.length() > 0){
            Example example = new Example(ItemSalesPropery.class);
            Example.Criteria criteria = example.createCriteria();
            String ids = sb.substring(0, sb.length()-1);
            String condition = String.format("spu_code in (%s)", ids);
            criteria.andCondition(condition);
            List<ItemSalesPropery> itemSalesProperyList = itemSalesProperyService.selectByExample(example);
            if(itemSalesProperyList.size() > 0){
                List<Property> propertyList = getSpuPropertys(itemSalesProperyList);
                for(Skus2 skus: skusList){
                    List<SkusProperty> skusPropertyList = new ArrayList<>();
                    for(ItemSalesPropery itemSalesPropery: itemSalesProperyList){
                        if(StringUtils.equals(skus.getSpuCode(), itemSalesPropery.getSpuCode()) &&
                                StringUtils.equals(skus.getSkuCode(), itemSalesPropery.getSkuCode())){
                            SkusProperty skusProperty = new SkusProperty();
                            skusProperty.setPropertyId(itemSalesPropery.getPropertyId());
                            for(Property property: propertyList){
                                if(itemSalesPropery.getPropertyId().longValue() == property.getId().longValue()){
                                    skusProperty.setPropertyName(property.getName());
                                    break;
                                }
                            }
                            skusProperty.setPropertyValueId(itemSalesPropery.getPropertyValueId());
                            skusProperty.setPropertyValue(itemSalesPropery.getPropertyActualValue());
                            skusPropertyList.add(skusProperty);
                        }
                    }
                    skus.setPropertys(skusPropertyList);
                }
            }

        }
    }

    /**
     * 获取SPU相关所有属性
     * @param itemSalesProperyList
     * @return
     */
    private List<Property> getSpuPropertys(List<ItemSalesPropery> itemSalesProperyList){
        StringBuilder sb = new StringBuilder();
        for(ItemSalesPropery itemSalesPropery: itemSalesProperyList){
            if(sb.indexOf(itemSalesPropery.getPropertyId().toString()) < 0){
                sb.append(itemSalesPropery.getPropertyId()).append(SupplyConstants.Symbol.COMMA);
            }
        }
        if(sb.length() > 0){
            Example example = new Example(Property.class);
            Example.Criteria criteria = example.createCriteria();
            String ids = sb.substring(0, sb.length()-1);
            String condition = String.format("id in (%s)", ids);
            criteria.andCondition(condition);
            return propertyService.selectByExample(example);
        }
        return null;
    }

    @Override
    public Pagenation<Items> itemsPage(ItemsForm2 queryModel, Pagenation<Items> page){
        Example example = new Example(Items.class);
        Example.Criteria criteria = example.createCriteria();
        if (org.apache.commons.lang.StringUtils.isNotBlank(queryModel.getSpuCode())){
            criteria.andLike("spuCode","%" + queryModel.getSpuCode() + "%");
        }
        if (StringUtil.isNotEmpty(queryModel.getName())) {//商品名称
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (null != queryModel.getCategoryId()) {//商品所属分类ID
            criteria.andEqualTo("categoryId", queryModel.getCategoryId());
        }
        if (null != queryModel.getBrandId()) {//商品所属品牌ID
            criteria.andEqualTo("brandId", queryModel.getBrandId());
        }
        if (StringUtil.isNotEmpty(queryModel.getTradeType())) {//贸易类型
            criteria.andEqualTo("tradeType", queryModel.getTradeType());
        }
        if (StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("updateTime").desc();
        page = itemsService.pagination(example, page, queryModel);
        setItemsSkus(page.getResult());
        return page;
    }

    /**
     * 设置商品相关SKU信息
     * @param itemsList
     */
    private void setItemsSkus(List<Items> itemsList){
        StringBuilder sb = new StringBuilder();
        for(Items items: itemsList){
            sb.append("\"").append(items.getSpuCode()).append("\"").append(SupplyConstants.Symbol.COMMA);
        }
        if(sb.length() > 0){
            Example example = new Example(Skus.class);
            Example.Criteria criteria = example.createCriteria();
            String ids = sb.substring(0, sb.length()-1);
            String condition = String.format("spu_code in (%s)", ids);
            criteria.andCondition(condition);
            List<Skus> skusList = skusService.selectByExample(example);
            if(skusList.size() > 0){
                setSkuStock(skusList);
            }
            for(Items items: itemsList){
                List<Skus> records = new ArrayList<Skus>();
                for(Skus skus: skusList){
                    if(StringUtils.equals(items.getSpuCode(), skus.getSpuCode())){
                        records.add(skus);
                    }
                }
                items.setRecords(records);
            }
        }
    }



    /**
     * 处理供应商分页结果
     *
     * @param page
     */
    private void handlerSupplierPage(Pagenation<Supplier> page) {
        List<String> supplierCodes = new ArrayList<String>();
        for (Supplier s : page.getResult()) {
            supplierCodes.add(s.getSupplierCode());
        }
        if (supplierCodes.size() > 0) {
            //查询供应商品牌
            Example example = new Example(SupplierBrand.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
            criteria.andIn("supplierCode", supplierCodes);
            List<SupplierBrand> supplierBrands = supplierBrandService.selectByExample(example);
            if(CollectionUtils.isEmpty(supplierBrands)){
                logger.error(String.format("根据供应商编码[%s]查询供应商品牌为空",
                        CommonUtil.converCollectionToString(supplierCodes)));
            }
            //查询供应商渠道
            Example example2 = new Example(SupplierApplyAudit.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
            criteria2.andIn("supplierCode", supplierCodes);
            criteria2.andEqualTo("status", AuditStatusEnum.PASS.getCode());//审核通过
            List<SupplierApply> supplierChannels = supplierApplyService.selectByExample(example2);
            for (Supplier s : page.getResult()) {
                if (supplierChannels.size() > 0) {
                    //设置渠道名称
                    setChannelName(s, supplierChannels);
                }
                //设置品牌名称
                setBrandName(s, supplierBrands);
            }
        }
    }
    /**
     * 设置渠道名称
     *
     * @param supplier
     * @param supplierChannels
     */
    private void setChannelName(Supplier supplier, List<SupplierApply> supplierChannels) {
        String _channels = "";
        for (SupplierApply supplierApplyAudit : supplierChannels) {
            if (StringUtils.equals(supplier.getSupplierCode(), supplierApplyAudit.getSupplierCode())) {
                Channel channel = new Channel();
                channel.setCode(supplierApplyAudit.getChannelCode());
                channel.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                channel = channelService.selectOne(channel);
                AssertUtil.notNull(channel, String.format("根据渠道编码[%s]查询渠道信息为空", supplierApplyAudit.getChannelCode()));
                _channels = _channels + channel.getName() + ",";
            }
        }
        if (_channels.length() > 0) {
            _channels = _channels.substring(0, _channels.length() - 1);
            supplier.setChannelName(_channels);
        }
    }

    /**
     * 设置代理品牌名称
     *
     * @param supplier
     * @param supplierBrands
     */
    private void setBrandName(Supplier supplier, List<SupplierBrand> supplierBrands) {
        String _brands = "";
        for (SupplierBrand supplierBrand : supplierBrands) {
            if (StringUtils.equals(supplier.getSupplierCode(), supplierBrand.getSupplierCode())) {
                Brand brand = new Brand();
                brand.setBrandCode(supplierBrand.getBrandCode());
                brand.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
                brand = brandService.selectOne(brand);
                AssertUtil.notNull(brand, String.format("根据品牌编码[%s]查询品牌信息为空", supplierBrand.getSupplierCode()));
                _brands = _brands + brand.getName() + ",";
            }
        }
        if (_brands.length() > 0) {
            _brands = _brands.substring(0, _brands.length() - 1);
            supplier.setBrandName(_brands);
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

    @Override
    public Object propertyPage(PropertyFormForTrc queryModel, Pagenation<Property> page) throws Exception {
        boolean bool = false;
        String flag = queryModel.getFlag();
        //为字符串‘1’or '0'
        Boolean flagParam = StringUtils.equals(flag,ZeroToNineEnum.ZERO.getCode()) || StringUtils.equals(flag,ZeroToNineEnum.ONE.getCode());
        if(!flagParam){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "flag不符合规定!");
        }
        Example example = new Example(Property.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isBlank(queryModel.getPropertyId())){
            AssertUtil.isTrue(queryModel.getPropertyId().indexOf(COMMA_ZH) == -1, "分隔多个属性ID必须是英文逗号");
            bool = true;
            verifyPropertyId(criteria,queryModel.getPropertyId());
        }
        if (!StringUtils.isBlank(queryModel.getName())) {
            bool = true;
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getTypeCode())) {
            bool = true;
            criteria.andEqualTo("typeCode", queryModel.getTypeCode());
        }
        if (!StringUtils.isBlank(queryModel.getSort())) {
            bool = true;
            criteria.andEqualTo("sort", queryModel.getSort());
        }
        example.orderBy("sort").asc();
        example.orderBy("updateTime").desc();
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), queryModel.getFlag())){//查询分类信息
            if (!StringUtils.isBlank(queryModel.getIsValid())) {
                bool = true;
                if(StringUtils.equals(flag,ZeroToNineEnum.ZERO.getCode())){
                    criteria.andEqualTo("isValid", queryModel.getIsValid());
                }
            }
            page = propertyService.pagination(example, page, queryModel);
            List<Property> list = page.getResult();
            for (Property property : list){ //创建人暂时不返回给trc  ：为属性赋值属性值
                endowPropertyValue(property, queryModel.getIsValid());
            }
            page.setResult(list);
            return page;
        }else {//查询分类子类信息
            /*
            步骤
            1.先查询所有的属性的id
            3.根据属性id ，属性值id，属性值的value 作为条件，分页查询
             */
            List<Long> propertyIdList = new ArrayList<Long>();
            if(bool){
                List<Property> propertyList = propertyService.selectByExample(example);
                if(CollectionUtils.isEmpty(propertyList)){
                    return page;
                }
                for (Property property : propertyList){
                    propertyIdList.add(property.getId());
                }
            }
            Example example1 = new  Example(PropertyValue.class);
            Example.Criteria criteria1 = example1.createCriteria();
            if(!CollectionUtils.isEmpty(propertyIdList))
                criteria1.andIn("propertyId",propertyIdList);
            if(!StringUtils.isBlank(queryModel.getPropertyValueId())){
                AssertUtil.isTrue(queryModel.getPropertyValueId().indexOf(COMMA_ZH) == -1, "分隔多个属性值ID必须是英文逗号");
                String[] ids = queryModel.getPropertyValueId().split(SupplyConstants.Symbol.COMMA);
                criteria1.andIn("id", Arrays.asList(ids));
            }
            if(!StringUtils.isBlank(queryModel.getPropertyValue())){
                criteria1.andLike("value","%"+queryModel.getPropertyValue()+"%");
            }
            if (!StringUtils.isBlank(queryModel.getIsValid())) {
                criteria1.andEqualTo("isValid", queryModel.getIsValid());
            }
            Pagenation<PropertyValue> page2 = new Pagenation<PropertyValue>();
            BeanUtils.copyProperties(page, page2);
            page2 = propertyValueService.pagination(example1, page2, new QueryModel());
            List<PropertyValueForTrc> propertyValueForTrcs = new ArrayList<PropertyValueForTrc>();
            for (PropertyValue pv : page2.getResult()) {
                PropertyValueForTrc propertyValueForTrc = new PropertyValueForTrc();
                propertyValueForTrc.setPropertyValueId(pv.getId());
                propertyValueForTrc.setIsDeleted(pv.getIsDeleted());
                propertyValueForTrc.setIsValid(pv.getIsValid());
                propertyValueForTrc.setPicture(pv.getPicture());
                propertyValueForTrc.setPropertyId(pv.getPropertyId());
                propertyValueForTrc.setSort(pv.getSort());
                propertyValueForTrc.setValue(pv.getValue());
                propertyValueForTrcs.add(propertyValueForTrc);
            }
            Pagenation<PropertyValueForTrc> page3 = new Pagenation<PropertyValueForTrc>();
            BeanUtils.copyProperties(page2, page3);
            page3.setResult(propertyValueForTrcs);
            return page3;
        }

    }

    @Override
    public Pagenation<Brand> brandList(BrandForm2 queryModel, Pagenation<Brand> page) throws Exception {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        setQueryParam(example,criteria,queryModel);
        Pagenation<Brand> pagenation = brandService.pagination(example, page, queryModel);
        return pagenation;
    }

    @Override
    public Pagenation<Category> categoryPage(CategoryForm2 queryModel, Pagenation<Category> page) throws Exception {
        //为字符串‘1’or '0'
        Boolean flagParam = StringUtils.equals(queryModel.getFlag(),ZeroToNineEnum.ZERO.getCode()) || StringUtils.equals(queryModel.getFlag(),ZeroToNineEnum.ONE.getCode());
        if(!flagParam){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "flag不符合规定!");
        }
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isBlank(queryModel.getCategoryId())) {
            AssertUtil.isTrue(queryModel.getCategoryId().indexOf(COMMA_ZH) == -1, "分隔多个分类ID必须是英文逗号");
            String[] ids = queryModel.getCategoryId().split(SupplyConstants.Symbol.COMMA);
            criteria.andIn("id", Arrays.asList(ids));
        }
        if (!StringUtils.isBlank(queryModel.getCategoryCode())) {
            AssertUtil.isTrue(queryModel.getCategoryCode().indexOf(COMMA_ZH) == -1, "分隔多个分类编码必须是英文逗号");
            String[] ids = queryModel.getCategoryCode().split(SupplyConstants.Symbol.COMMA);
            criteria.andIn("categoryCode", Arrays.asList(ids));
        }
        if (!StringUtils.isBlank(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getLevel())) {
            criteria.andEqualTo("level", queryModel.getLevel());
        }
        example.orderBy("isValid").desc();
        example.orderBy("updateTime").desc();
        if(StringUtils.equals(ZeroToNineEnum.ZERO.getCode(), queryModel.getFlag())){//查询分类信息
            if (!StringUtils.isBlank(queryModel.getIsValid())) {
                criteria.andEqualTo("isValid", queryModel.getIsValid());
            }
            return categoryService.pagination(example, page, queryModel);
        }else {//查询分类子类信息
            List<Category> categoryList = categoryService.selectByExample(example); //查询当前的类
            StringBuilder sb = new StringBuilder();
            for(Category category: categoryList){
                sb.append(category.getId()).append(SupplyConstants.Symbol.COMMA);
            }
            if (sb.length() > 0){
                example.clear();
                criteria = example.createCriteria();
                String ids = sb.substring(0, sb.length()-1);
                String condition = String.format("parent_id in (%s)", ids);
                if (!StringUtils.isBlank(queryModel.getIsValid())) {
                    condition = String.format("parent_id in (%s) and is_valid = \"%s\"", ids, queryModel.getIsValid());
                }
                criteria.andCondition(condition);
                return categoryService.pagination(example, page, new CategoryForm2());
            }else {
                return page;
            }
        }
    }

    public void setQueryParam(Example example,Example.Criteria criteria,BrandForm2 queryModel){
        if (!StringUtils.isBlank(queryModel.getName())) {
            criteria.andLike("name", "%" + queryModel.getName() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        if (!StringUtils.isBlank(queryModel.getStartUpdateTime())) {
            criteria.andGreaterThan("updateTime", queryModel.getStartUpdateTime());
        }
        if (!StringUtils.isBlank(queryModel.getEndUpdateTime())) {
            criteria.andLessThan("updateTime", DateUtils.formatDateTime(DateUtils.addDays(queryModel.getEndUpdateTime(),DateUtils.NORMAL_DATE_FORMAT,1)));
        }
        if (!StringUtils.isBlank(queryModel.getAlise())) {
            criteria.andLike("alise", "%" + queryModel.getAlise() + "%");
        }
        if (!StringUtils.isBlank(queryModel.getBrandCode())) {
            AssertUtil.isTrue(queryModel.getBrandCode().indexOf(COMMA_ZH) == -1, "分隔多个品牌编码必须是英文逗号");
            String[] ids = queryModel.getBrandCode().split(SupplyConstants.Symbol.COMMA);
            criteria.andIn("brandCode", Arrays.asList(ids));
        }
        if (!StringUtils.isBlank(queryModel.getBrandId())) {
            String[] ids = queryModel.getBrandId().split(SupplyConstants.Symbol.COMMA);
            criteria.andIn("id", Arrays.asList(ids));
        }
        example.orderBy("updateTime").desc();
    }

    //为属性赋予属性值
    private void endowPropertyValue(Property property, String isValid){

        List<PropertyValueForTrc> propertyValueForTrcs = new ArrayList<PropertyValueForTrc>();
        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setPropertyId(property.getId());
        if(StringUtils.isNotBlank(isValid)){
            propertyValue.setIsValid(isValid);
        }
        List<PropertyValue> propertyValueList = propertyValueService.select(propertyValue);
        for (PropertyValue pv : propertyValueList) {
            PropertyValueForTrc propertyValueForTrc = new PropertyValueForTrc();
            propertyValueForTrc.setPropertyValueId(pv.getId());
            propertyValueForTrc.setIsDeleted(pv.getIsDeleted());
            propertyValueForTrc.setIsValid(pv.getIsValid());
            propertyValueForTrc.setPicture(pv.getPicture());
            propertyValueForTrc.setPropertyId(pv.getPropertyId());
            propertyValueForTrc.setSort(pv.getSort());
            propertyValueForTrc.setValue(pv.getValue());
            propertyValueForTrcs.add(propertyValueForTrc);
        }
        property.setPropertyValueList(propertyValueForTrcs);

    }


    //校验属性id
    private void verifyPropertyId(Example.Criteria criteria,String propertyIds) throws ParamValidException{
        String propertyIdStr = propertyIds;
        String[] propertyIdStrs = StringUtils.split(propertyIdStr,",");
        if(propertyIdStrs.length > 0){
            //校验传入的值是否能转化成Long类型
            try {
                for (String propertyId : propertyIdStrs) {
                    Long.parseLong(propertyId);
                }
            }catch (NumberFormatException e){ //捕获到这个异常，说明调用方传入数据不合理
                logger.error("属性id:参数格式错误!",e);
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "属性id:参数格式错误!");
            }
            criteria.andIn("id",Arrays.asList(propertyIdStrs));
        }
    }




}
