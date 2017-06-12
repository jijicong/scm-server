package org.trc.biz.impl.trc;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constant.RequestFlowConstant;
import org.trc.domain.category.*;
import org.trc.domain.config.RequestFlow;
import org.trc.enums.CategoryActionTypeEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.exception.TrcException;
import org.trc.model.BrandToTrc;
import org.trc.model.CategoryToTrc;
import org.trc.model.PropertyToTrc;
import org.trc.model.ResultModel;
import org.trc.service.ITrcService;
import org.trc.service.config.IRequestFlowService;
import org.trc.util.GuidUtil;
import org.trc.util.HttpClientUtil;
import org.trc.util.MD5;

import java.util.Calendar;
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

    @Value("$(trc.key)")
    private String TRC_KEY;

    @Value("$(trc.brand.url)")
    private String BRAND_URL;

    @Value("$(trc.property.url)")
    private String PROPERTY_URL;

    @Value("$(trc.category.url)")
    private String CATEGORY_URL;

    @Value("$(trc.category.brand.url)")
    private String CATEGORY_BRAND_URL;

    @Value("$(trc.category.property.url)")
    private String CATEGORY_PROPERTY_URL;


    private static final String OR = "|";

    private static final String UNDER_LINE = "_";

    @Override
    public ResultModel sendBrand(String action, Brand oldBrand, Brand brand, long operateTime) throws Exception {
        Assert.notNull(brand.getAlise(), "品牌别名不能为空");
        Assert.notNull(brand.getBrandCode(), "品牌编码不能为空");
        Assert.notNull(brand.getIsValid(), "是否停用不能为空");
        Assert.notNull(brand.getLogo(), "图片路径不能为空");
        Assert.notNull(brand.getName(), "品牌名称不能为空");
        Assert.notNull(brand.getWebUrl(), "品牌网址不能为空");
        //判断是否通知
        if (oldBrand.getName().equals(brand.getName()) && oldBrand.getIsValid().equals(brand.getIsValid())) {
            return new ResultModel("1", "无需通知品牌变更");
        }
        BrandToTrc brandToTrc = new BrandToTrc();
        brandToTrc.setAlise(brand.getAlise());
        brandToTrc.setBrandCode(brand.getBrandCode());
        brandToTrc.setIsValid(brand.getIsValid());
        brandToTrc.setLogo(brand.getLogo());
        brandToTrc.setName(brand.getName());
        brandToTrc.setWebUrl(brand.getWebUrl());
        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TRC_KEY).append(OR).append(action).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
                append(brandToTrc.getAlise()).append(OR).append(brandToTrc.getBrandCode()).append(OR).append(brandToTrc.getIsValid()).append(OR).
                append(brandToTrc.getLogo()).append(OR).append(brandToTrc.getName()).append(OR).append(brandToTrc.getWebUrl());

        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("brandToTrc", brandToTrc);
        logger.info(params.toJSONString());
        String result = null;
        //抛出通知自定义异常
        try {
            result = trcService.sendBrandNotice(BRAND_URL, params.toJSONString());
        } catch (Exception e) {
            logger.error(ExceptionEnum.TRC_BRAND_EXCEPTION.getMessage() + " :" + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_BRAND_EXCEPTION, e.getMessage());
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.POST + UNDER_LINE);
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, RequestFlowConstant.POST,
                requestNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime());
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    @Override
    public ResultModel sendProperty(String action, Property oldProperty, Property property, List<PropertyValue> valueList, long operateTime) throws Exception {
        Assert.notNull(property.getSort(), "属性排序不能为空");
        Assert.notNull(property.getName(), "属性名称不能为空");
        Assert.notNull(property.getIsValid(), "属性是否停用不能为空");
        Assert.notNull(property.getDescription(), "属性描述不能为空");
        Assert.notNull(property.getTypeCode(), "属性类型编码不能为空");
        Assert.notNull(property.getValueType(), "属性值类型不能为空");
        //判断是否通知
        if (oldProperty.getIsValid().equals(property.getIsValid()) && oldProperty.getName().equals(property.getName())
                && oldProperty.getValueType().equals(property.getValueType()) && oldProperty.getTypeCode().equals(property.getTypeCode())
                && valueList == null) {
            return new ResultModel("1", "无需通知属性变更");
        }
        PropertyToTrc propertyToTrc = new PropertyToTrc();
        propertyToTrc.setSort(property.getSort());
        propertyToTrc.setName(property.getName());
        propertyToTrc.setIsValid(property.getIsValid());
        propertyToTrc.setDescription(property.getDescription());
        propertyToTrc.setTypeCode(property.getTypeCode());
        propertyToTrc.setValueType(property.getValueType());

        //传值处理
        String noticeNum = GuidUtil.getNextUid(action + UNDER_LINE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(TRC_KEY).append(OR).append(action).append(OR).append(noticeNum).append(OR).append(operateTime).append(OR).
                append(propertyToTrc.getDescription()).append(OR).append(propertyToTrc.getIsValid()).append(OR).
                append(propertyToTrc.getName()).append(OR).append(propertyToTrc.getSort()).append(OR).append(propertyToTrc.getTypeCode()).
                append(OR).append(propertyToTrc.getValueType());
        //MD5加密
        String sign = MD5.encryption(stringBuilder.toString()).toLowerCase();
        JSONObject params = new JSONObject();
        params.put("action", action);
        params.put("operateTime", operateTime);
        params.put("noticeNum", noticeNum);
        params.put("sign", sign);
        params.put("propertyToTrc", propertyToTrc);
        params.put("valueList", valueList);
        logger.info(params.toJSONString());
        String result = null;
        //抛出通知自定义异常
        try {
            result = trcService.sendPropertyNotice(PROPERTY_URL, params.toJSONString());
        } catch (Exception e) {
            logger.error(ExceptionEnum.TRC_PROPERTY_EXCEPTION.getMessage() + " :" + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_PROPERTY_EXCEPTION, e.getMessage());
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.POST + UNDER_LINE);
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, RequestFlowConstant.POST,
                requestNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime());
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    @Override
    public ResultModel sendCategory(CategoryActionTypeEnum action, Category oldCategory, Category category, List<CategoryBrand> categoryBrandList, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception {
        if (action.getCode().equals(CategoryActionTypeEnum.ADD_CATEGORY.getCode()) || action.getCode().equals(CategoryActionTypeEnum.EDIT_CATEGORY.getCode())
                || action.getCode().equals(CategoryActionTypeEnum.STOP_CATEGORY.getCode())) {
            return sendCategoryToTrc(action, oldCategory, category, operateTime);
        }
        if (action.getCode().equals(CategoryActionTypeEnum.EDIT_CATEGORY_BRAND.getCode())) {
            return sendCategoryBrandList(action, categoryBrandList, operateTime);
        }
        if (action.getCode().equals(CategoryActionTypeEnum.EDIT_CATEGORY_PROPERTY.getCode())) {
            return sendCategoryPropertyList(action, categoryPropertyList, operateTime);
        }
        return null;
    }

    //发送分类属性改动
    public ResultModel sendCategoryPropertyList(CategoryActionTypeEnum action, List<CategoryProperty> categoryPropertyList, long operateTime) throws Exception {
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
        logger.info(params.toJSONString());
        String result = null;
        //抛出通知自定义异常
        try {
            result = trcService.sendCategoryPropertyList(CATEGORY_PROPERTY_URL, params.toJSONString());
        } catch (Exception e) {
            logger.error(ExceptionEnum.TRC_CATEGORY_PROPERTY_EXCEPTION.getMessage() + " :" + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_CATEGORY_PROPERTY_EXCEPTION, e.getMessage());
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.POST + UNDER_LINE);
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, RequestFlowConstant.POST,
                requestNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime());
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    //发送分类品牌改动
    public ResultModel sendCategoryBrandList(CategoryActionTypeEnum action, List<CategoryBrand> categoryBrandList, long operateTime) throws Exception {
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
        logger.info(params.toJSONString());
        String result = null;
        //抛出通知自定义异常
        try {
            result = trcService.sendCategoryBrandList(CATEGORY_BRAND_URL, params.toJSONString());
        } catch (Exception e) {
            logger.error(ExceptionEnum.TRC_CATEGORY_BRAND_EXCEPTION.getMessage() + " :" + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_CATEGORY_BRAND_EXCEPTION, e.getMessage());
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.POST + UNDER_LINE);
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, RequestFlowConstant.POST,
                requestNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime());
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    //发送分类改动
    public ResultModel sendCategoryToTrc(CategoryActionTypeEnum action, Category oldCategory, Category category, long operateTime) throws Exception {
        Assert.notNull(category.getIsValid(), "是否停用不能为空");
        Assert.notNull(category.getName(), "分类名称不能为空");
        Assert.notNull(category.getClassifyDescribe(), "分类描述不能为空");
        Assert.notNull(category.getSort(), "分类排序不能为空");
        //判断是否通知
        if (oldCategory.getName().equals(category.getName()) && oldCategory.getIsValid().equals(category.getIsValid())) {
            return new ResultModel("1", "无需通知分类变更");
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
        logger.info(params.toJSONString());
        String result = null;
        //抛出通知自定义异常
        try {
            result = trcService.sendCategoryToTrc(CATEGORY_URL, params.toJSONString());
        } catch (Exception e) {
            logger.error(ExceptionEnum.TRC_CATEGORY_EXCEPTION.getMessage() + " :" + e.getMessage());
            throw new TrcException(ExceptionEnum.TRC_CATEGORY_EXCEPTION, e.getMessage());
        }
        ResultModel resultModel = JSONObject.parseObject(result, ResultModel.class);
        //存储请求记录
        String requestNum = GuidUtil.getNextUid(RequestFlowConstant.POST + UNDER_LINE);
        RequestFlow requestFlow = new RequestFlow(RequestFlowConstant.GYL, RequestFlowConstant.TRC, RequestFlowConstant.POST,
                requestNum, resultModel.getStatus(), params.toJSONString(), result, Calendar.getInstance().getTime());
        requestFlowService.insert(requestFlow);
        return resultModel;
    }

    public static void main(String[] args) throws Exception {
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
        String result = HttpClientUtil.httpPostJsonRequest("http://10.200.5.3/brands", params.toJSONString(), 10000);
        System.out.println("********返回值********");
        System.out.println(result);
    }
}
