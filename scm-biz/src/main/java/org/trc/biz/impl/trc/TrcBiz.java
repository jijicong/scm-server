package org.trc.biz.impl.trc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constant.RequestFlowConstant;
import org.trc.domain.category.*;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.goods.*;
import org.trc.enums.TrcActionTypeEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.TrcException;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.model.BrandToTrc;
import org.trc.model.CategoryToTrc;
import org.trc.model.PropertyToTrc;
import org.trc.model.ResultModel;
import org.trc.service.ITrcService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.goods.IExternalItemSkuService;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkuRelationService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

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
    private IItemsService itemsService;

    @Value("${trc.key}")
    private String TRC_KEY;

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

    private static final String OR = "|";

    private static final String UNDER_LINE = "_";

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ResultModel sendBrand(TrcActionTypeEnum action, Brand oldBrand, Brand brand, long operateTime) throws Exception {
        Assert.notNull(brand.getAlise(), "品牌别名不能为空");
        Assert.notNull(brand.getBrandCode(), "品牌编码不能为空");
        Assert.notNull(brand.getIsValid(), "是否停用不能为空");
        Assert.notNull(brand.getLogo(), "图片路径不能为空");
        Assert.notNull(brand.getName(), "品牌名称不能为空");
        Assert.notNull(brand.getWebUrl(), "品牌网址不能为空");
        //判断是否通知
        if (!action.equals(TrcActionTypeEnum.ADD_BRAND)) {
            if (oldBrand.getName().equals(brand.getName()) && oldBrand.getIsValid().equals(brand.getIsValid())) {
                return new ResultModel("1", "无需通知品牌变更");
            }
        }
        BrandToTrc brandToTrc = new BrandToTrc();
        brandToTrc.setAlise(brand.getAlise());
        brandToTrc.setBrandCode(brand.getBrandCode());
        brandToTrc.setIsValid(brand.getIsValid());
        brandToTrc.setLogo(brand.getLogo());
        brandToTrc.setName(brand.getName());
        brandToTrc.setWebUrl(brand.getWebUrl());
        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TRC_KEY).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
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
        String result = trcService.sendBrandNotice(BRAND_URL, params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知品牌变更接口sendBrand]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_BRAND_EXCEPTION.getMessage());
            throw new TrcException(ExceptionEnum.TRC_BRAND_EXCEPTION, "Failure:" + remark);
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                noticeNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ResultModel sendProperty(TrcActionTypeEnum action, Property oldProperty, Property property, List<PropertyValue> valueList, long operateTime) throws Exception {
        Assert.notNull(property.getSort(), "属性排序不能为空");
        Assert.notNull(property.getName(), "属性名称不能为空");
        Assert.notNull(property.getIsValid(), "属性是否停用不能为空");
        Assert.notNull(property.getDescription(), "属性描述不能为空");
        Assert.notNull(property.getTypeCode(), "属性类型编码不能为空");
        Assert.notNull(property.getValueType(), "属性值类型不能为空");
        //判断是否通知
        if (!action.equals(TrcActionTypeEnum.ADD_PROPERTY)) {
            if (oldProperty.getIsValid().equals(property.getIsValid()) && oldProperty.getName().equals(property.getName())
                    && oldProperty.getValueType().equals(property.getValueType()) && oldProperty.getTypeCode().equals(property.getTypeCode())
                    && valueList == null) {
                return new ResultModel("1", "无需通知属性变更");
            }
        }
        PropertyToTrc propertyToTrc = new PropertyToTrc();
        propertyToTrc.setSort(property.getSort());
        propertyToTrc.setName(property.getName());
        propertyToTrc.setIsValid(property.getIsValid());
        propertyToTrc.setDescription(property.getDescription());
        propertyToTrc.setTypeCode(property.getTypeCode());
        propertyToTrc.setValueType(property.getValueType());

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TRC_KEY).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
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
        String result = trcService.sendPropertyNotice(PROPERTY_URL, params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知属性变更接口sendProperty]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_PROPERTY_EXCEPTION.getMessage());
            throw new TrcException(ExceptionEnum.TRC_PROPERTY_EXCEPTION, "Failure" + remark);
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                noticeNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ResultModel sendCategory(TrcActionTypeEnum action, Category oldCategory, Category category, List<CategoryBrand> categoryBrandList, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception {
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
    public ResultModel sendItem(TrcActionTypeEnum action, Items items, ItemNaturePropery itemNaturePropery, ItemSalesPropery itemSalesPropery, Skus skus, Long operateTime) throws Exception {

        //TODO 判断石头通知，暂时觉得都得通知

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TRC_KEY).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime);
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
        String result = trcService.sendItemsNotice(ITEMS_URL, params.toJSONString());

        String remark = "调用方法-TrcBiz类中[通知商品变更接口sendItem]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_ITEMS_EXCEPTION.getMessage());
            throw new TrcException(ExceptionEnum.TRC_ITEMS_EXCEPTION, "Failure" + remark);
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                noticeNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        requestFlowService.insert(requestFlow);
        return resultModel;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ResultModel sendExternalItemSkuUpdation(TrcActionTypeEnum action, List<ExternalItemSku> oldExternalItemSkuList, List<ExternalItemSku> externalItemSkuList, Long operateTime) throws Exception {
        Assert.notNull(externalItemSkuList, "更新列表不能为空");

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TRC_KEY).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime);
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        List<ExternalItemSku> newOldExternalItemSkuList = new ArrayList<>();
        List<ExternalItemSku> newExternalItemSkuList = new ArrayList<>();
        List<String> skuCodes = skuRelationService.selectSkuCode(externalItemSkuList);
        for (int i = 0; i < externalItemSkuList.size(); i++) {
            Iterator<String> iter = skuCodes.iterator();
            while (iter.hasNext()) {
                if (externalItemSkuList.get(i).getSkuCode().equals(iter.next())) {
                    newExternalItemSkuList.add(externalItemSkuList.get(i));
                    newOldExternalItemSkuList.add((oldExternalItemSkuList.get(i)));
                    iter.remove();
                }
            }
        }
        //对两个新集合做比较判断是否推送
        List<ExternalItemSku> sendList = new ArrayList<>();
        for (int i = 0; i < newExternalItemSkuList.size(); i++) {
            ExternalItemSku externalItemSku = newExternalItemSkuList.get(i);
            ExternalItemSku oldExternalItemSku = newOldExternalItemSkuList.get(i);
            Boolean flag = externalItemSku.getSupplierPrice() == oldExternalItemSku.getSupplierPrice() && externalItemSku.getSupplyPrice() == oldExternalItemSku.getSupplyPrice();
            if (!flag) {
                sendList.add(externalItemSku);
            }
        }
        //发送数据
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("externalItemSkuList", sendList);
        String result = trcService.sendPropertyNotice(EXTERNALITEMSKU_UPDATE_INFROMATION_URL, params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知一件代发商品变更接口sendExternalItemSkuUpdation]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_EXTERNALITEMSKU_UPDATE_EXCEPTION.getMessage());
            throw new TrcException(ExceptionEnum.TRC_EXTERNALITEMSKU_UPDATE_EXCEPTION, "Failure" + remark);
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                noticeNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    @Override
    public Pagenation<ExternalItemSku> externalItemSkuPage(ExternalItemSkuForm queryModel, Pagenation<ExternalItemSku> page) throws Exception {

        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        if (queryModel.getSupplierCode() != null) {
            criteria.andEqualTo("supplierCode",queryModel.getSupplierCode());
        }
        if (!StringUtils.isEmpty(queryModel.getSkuCode())) {
            criteria.andEqualTo("skuCode",queryModel.getSkuCode());
        }
        if (!StringUtils.isEmpty(queryModel.getItemName())) {
            criteria.andEqualTo("itemName",queryModel.getItemName());
        }
        example.orderBy("supplierCode").desc();
        return externalItemSkuService.pagination(example,page,queryModel);
    }

    @Override
    public void updateRelation(String action, JSONArray relations) throws Exception {
        try{
            AssertUtil.notBlank(action, "动作参数不能为空");
            AssertUtil.notNull(relations, "关联列表不能为空");
            Boolean flag = action.equals(TrcActionTypeEnum.SKURELATION_REMOVE)&&action.equals(TrcActionTypeEnum.SKURELATION_EXTERNALSKU_ADD)&&action.equals(TrcActionTypeEnum.SKURELATION_SKU_ADD);
            AssertUtil.isTrue(flag,"动作参数类型错误");
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_PARAM_EXCEPTION,e.getMessage());
        }
        List<SkuRelation> skuRelationList = relations.toJavaList(SkuRelation.class);
        //删除关联关系
        if (action.equals(TrcActionTypeEnum.SKURELATION_REMOVE)) {
            for (SkuRelation skuRelation: skuRelationList){
                Example example = new Example(SkuRelation.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("skuCode",skuRelation.getSkuCode());
                criteria.andEqualTo("channelSkuCode",skuRelation.getChannelSkuCode());
                skuRelationService.deleteByExample(example);
            }
        }
        //一件代发商品批量关联
        if (action.equals(TrcActionTypeEnum.SKURELATION_EXTERNALSKU_ADD)){
            Iterator<SkuRelation> iter = skuRelationList.iterator();
            while (iter.hasNext()){
                SkuRelation skuRelation = iter.next();
                ExternalItemSku externalItemSku = new ExternalItemSku();
                externalItemSku.setSkuCode(skuRelation.getSkuCode());
                externalItemSku = externalItemSkuService.selectOne(externalItemSku);
                skuRelation.setSupplierSkuCode(externalItemSku.getSupplierSkuCode());
                skuRelation.setSupplierCode(externalItemSku.getSupplierCode());
                skuRelationService.insert(skuRelation);
            }
        }
        //自采商品批量关联
        if (action.equals(TrcActionTypeEnum.SKURELATION_SKU_ADD)){
            Iterator<SkuRelation> iter = skuRelationList.iterator();
            while(iter.hasNext()){
                SkuRelation skuRelation = iter.next();
                Items items = new Items();
                items.setSpuCode(skuRelation.getSpuCode());
                items = itemsService.selectOne(items);
                //TODO 自采商品表结构未完善，后续再写
            }
        }
    }


    //发送分类属性改动
    public ResultModel sendCategoryPropertyList(TrcActionTypeEnum action, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception {
        Assert.notNull(categoryPropertyList, "分类属性列表不能为空");
        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TRC_KEY).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime);
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("categoryPropertyList", categoryPropertyList);
        logger.info("请求数据: " + params.toJSONString());
        String result = trcService.sendCategoryPropertyList(CATEGORY_PROPERTY_URL, params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知分类属性变更接口sendCategoryPropertyList]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_CATEGORY_PROPERTY_EXCEPTION.getMessage());
            throw new TrcException(ExceptionEnum.TRC_CATEGORY_PROPERTY_EXCEPTION, "Failure" + remark);
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                noticeNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    //发送分类品牌改动
    public ResultModel sendCategoryBrandList(TrcActionTypeEnum action, List<CategoryBrand> categoryBrandList, long operateTime) throws Exception {
        Assert.notNull(categoryBrandList, "分类品牌列表不能为空");
        //传值处理
        String noticeNum = GuidUtil.getNextUid(action.getCode() + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TRC_KEY).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime);
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action.getCode());
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("categoryBrandList", categoryBrandList);
        logger.info("请求数据: " + params.toJSONString());
        String result = trcService.sendCategoryBrandList(CATEGORY_BRAND_URL, params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知分类品牌变更接口sendCategoryBrandList]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_CATEGORY_BRAND_EXCEPTION.getMessage());
            throw new TrcException(ExceptionEnum.TRC_CATEGORY_BRAND_EXCEPTION, "Failure" + remark);
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                noticeNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    //发送分类改动
    public ResultModel sendCategoryToTrc(TrcActionTypeEnum action, Category oldCategory, Category category, long operateTime) throws Exception {
        Assert.notNull(category.getIsValid(), "是否停用不能为空");
        Assert.notNull(category.getName(), "分类名称不能为空");
        Assert.notNull(category.getClassifyDescribe(), "分类描述不能为空");
        Assert.notNull(category.getSort(), "分类排序不能为空");
        //判断是否通知
        if (!action.equals(TrcActionTypeEnum.ADD_CATEGORY.getCode())) {
            if (oldCategory.getName().equals(category.getName()) && oldCategory.getIsValid().equals(category.getIsValid())) {
                return new ResultModel("1", "无需通知分类变更");
            }
        }
        CategoryToTrc categoryToTrc = new CategoryToTrc();
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
        stringBuilder.append(TRC_KEY).append(OR).append(action.getCode()).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
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
        String result = trcService.sendCategoryToTrc(CATEGORY_URL, params.toJSONString());
        String remark = "调用方法-TrcBiz类中[通知分类品牌变更接口sendCategoryToTrc]";
        //抛出通知自定义异常
        if (StringUtils.isEmpty(result)) {
            logger.error(ExceptionEnum.TRC_CATEGORY_EXCEPTION.getMessage());
            throw new TrcException(ExceptionEnum.TRC_CATEGORY_EXCEPTION, "Failure" + remark);
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, action.getCode(),
                noticeNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime(), remark);
        requestFlowService.insert(requestFlow);
        return resultModel;
    }


    public static void main(String[] args) {
        try {
            String action = "delete";
            String noticeNum = GuidUtil.getNextUid(action + UNDER_LINE);
            BrandToTrc brandToTrc = new BrandToTrc();
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
