package org.trc.biz.impl.trc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.impl.goods.GoodsBiz;
import org.trc.biz.impl.trc.model.Skus2;
import org.trc.biz.impl.trc.model.SkusProperty;
import org.trc.biz.trc.ITrcBiz;
import org.trc.constant.RequestFlowConstant;
import org.trc.constants.SupplyConstants;
import org.trc.domain.System.Channel;
import org.trc.domain.System.SellChannel;
import org.trc.domain.afterSale.AfterSaleOrder;
import org.trc.domain.afterSale.AfterSaleOrderDetail;
import org.trc.domain.afterSale.AfterSaleWarehouseNotice;
import org.trc.domain.afterSale.AfterSaleWarehouseNoticeDetail;
import org.trc.domain.category.*;
import org.trc.domain.config.RequestFlow;
import org.trc.domain.config.SystemConfig;
import org.trc.domain.forTrc.PropertyValueForTrc;
import org.trc.domain.goods.*;
import org.trc.domain.order.*;
import org.trc.domain.supplier.Supplier;
import org.trc.domain.supplier.SupplierApply;
import org.trc.domain.supplier.SupplierApplyAudit;
import org.trc.domain.supplier.SupplierBrand;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleOrderStatusEnum;
import org.trc.enums.AfterSaleOrderEnum.AfterSaleWarehouseNoticeStatusEnum;
import org.trc.enums.AfterSaleOrderEnum.launchTypeEnum;
import org.trc.enums.AfterSaleOrderEnum.returnSceneEnum;
import org.trc.enums.*;
import org.trc.exception.AfterSaleException;
import org.trc.exception.AfterSaleWarehousesNoticeException;
import org.trc.exception.ParamValidException;
import org.trc.exception.TrcException;
import org.trc.form.TrcConfig;
import org.trc.form.TrcParam;
import org.trc.form.afterSale.AfterSaleWaybillForm;
import org.trc.form.afterSale.TaiRanAfterSaleOrderDetail;
import org.trc.form.afterSale.TairanAfterSaleOrderDO;
import org.trc.form.goods.ExternalItemSkuForm;
import org.trc.form.goods.SkusForm;
import org.trc.form.supplier.SupplierForm;
import org.trc.form.trc.BrandForm2;
import org.trc.form.trc.CategoryForm2;
import org.trc.form.trc.ItemsForm2;
import org.trc.form.trcForm.PropertyFormForTrc;
import org.trc.form.warehouse.ScmInventoryQueryResponse;
import org.trc.form.warehouse.ScmReturnInOrderDetail;
import org.trc.form.warehouse.ScmReturnOrderCreateRequest;
import org.trc.form.warehouse.ScmReturnOrderCreateResponse;
import org.trc.form.warehouse.entryReturnOrder.ScmCancelAfterSaleOrderRequest;
import org.trc.form.warehouse.entryReturnOrder.ScmCancelAfterSaleOrderResponse;
import org.trc.form.warehouse.entryReturnOrder.ScmSubmitAfterSaleOrderLogisticsRequest;
import org.trc.form.warehouseInfo.TaiRanWarehouseInfo;
import org.trc.model.BrandToTrcDO;
import org.trc.model.CategoryToTrcDO;
import org.trc.model.PropertyToTrcDO;
import org.trc.model.ToGlyResultDO;
import org.trc.service.IQimenService;
import org.trc.service.ITrcService;
import org.trc.service.System.ISellChannelService;
import org.trc.service.afterSale.IAfterSaleOrderDetailService;
import org.trc.service.afterSale.IAfterSaleOrderService;
import org.trc.service.afterSale.IAfterSaleWarehouseNoticeDetailService;
import org.trc.service.afterSale.IAfterSaleWarehouseNoticeService;
import org.trc.service.category.ICategoryService;
import org.trc.service.category.IPropertyService;
import org.trc.service.category.IPropertyValueService;
import org.trc.service.config.ILogInfoService;
import org.trc.service.config.IRequestFlowService;
import org.trc.service.config.ISystemConfigService;
import org.trc.service.goods.*;
import org.trc.service.impl.category.BrandService;
import org.trc.service.impl.system.ChannelService;
import org.trc.service.order.IOrderItemService;
import org.trc.service.order.IPlatformOrderService;
import org.trc.service.order.IShopOrderService;
import org.trc.service.order.IWarehouseOrderService;
import org.trc.service.outbound.IOutBoundOrderService;
import org.trc.service.outbound.IOutboundDetailService;
import org.trc.service.supplier.ISupplierApplyService;
import org.trc.service.supplier.ISupplierBrandService;
import org.trc.service.supplier.ISupplierService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouse.IWarehouseExtService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
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
    @Autowired
    private ISystemConfigService systemConfigService;
    @Autowired
    private ISellChannelService sellChannelService;
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IQimenService qimenService;
    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;
    @Autowired
    private IWarehouseExtService warehouseExtService;
    @Autowired
    private IAfterSaleOrderService afterSaleOrderService;
    @Autowired
    private IShopOrderService shopOrderService;
    @Autowired
    private IPlatformOrderService platformOrderService;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private IOrderItemService orderItemService;
    @Autowired
    private IWarehouseOrderService warehouseOrderService;
    @Autowired
    private IAfterSaleOrderDetailService afterSaleOrderDetailService;
    @Autowired
    private IAfterSaleWarehouseNoticeDetailService afterSaleWarehouseNoticeDetailService;
    @Autowired
    private IAfterSaleWarehouseNoticeService afterSaleWarehouseNoticeService;
    @Autowired
    private IWarehouseApiService warehouseApiService;
    @Autowired
    private ILogInfoService logInfoService;
    @Autowired
    private IOutBoundOrderService outBoundOrderService;
    @Autowired
    private IOutboundDetailService outboundDetailService;

    private static final String AFTER_SALE_ORDER_DETAIL_ID="AFTERD-";
	private static final String AFTER_SALE_ORDER_ID="AFTERO-";
	private static final String AFTER_SALE_WAREHOUSE_NOTICE_ID="AFTERW-";
	private static final String AFTER_SALE_WAREHOUSE_NOTICE_DETAIL_ID="AFTERN-";

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
            //setSkuStock(updateSkus);
        }
        List<Skus> noticeSkus = new ArrayList<Skus>();
        for(Skus skus2: updateSkus){
            SkuRelation skuRelation = new SkuRelation();
            skuRelation.setChannelCode(RequestFlowConstant.TRC);
            skuRelation.setSpuCode(skus2.getSpuCode());
            skuRelation.setSkuCode(skus2.getSkuCode());
            skuRelation = skuRelationService.selectOne(skuRelation);
            if(null != skuRelation){
            	skus2.setName(skus2.getSkuName());// trc那边统一用name字段表示skuName字段，所以此处需要设置下
                noticeSkus.add(skus2);
            }
        }
        if(noticeSkus.size() == 0){
            return new ToGlyResultDO(SuccessFailureEnum.SUCCESS.getCode(), "商品修改无需同步");
        }
        //设置自采sku库存
        setSkusStock(noticeSkus);
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

    /**
     * 设置自采sku库存
     * @param skusList
     */
    private void setSkusStock(List<Skus> skusList){
        //查询SKU相关库存信息,直接调用京东接口查库存
        //通知成功的仓库
        List<ScmInventoryQueryResponse> inventoryQueryResponseList = new ArrayList<>();
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setOwnerWarehouseState(OwnerWarehouseStateEnum.NOTICE_SUCCESS.getCode());//通知成功
        warehouseInfo.setIsValid(ZeroToNineEnum.ONE.getCode());//启用
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.select(warehouseInfo);
        if(CollectionUtils.isEmpty(warehouseInfoList)){
            logger.warn("自采SKU没有查询到可用仓库");
        }
        List<String> skuCodes =  new ArrayList<>();
        for(Skus skus: skusList){
            skuCodes.add(skus.getSkuCode());
        }
        inventoryQueryResponseList= warehouseExtService.getWarehouseInventory(skuCodes,null);
        if (!AssertUtil.collectionIsEmpty(inventoryQueryResponseList)){
            List<SkuStock> skuStockList = new ArrayList<>();
            //sku计算库存总和
            for (String skuCode:skuCodes) {
                //先获取到该sku关联的仓库
                WarehouseItemInfo warehouseItemInfo = new WarehouseItemInfo();
                warehouseItemInfo.setSkuCode(skuCode);
                //通知成功的状态
                warehouseItemInfo.setNoticeStatus(4);
                List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.select(warehouseItemInfo);
                if (AssertUtil.collectionIsEmpty(warehouseItemInfoList)){
                    logger.warn("自采SKU"+skuCode+"没有查询到仓库关联信息");
                    continue;
                }
                List<String> itemIds = new ArrayList<>();
                for (WarehouseItemInfo warehouseItem:warehouseItemInfoList) {
                    itemIds.add(warehouseItem.getWarehouseItemId());
                }
                for (ScmInventoryQueryResponse inventoryQueryResponse:inventoryQueryResponseList ) {
                    SkuStock skuStock = new SkuStock();
                    skuStock.setSkuCode(skuCode);
                    boolean flag = false;
                    for (String itemId:itemIds) {
                        if (StringUtils.equals(inventoryQueryResponse.getItemId(),itemId)){
                            flag =true;
                        }
                    }
                    if (flag){
                        //判断库存类型,可销售
                        if (StringUtils.equals(inventoryQueryResponse.getInventoryType(),JingdongInventoryTypeEnum.SALE.getCode())&&StringUtils.equals(inventoryQueryResponse.getInventoryStatus(),JingdongInventoryStateEnum.GOOD.getCode())){
                            skuStock.setAvailableInventory((inventoryQueryResponse.getQuantity()==null?0:inventoryQueryResponse.getQuantity())+(skuStock.getAvailableInventory()==null?0:skuStock.getAvailableInventory()));
                        }
                    }
                    skuStockList.add(skuStock);
                }
            }
            for(SkuStock skuStock: skuStockList){
                for(Skus skus: skusList){
                    if(StringUtils.equals(skus.getSkuCode(), skuStock.getSkuCode())){
                        skus.setAvailableInventory((skuStock.getAvailableInventory() == null ? 0 : skuStock.getAvailableInventory()) + (skus.getAvailableInventory() == null ? 0 : skus.getAvailableInventory()));
                        skus.setRealInventory((skuStock.getLockInventory() == null ? 0 : skuStock.getLockInventory()) + (skus.getRealInventory() == null ? 0 : skus.getRealInventory()));
                        if (null == skus.getAvailableInventory()) {
                            skus.setAvailableInventory(null);
                        }
                        if (null == skus.getRealInventory()) {
                            skus.setRealInventory(null);
                        }
                        skus.setStock(skus.getAvailableInventory());
                    }
                }
            }
        }
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
        List<ExternalItemSku> externalItemSkuList2 = new ArrayList<>(externalItemSkuList);
        List<ExternalItemSku> oldExternalItemSkuList2 = new ArrayList<>(oldExternalItemSkuList);
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
                    oldExternalItemSkuList2.remove(externalItemSku);
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
                    externalItemSkuList2.remove(externalItemSku);
                }
            }
        }else{
            return toGlyResult;
        }
        for(ExternalItemSku externalItemSku: externalItemSkuList2){
            for(ExternalItemSku externalItemSku2: oldExternalItemSkuList2){
                if(StringUtils.equals(externalItemSku.getSkuCode(), externalItemSku2.getSkuCode())){
                	// 以下字段变更需要通知泰然城
                    if(getLongVal(externalItemSku.getSupplierPrice()) != getLongVal(externalItemSku2.getSupplierPrice()) ||
                            getLongVal(externalItemSku.getSupplyPrice()) != getLongVal(externalItemSku2.getSupplyPrice()) ||
                            getLongVal(externalItemSku.getMarketReferencePrice()) != getLongVal(externalItemSku2.getMarketReferencePrice()) ||
                            getLongVal(externalItemSku.getStock()) != getLongVal(externalItemSku2.getStock()) ||
                            !StringUtils.equals(externalItemSku.getBarCode(), externalItemSku2.getBarCode()) ||
                            !StringUtils.equals(externalItemSku.getIsValid(), externalItemSku2.getIsValid()) ||
                            !StringUtils.equals(externalItemSku.getMainPictrue(), externalItemSku2.getMainPictrue()) ||
                            !StringUtils.equals(externalItemSku.getDetailPictrues(), externalItemSku2.getDetailPictrues()) ||
                            getIntVal(externalItemSku.getMinBuyCount()) != getIntVal(externalItemSku2.getMinBuyCount()) ||
                            (StringUtils.isBlank(externalItemSku2.getMainPictrue2()) && StringUtils.isBlank(externalItemSku2.getDetailPictrues2()))
                            ) {
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
            logger.error(String.format("代发商品%s更新通知渠道失败,渠道返回失败信息:%s", JSON.toJSONString(oldExternalItemSkuList2), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_FAILED.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SOCKET_TIME_OUT.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("代发商品%s更新通知渠道超时,渠道返回错误信息:%s", JSON.toJSONString(oldExternalItemSkuList2), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_TIME_OUT.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.SUCCESS.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("代发商品%s更新通知渠道成功,渠道返回信息:%s", JSON.toJSONString(oldExternalItemSkuList2), toGlyResultDO.getMsg()));
            requestFlowUpdate.setStatus(RequestFlowStatusEnum.SEND_SUCCESS.getCode());
        }
        if(StringUtils.equals(SuccessFailureEnum.ERROR.getCode(), toGlyResultDO.getStatus())){
            logger.error(String.format("代发商品%s更新通知渠道错误,渠道返回错误信息:%s", JSON.toJSONString(oldExternalItemSkuList2), toGlyResultDO.getMsg()));
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
    private int getIntVal(Integer val){
    	if(null == val)
    		return 0;
    	else{
    		return val.intValue();
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
    @Cacheable(value = SupplyConstants.Cache.OUT_GOODS_QUERY)
    public Pagenation<ExternalItemSku> externalItemSkuPage(ExternalItemSkuForm queryModel, Pagenation<ExternalItemSku> page,String channelCode) throws Exception {
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(queryModel.getSupplierCode())) {
            Supplier supplier = getSupplier(queryModel.getSupplierCode());
            if(null != supplier){
                if(StringUtils.equals(SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING, supplier.getSupplierKindCode())){//一件代发供应商
                    criteria.andEqualTo("supplierCode", supplier.getSupplierInterfaceId());
                }else{
                    criteria.andEqualTo("supplierCode", queryModel.getSupplierCode());
                }
            }else{
               return page;
            }
        }
        List<SkuRelation> skuRelationList = getSkuRelation(GoodsTypeEnum.SUPPLIER.getCode().intValue());
        if (StringUtils.isNotBlank(queryModel.getSkuRelationStatus())){
            List<String> relationSkus = new ArrayList<>();
            if(!CollectionUtils.isEmpty(skuRelationList)){
                for(SkuRelation skuRelation: skuRelationList){
                    relationSkus.add(skuRelation.getSkuCode());
                }
            }
            if(StringUtils.equals(SkuRelationStatusEnum.RELATION.getCode(), queryModel.getSkuRelationStatus())){//已关联
                if(CollectionUtils.isEmpty(relationSkus)){
                    return page;
                }
                criteria.andIn("skuCode", relationSkus);
            }else if(StringUtils.equals(SkuRelationStatusEnum.NOT_RELATION.getCode(), queryModel.getSkuRelationStatus())){//未关联
                if(!CollectionUtils.isEmpty(relationSkus)){
                    criteria.andNotIn("skuCode", relationSkus);
                }
            }
            if (StringUtils.isNotBlank(queryModel.getSkuCode())) {//商品SKU编号
                criteria.andLike("skuCode", "%" + queryModel.getSkuCode() + "%");
            }
        }else{
            if (StringUtils.isNotBlank(queryModel.getSkuCode())) {//商品SKU编号
                criteria.andLike("skuCode", "%" + queryModel.getSkuCode() + "%");
            }
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
        if (StringUtils.isNotBlank(queryModel.getCategory())) {//分类
            criteria.andLike("category", "%" + queryModel.getCategory() + "%");
        }
        if (StringUtils.isNotBlank(queryModel.getBarCode())) {//条形码
            criteria.andLike("barCode", "%" + queryModel.getBarCode() + "%");
        }
        if (StringUtils.isNotBlank(channelCode)){
            //查询到当前渠道下审核通过的一件代发供应商
            Example example2 = new Example(SupplierApply.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andEqualTo("status", ZeroToNineEnum.TWO.getCode());
            criteria2.andEqualTo("channelCode",channelCode);
            List<SupplierApply> supplierApplyList = supplierApplyService.selectByExample(example2);
            AssertUtil.notEmpty(supplierApplyList,"当前渠道没有一件代发供应商!");
            List<String>  supplierInterfaceIdList = new ArrayList<>();
            for (SupplierApply supplierApply:supplierApplyList) {
                Supplier supplier = new Supplier();
                supplier.setSupplierCode(supplierApply.getSupplierCode());
                supplier.setSupplierKindCode(SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING);
                supplier=  supplierService.selectOne(supplier);
                if (null!=supplier){
                    supplierInterfaceIdList.add(supplier.getSupplierInterfaceId());
                }
            }
            criteria.andIn("supplierCode",supplierInterfaceIdList);
        }
        example.orderBy("supplierCode").desc();
        page = externalItemSkuService.pagination(example, page, queryModel);
        if(!CollectionUtils.isEmpty(page.getResult())){
            setMoneyWeight(page.getResult());
            setSupplierInfo(page.getResult());
            if(StringUtils.isNotBlank(queryModel.getSkuRelationStatus())) {
                //设置sku的关联状态
                setSkuRelationStatus(queryModel.getSkuRelationStatus(), skuRelationList, page.getResult());
            }else {
                //设置sku的关联状态
                setSkuRelationStatus(null, skuRelationList, page.getResult());
            }
        }
        return page;
    }

    /**
     * 设置代付供应商信息
     * @param externalItemSkuList
     */
    private void setSupplierInfo(List<ExternalItemSku> externalItemSkuList){
        Set<String> supplierCodes = new HashSet<>();
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            supplierCodes.add(externalItemSku.getSupplierCode());
        }
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("supplierInterfaceId", supplierCodes);
        criteria.andEqualTo("supplierKindCode", SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING);//一件代付供应商
        List<Supplier> supplierList = supplierService.selectByExample(example);
        for(ExternalItemSku externalItemSku: externalItemSkuList){
            for(Supplier supplier: supplierList){
                if(StringUtils.equals(externalItemSku.getSupplierCode(), supplier.getSupplierInterfaceId())){
                    externalItemSku.setSupplierCode2(externalItemSku.getSupplierCode());
                    externalItemSku.setSupplierCode(supplier.getSupplierCode());
                    externalItemSku.setSupplierName(supplier.getSupplierName());
                    break;
                }
            }
        }
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
    @Cacheable(value = SupplyConstants.Cache.SUPPLIER)
    public Pagenation<Supplier> supplierPage(SupplierForm queryModel, Pagenation<Supplier> page,String channelCode) throws Exception {
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
        criteria.andEqualTo("supplierKindCode", SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING);//一件代发供应商
        example.orderBy("isValid").desc();
        example.orderBy("updateTime").desc();
        page = supplierService.pagination(example, page, queryModel);
        List<Supplier> supplierList = page.getResult();
        if (StringUtils.isNotBlank(channelCode)){
            Example example2 = new Example(SupplierApply.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andEqualTo("status",ZeroToNineEnum.TWO.getCode());
            criteria2.andEqualTo("channelCode",channelCode);
            List<SupplierApply> supplierApplyList = supplierApplyService.selectByExample(example2);
            List<Supplier> supplierResultList =  new ArrayList<>();
            if (!AssertUtil.collectionIsEmpty(supplierList)) {
                for (Supplier  supplierResult:supplierList) {
                    boolean isAudit = false;
                    for (SupplierApply  supplierApply:supplierApplyList) {
                        if (StringUtils.equals(supplierResult.getSupplierCode(),supplierApply.getSupplierCode())){
                            isAudit = true;}
                    }
                    if (isAudit){
                        supplierResultList.add(supplierResult);
                    }
                }
            }
            page.setResult(supplierResultList);
        }
        handlerSupplierPage(page);
        //分页查询
        return page;
    }

    @Override
    public Pagenation<Skus2> skusPage(SkusForm form, Pagenation<Skus> page, String channelCode) {
        Pagenation<Skus2> page2 = new Pagenation<Skus2>();
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        List<SkuRelation> skuRelationList = getSkuRelation(GoodsTypeEnum.SELF_PURCHARSE.getCode().intValue());
        if (StringUtils.isNotBlank(form.getSkuRelationStatus())){
            List<String> relationSkus = new ArrayList<>();
            if(!CollectionUtils.isEmpty(skuRelationList)){
                for(SkuRelation skuRelation: skuRelationList){
                    relationSkus.add(skuRelation.getSkuCode());
                }
            }
            if(StringUtils.equals(SkuRelationStatusEnum.RELATION.getCode(), form.getSkuRelationStatus())){//已关联
                if(CollectionUtils.isEmpty(relationSkus)){
                    return page2;
                }
                criteria.andIn("skuCode", relationSkus);
            }else if(StringUtils.equals(SkuRelationStatusEnum.NOT_RELATION.getCode(), form.getSkuRelationStatus())){//未关联
                if(!CollectionUtils.isEmpty(relationSkus)){
                    criteria.andNotIn("skuCode", relationSkus);
                }
            }
            if (StringUtils.isNotBlank(form.getSpuCode())){
                criteria.andEqualTo("spuCode",form.getSpuCode());
            }
            if (StringUtils.isNotBlank(form.getSkuCode())){
                criteria.andEqualTo("skuCode",form.getSkuCode());
            }
        }else{
            if (StringUtils.isNotBlank(form.getSpuCode())){
                criteria.andEqualTo("spuCode",form.getSpuCode());
            }
            if (StringUtils.isNotBlank(form.getSkuCode())){
                criteria.andEqualTo("skuCode",form.getSkuCode());
            }
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

        //setSkuStock(page.getResult());
        //设置库存
        setStock(page.getResult(), channelCode);

        BeanUtils.copyProperties(page, page2,"result");
        List<Skus2> skus2List = new ArrayList<>();
        for(Skus skus: page.getResult()){
            Skus2 skus2 = new Skus2();
            BeanUtils.copyProperties(skus, skus2);
            skus2.setName(skus.getSkuName()); // 将“SKU名称”赋值给接口中的“商品名称”给到渠道（原先传的是SPU信息中的商品名称）；
            skus2List.add(skus2);
        }
        //设置SPU商品信息
        setSpuInfo(skus2List);
        //设置SKU属性信息
        setSkuPropertyInfo(skus2List);
        /**
         * sku数据剔除及关联状态设置
         */
        if(StringUtils.isNotBlank(form.getSkuRelationStatus())) {
            //设置sku的关联状态
            setSkuRelationStatus(form.getSkuRelationStatus(), skuRelationList, skus2List);
        }else {
            //设置sku的关联状态
            setSkuRelationStatus(null, skuRelationList, skus2List);
        }
        page2.setResult(skus2List);
        return page2;

    }

    private List<String> getSkuCodes(List<Skus> skusList){
        List<String> skuCodeList = new ArrayList<String>();
        for(Skus s : skusList){
            skuCodeList.add(s.getSkuCode());
        }
        return skuCodeList;
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
                    items2.setBrandCode(c.getBrandCode());
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
            for(Category category: thridCategories){
                if(items2.getCategoryId().longValue() == category.getId().longValue()){
                    items2.setCategoryCode(category.getCategoryCode());
                    break;
                }
            }
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
            if (tmps.length > 0) {
                //第一级分类名称
                for (Category c2 : categories) {
                    if (Long.parseLong(tmps[0]) == c2.getId()) {
                        sb.append(c2.getName());
                        break;
                    }
                }
            }
            if (tmps.length > 1) {
                //第二级分类名称
                for (Category c2 : categories) {
                    if (Long.parseLong(tmps[1]) == c2.getId()) {
                        sb.append(GoodsBiz.CATEGORY_NAME_SPLIT_SYMBOL).append(c2.getName());
                        break;
                    }
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
    private void setSpuInfo(List<?> skusList){
        StringBuilder sb = new StringBuilder();
        for(Object skus: skusList){
            if(skus instanceof Skus){
                Skus _skus = (Skus) skus;
                sb.append("\"").append(_skus.getSpuCode()).append("\"").append(SupplyConstants.Symbol.COMMA);
            }
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
                for(Object skus: skusList){
                    Skus _skus = (Skus) skus;
                    for(Items items: itemsList){
                        if(StringUtils.equals(_skus.getSpuCode(), items.getSpuCode())){
                            _skus.setName(items.getName());
                            _skus.setBrandCode(items.getBrandCode());
                            _skus.setBrandName(items.getBrandName());
                            _skus.setCategoryCode(items.getCategoryCode());
                            _skus.setCategoryName(items.getCategoryName());
                            _skus.setMainPicture(items.getMainPicture());// 设置spu主图信息
                            if(skus instanceof Skus2){
                                ((Skus2)_skus).setCategoryId(items.getCategoryId());
                                ((Skus2)_skus).setBrandId(items.getBrandId());
                                ((Skus2)_skus).setItemNo(items.getItemNo());
                                ((Skus2)_skus).setProducer(items.getProducer());
                                ((Skus2)_skus).setTradeType(items.getTradeType());
                            }
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
            	skus.setName(skus.getSkuName());// 将skuName赋值给name，提供给泰然城用
                for(SkuStock skuStock: skuStockList){
                    if(StringUtils.equals(skus.getSkuCode(), skuStock.getSkuCode())){
                        skus.setStock(skuStock.getAvailableInventory());
                    }
                }
            }
        }
    }

    public void setStock(List<Skus> skusList, String channelCode){
        if(skusList == null || skusList.size() < 1){
            return;
        }
        for(Skus skus: skusList){
            skus.setStock(0l);
        }
        //获取仓库库存
        List<ScmInventoryQueryResponse> scmInventoryQueryResponseList = getWarehouseInventory(skusList);
        //合并同skuCode库存
        Map<String, Long> map = this.getSkuMap(scmInventoryQueryResponseList);
        //赋值stock
        this.setSkuStock(skusList, map);
    }

    //赋值stock
    private void setSkuStock(List<Skus> skusList, Map<String, Long> map){
        for(Skus skus : skusList){
            String skuCode = skus.getSkuCode();
            if(map.containsKey(skuCode)){
                skus.setStock(map.get(skuCode));
            }else{
                skus.setStock(0L);
            }
        }
    }

    /**
     * 整合sku库存信息
     * @param scmInventoryQueryResponseList
     * @return
     */
    private Map<String, Long> getSkuMap(List<ScmInventoryQueryResponse> scmInventoryQueryResponseList){
        Map<String, Long> skuMap = new HashMap<String, Long>();
        for(ScmInventoryQueryResponse response : scmInventoryQueryResponseList){
            String skuCode = response.getItemCode();
            Long stockNum = response.getQuantity();
            if(skuMap.containsKey(skuCode)){
                skuMap.put(skuCode, stockNum + skuMap.get(skuCode));
            }else{
                skuMap.put(skuCode, stockNum);
            }
        }
        return skuMap;
    }

    /**
     * 获取仓库商品库存
     * @param skusList
     * @return
     */
    private List<ScmInventoryQueryResponse> getWarehouseInventory(List<Skus> skusList){
        List<ScmInventoryQueryResponse> scmInventoryQueryResponseList = new ArrayList<>();
        Set<String> skuCodes = new HashSet<>();
        for(Skus skus: skusList){
            skuCodes.add(skus.getSkuCode());
        }
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setOwnerWarehouseState(OwnerWarehouseStateEnum.NOTICE_SUCCESS.getCode());//通知成功
        warehouseInfo.setIsValid(ZeroToNineEnum.ONE.getCode());//启用
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.select(warehouseInfo);
        if(CollectionUtils.isEmpty(warehouseInfoList)){
            logger.warn("自采SKU没有查询到可用仓库");
            return scmInventoryQueryResponseList;
        }
        try{
            scmInventoryQueryResponseList = warehouseExtService.getWarehouseInventory(new ArrayList<>(skuCodes),JingdongInventoryTypeEnum.SALE.getCode());
        }catch (Exception e){
            logger.error("查询库存异常", e);
        }
        return scmInventoryQueryResponseList;
    }


    /**
     * 根据业务线获取所有仓库信息
     * @param channelCode
     * @return
     */
    private List<WarehouseInfo> getWharehouseInfoListByChannelCode(String channelCode){
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        if(StringUtils.isNotEmpty(channelCode)){
            warehouseInfo.setChannelCode(channelCode);
        }
        warehouseInfo.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        warehouseInfo.setOwnerWarehouseState(ZeroToNineEnum.ONE.getCode());
        return warehouseInfoService.select(warehouseInfo);
    }

    /**
     * 根据skuCode和业务线获取奇门库存
     * @return
     */
    /*private List<InventoryQueryResponse.Item> getQimenStockByskuCode(List<String> skuCodes, String channelCode){
        //根据业务线获取所有仓库信息
        List<WarehouseInfo> warehouseInfoList = this.getWharehouseInfoListByChannelCode(channelCode);
        AssertUtil.notNull(warehouseInfoList,"当前业务线没有对应的库存仓库信息!");

        //调用奇门库存查询接口校验绑定过商品的库存
        InventoryQueryRequest request = new InventoryQueryRequest();
        InventoryQueryRequest.Criteria criteria = null;
        List<InventoryQueryRequest.Criteria> criteriaList = new ArrayList<>();
        for(WarehouseInfo info : warehouseInfoList){
            String warehouseOwnerId = info.getWarehouseOwnerId();
            for(String skuCode : skuCodes){
                criteria = new InventoryQueryRequest.Criteria();
                criteria.setInventoryType(InventoryTypeEnum.ZP.getCode());//正品
                criteria.setItemCode(skuCode);
                criteria.setOwnerCode(warehouseOwnerId);
                criteriaList.add(criteria);
            }
        }
        request.setCriteriaList(criteriaList);
        AppResult appResult = qimenService.inventoryQuery(request);
        if(!StringUtils.equals(appResult.getAppcode(), ResponseAck.SUCCESS_CODE)){
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("调用奇门库存查询接口失败, %s", appResult.getDatabuffer()));
        }
        AssertUtil.notNull(appResult.getResult(), "调用奇门库存查询接口返回结果数据为空");
        AssertUtil.notBlank(appResult.getResult().toString(), "调用奇门库存查询接口返回结果数据为空");
        InventoryQueryResponse inventoryQueryResponse = null;
        try{
            inventoryQueryResponse = JSON.parseObject(appResult.getResult().toString()).toJavaObject(InventoryQueryResponse.class);
        }catch (ClassCastException e) {
            String msg = String.format("调用奇门库存查询接口返回库存结果信息格式错误,%s", e.getMessage());
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        if(inventoryQueryResponse.isSuccess()){
            return inventoryQueryResponse.getItems();
        }else {
            throw new QimenException(ExceptionEnum.QIMEN_INVENTORY_QUERY_EXCEPTION, inventoryQueryResponse.getMessage());
        }
    }*/

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
    public Pagenation<Items> itemsPage(ItemsForm2 queryModel, Pagenation<Items> page, String channelCode){
        Example example = new Example(Items.class);
        Example.Criteria criteria = example.createCriteria();
        List<SkuRelation> skuRelationList = getSkuRelation(GoodsTypeEnum.SELF_PURCHARSE.getCode().intValue());
        if (StringUtils.isNotBlank(queryModel.getSkuRelationStatus())){
            List<String> relationSpus = new ArrayList<>();
            if(!CollectionUtils.isEmpty(skuRelationList)){
                for(SkuRelation skuRelation: skuRelationList){
                    relationSpus.add(skuRelation.getSpuCode());
                }
            }
            if(StringUtils.equals(SkuRelationStatusEnum.RELATION.getCode(), queryModel.getSkuRelationStatus())){//已关联
                if(CollectionUtils.isEmpty(relationSpus)){
                    return page;
                }
                criteria.andIn("spuCode", relationSpus);
            }else if(StringUtils.equals(SkuRelationStatusEnum.NOT_RELATION.getCode(), queryModel.getSkuRelationStatus())){//未关联
                if(!CollectionUtils.isEmpty(relationSpus)){
                    criteria.andNotIn("spuCode", relationSpus);
                }
            }
            if (org.apache.commons.lang.StringUtils.isNotBlank(queryModel.getSpuCode())){
                criteria.andLike("spuCode","%" + queryModel.getSpuCode() + "%");
            }
        }else{
            if (org.apache.commons.lang.StringUtils.isNotBlank(queryModel.getSpuCode())){
                criteria.andLike("spuCode","%" + queryModel.getSpuCode() + "%");
            }
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
        setItemsSkus(page.getResult(), channelCode);
        return page;
    }

    /**
     *设置sku的关联状态
     * @param skuRelationStatus sku关联状态: 0-未关联,1-已关联
     * @param skuRelationList
     * @param skuList
     */
    private void setSkuRelationStatus(String skuRelationStatus, List<SkuRelation> skuRelationList, List<?> skuList){
        List<Skus> skusList = null;
        List<ExternalItemSku> externalItemSkuList = null;
        for(Object obj: skuList){
            if(obj instanceof Skus){
                skusList = (List<Skus>)skuList;
                break;
            }
            if(obj instanceof ExternalItemSku){
                externalItemSkuList = (List<ExternalItemSku>)skuList;
                break;
            }
        }
        if(StringUtils.isNotBlank(skuRelationStatus)){
            if(null != skusList){
                for(Skus skus: skusList){
                    skus.setSkuRelationStatus(skuRelationStatus);
                }
            }
            if(null != externalItemSkuList){
                for(ExternalItemSku skus: externalItemSkuList){
                    skus.setSkuRelationStatus(skuRelationStatus);
                }
            }
        }else{
            if(null != skusList){
                for(Skus skus: skusList){
                    skus.setSkuRelationStatus(SkuRelationStatusEnum.NOT_RELATION.getCode());
                    if(!CollectionUtils.isEmpty(skuRelationList)){
                        for(SkuRelation skuRelation: skuRelationList){
                            if(StringUtils.equals(skus.getSkuCode(), skuRelation.getSkuCode())){
                                skus.setSkuRelationStatus(SkuRelationStatusEnum.RELATION.getCode());
                                break;
                            }
                        }
                    }
                }
            }
            if(null != externalItemSkuList){
                for(ExternalItemSku skus: externalItemSkuList){
                    skus.setSkuRelationStatus(SkuRelationStatusEnum.NOT_RELATION.getCode());
                    if(!CollectionUtils.isEmpty(skuRelationList)){
                        for(SkuRelation skuRelation: skuRelationList){
                            if(StringUtils.equals(skus.getSkuCode(), skuRelation.getSkuCode())){
                                skus.setSkuRelationStatus(SkuRelationStatusEnum.RELATION.getCode());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置商品相关SKU信息
     * @param itemsList
     */
    private void setItemsSkus(List<Items> itemsList, String channelCode){
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
                //setSkuStock(skusList);
                setStock(skusList, channelCode);
            }
            for(Items items: itemsList){
                List<Skus> records = new ArrayList<Skus>();
                for(Skus skus: skusList){
                    if(StringUtils.equals(items.getSpuCode(), skus.getSpuCode())){
                        records.add(skus);
                    }
                }
                //设置SPU商品信息
                setSpuInfo(records);
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
    @Cacheable(value = SupplyConstants.Cache.PROPERTY)
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
    @Cacheable(value = SupplyConstants.Cache.BRAND)
    public Pagenation<Brand> brandList(BrandForm2 queryModel, Pagenation<Brand> page) throws Exception {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        setQueryParam(example,criteria,queryModel);
        Pagenation<Brand> pagenation = brandService.pagination(example, page, queryModel);
        return pagenation;
    }



    @Override
    @Cacheable(value = SupplyConstants.Cache.CATEGORY)
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

    @Override
    public void checkChannelCode(String channelCode) throws Exception {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setCode("channelCodeCheck");
        systemConfig=systemConfigService.selectOne(systemConfig);
        if (StringUtils.equals(systemConfig.getContent(), ZeroToNineEnum. ONE.getCode())){
            AssertUtil.notBlank(channelCode,"channelCode不能为空!");
            if (StringUtils.isNotBlank(channelCode)){
                Channel channel = new Channel();
                channel.setCode(channelCode);
                channel=  channelService.selectOne(channel);
                AssertUtil.notNull(channel,"渠道编码对应的渠道不存在");
            }
        }
    }

    @Override
    public void checkSellCode(String sellCode) throws Exception {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setCode("sellCodeCheck");
        systemConfig=systemConfigService.selectOne(systemConfig);
        if (StringUtils.equals(systemConfig.getContent(), ZeroToNineEnum. ONE.getCode())){
            AssertUtil.notBlank(sellCode,"sellCode不能为空!");
            if (StringUtils.isNotBlank(sellCode)){
                SellChannel channel = new SellChannel();
                channel.setSellCode(sellCode);
                channel=  sellChannelService.selectOne(channel);
                AssertUtil.notNull(channel,"渠道编码对应的渠道不存在");
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



    /**
     * 根据业务线变获取已经建立关联关系的sku
     * @param flag :1-自采, 2-代发
     * @return
     */
    private List<SkuRelation> getSkuRelation(int flag){
        Example example = new Example(SkuRelation.class);
        Example.Criteria criteria = example.createCriteria();
        if(GoodsTypeEnum.SELF_PURCHARSE.getCode().intValue() == flag){//自采
            criteria.andCondition("supplier_code is null or supplier_code = ''");
        }else if(GoodsTypeEnum.SUPPLIER.getCode().intValue() == flag){//代发
            criteria.andCondition("supplier_code is not null and supplier_code != ''");
        }
        return skuRelationService.selectByExample(example);
    }


    @Override
    public List<Skus> getSkuInformation(String skuCode, String channelCode) {
        AssertUtil.notBlank(skuCode,"查询SKU信息sckCode不能为空");
        AssertUtil.isTrue(skuCode.indexOf(TrcBiz.COMMA_ZH) == -1, "分隔多个sku编码必须是英文逗号");
        String[] skuCodes = skuCode.split(SupplyConstants.Symbol.COMMA);
        for(String _skuCode: skuCodes) {
            AssertUtil.isTrue(_skuCode.startsWith(SupplyConstants.Goods.SKU_PREFIX), String.format("skuCode[%s]不是自采商品", _skuCode));
        }
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", Arrays.asList(skuCodes));
        List<Skus> skusList = skusService.selectByExample(example);
        //setSkuStock(skusList);
        //设置库存
        setStock(skusList, channelCode);
        setSpuInfo(skusList); // 将spu里面的相关信息设置到sku中，例如：spu商品主图mainPicture字段
        //设置sku关联状态
        List<SkuRelation> skuRelationList = getSkuRelation(GoodsTypeEnum.SELF_PURCHARSE.getCode().intValue());
        setSkuRelationStatus(null, skuRelationList, skusList);
        return skusList;
    }


    @Override
    @Cacheable(value = SupplyConstants.Cache.OUT_GOODS_QUERY)
    public List<ExternalItemSku> getExternalSkuInformation(String skuCode, String channelCode) {
        AssertUtil.notBlank(skuCode,"查询SKU信息sckCode不能为空");
        AssertUtil.isTrue(skuCode.indexOf(TrcBiz.COMMA_ZH) == -1, "分隔多个sku编码必须是英文逗号");
        String[] skuCodes = skuCode.split(SupplyConstants.Symbol.COMMA);
        for(String _skuCode: skuCodes){
            AssertUtil.isTrue(_skuCode.startsWith(SupplyConstants.Goods.EXTERNAL_SKU_PREFIX), String.format("skuCode[%s]不是代发商品", _skuCode));
        }
        Example example = new Example(ExternalItemSku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", Arrays.asList(skuCodes));
        criteria.andEqualTo("isValid", ValidEnum.VALID.getCode());
        if (StringUtils.isNotBlank(channelCode)){
            //查询到当前渠道下审核通过的一件代发供应商
            Example example2 = new Example(SupplierApply.class);
            Example.Criteria criteria2 = example2.createCriteria();
            criteria2.andEqualTo("status", ZeroToNineEnum.TWO.getCode());
            criteria2.andEqualTo("channelCode",channelCode);
            List<SupplierApply> supplierApplyList = supplierApplyService.selectByExample(example2);
            AssertUtil.notEmpty(supplierApplyList,"当前渠道没有一件代发供应商!");
            List<String>  supplierInterfaceIdList = new ArrayList<>();
            for (SupplierApply supplierApply:supplierApplyList) {
                Supplier supplier = new Supplier();
                supplier.setSupplierCode(supplierApply.getSupplierCode());
                supplier.setSupplierKindCode(SupplyConstants.Supply.Supplier.SUPPLIER_ONE_AGENT_SELLING);
                supplier=  supplierService.selectOne(supplier);
                if (null!=supplier){
                    supplierInterfaceIdList.add(supplier.getSupplierInterfaceId());
                }
            }
            criteria.andIn("supplierCode",supplierInterfaceIdList);
        }
        List<ExternalItemSku> externalItemSkuList = externalItemSkuService.selectByExample(example);
        if(!CollectionUtils.isEmpty(externalItemSkuList)){
            setMoneyWeight(externalItemSkuList);
            setSupplierInfo(externalItemSkuList);
            //设置sku关联状态
            List<SkuRelation> skuRelationList = getSkuRelation(GoodsTypeEnum.SUPPLIER.getCode().intValue());
            setSkuRelationStatus(null, skuRelationList, externalItemSkuList);
        }
        return externalItemSkuList;
    }

    @Override
    public List<TaiRanWarehouseInfo> returnWarehouseQuery() {
    	List<TaiRanWarehouseInfo> list=new ArrayList<TaiRanWarehouseInfo>();
    	
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isSupportReturn", ZeroToNineEnum.ONE.getCode());
        criteria.andEqualTo("isValid", ValidEnum.VALID.getCode());
        List<WarehouseInfo> warehouseInfoList = warehouseInfoService.selectByExample(example);
        if (!AssertUtil.collectionIsEmpty(warehouseInfoList)){
        	for(WarehouseInfo warehouseInfo:warehouseInfoList) {
        		TaiRanWarehouseInfo taiRanWarehouseInfo=new TaiRanWarehouseInfo();
            	taiRanWarehouseInfo.setCode(warehouseInfo.getCode());
            	taiRanWarehouseInfo.setWarehouseName(warehouseInfo.getWarehouseName());
            	taiRanWarehouseInfo.setWarehouseTypeCode(warehouseInfo.getWarehouseTypeCode());
            	taiRanWarehouseInfo.setWarehouseContactNumber(warehouseInfo.getWarehouseContactNumber());
            	taiRanWarehouseInfo.setWarehouseContact(warehouseInfo.getWarehouseContact());
            	taiRanWarehouseInfo.setProvince(warehouseInfo.getProvince());
            	taiRanWarehouseInfo.setCity(warehouseInfo.getCity());
            	taiRanWarehouseInfo.setArea(warehouseInfo.getArea());
            	taiRanWarehouseInfo.setAllAreaName(warehouseInfo.getAllAreaName());
            	list.add(taiRanWarehouseInfo);
        	}
        	
        }
        return list;
    }

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ResponseAck<Map<String,Object>> afterSaleCreate(TairanAfterSaleOrderDO afterSaleOrderDO) throws Exception{
		Map<String,Object> data=new HashMap<>();

		//参数校验
		afterSaleCreateCheckParam(afterSaleOrderDO);

        //退货场景：0实体店退货，1线上商城退货
        int returnScene=afterSaleOrderDO.getReturnScene();
        //售后类型：0取消发货，1退货
        int afterSaleType=afterSaleOrderDO.getAfterSaleType();

		String shopOrderCode=afterSaleOrderDO.getShopOrderCode();
		List<TaiRanAfterSaleOrderDetail> details=afterSaleOrderDO.getAfterSaleOrderDetailList();
		
		ShopOrder shopOrderselect=new ShopOrder();
		shopOrderselect.setShopOrderCode(shopOrderCode);
		ShopOrder shopOrder=shopOrderService.selectOne(shopOrderselect);
		AssertUtil.notNull(shopOrder, "根据该订单号"+shopOrderCode+"查询到的订单为空!");
		String afterSaleCode=null;
		//线下退货
		if(returnScene==returnSceneEnum.STATUS_0.getCode() && afterSaleType==AfterSaleTypeEnum.RETURN_GOODS.getCode()) {
			 afterSaleCode=ReturnGoods(afterSaleOrderDO,shopOrder,returnScene);
		}
		//线上退货
		if(returnScene==returnSceneEnum.STATUS_1.getCode() && afterSaleType==AfterSaleTypeEnum.RETURN_GOODS.getCode()) {
			//判断该订单能否退货
			boolean canReturn=judgeCanReturn(shopOrderCode,details.get(0));
			if(canReturn) {
				 afterSaleCode=ReturnGoods(afterSaleOrderDO,shopOrder,returnScene);
			}else {
				AssertUtil.isTrue(false,"只有部分发货或者全部发货的状态才能退货!");
			}
		}
		
		//线上取消发货
		if(returnScene==returnSceneEnum.STATUS_1.getCode() && afterSaleType==AfterSaleTypeEnum.CANCEL_DELIVER.getCode()) {
			//判断该订单能否取消发货   确认是等待供应商发货还是 等待仓库发货
			boolean canCancel=judgeCanCancel(shopOrderCode,details.get(0));
			if(canCancel) {
				//创建售后单（待客户发货）  取消失败返回null
				Map<String,Object> result=onlineCancel(afterSaleOrderDO,shopOrder,returnScene);
				boolean res=(boolean) result.get("res");
				afterSaleCode=(String) result.get("afterSaleCode");
				 if(!res) {
					 data.put("afterSaleCode", afterSaleCode);
					 return new ResponseAck("500",(String) result.get("msg"),data);
				 }
			}else {
				AssertUtil.isTrue(false,"只有在仓库未发货状态才能取消!");
			}
		}

		data.put("afterSaleCode", afterSaleCode);
		return new ResponseAck("200","售后单接收成功",data);

	}

    private void afterSaleCreateCheckParam(TairanAfterSaleOrderDO afterSaleOrderDO) {
    	String requestNo=afterSaleOrderDO.getRequestNo();
		AssertUtil.notBlank(requestNo, "请求流水号不能为空 !");
		boolean isExistRequestNo=isExistRequestNo(requestNo);
		AssertUtil.isTrue(!isExistRequestNo, "请勿重复创建售后单!");
		
		String shopOrderCode=afterSaleOrderDO.getShopOrderCode();
		AssertUtil.notBlank(shopOrderCode, "店铺订单号不能为空 !");
		
		String returnWarehouseCode=afterSaleOrderDO.getReturnWarehouseCode();
		AssertUtil.notBlank(returnWarehouseCode, "入库仓库仓库编码不能为空 !");

		String channelCode=afterSaleOrderDO.getChannelCode();
        AssertUtil.notBlank(channelCode, "渠道编码不能为空 !");

		String sellCode=afterSaleOrderDO.getSellCode();
        AssertUtil.notBlank(sellCode, "销售渠道不能为空 !");

        //退货场景：0实体店退货，1线上商城退货
        int returnScene=afterSaleOrderDO.getReturnScene();
        AssertUtil.isTrue((returnScene==0 ||returnScene==1),"returnScene只能传0或者1");
        //售后类型：0取消发货，1退货
        int afterSaleType=afterSaleOrderDO.getAfterSaleType();
        AssertUtil.isTrue((afterSaleType==0 ||afterSaleType==1),"afterSaleType只能传0或者1");

		List<TaiRanAfterSaleOrderDetail> details=afterSaleOrderDO.getAfterSaleOrderDetailList();
		AssertUtil.notEmpty(details, "售后单子订单不能为空!");
		if(details.size()>1) {
			AssertUtil.notNull(null, "售后单详情只能包含一个s" +
                    "ku!");
		}
		
		String skuCode=afterSaleOrderDO.getAfterSaleOrderDetailList().get(0).getSkuCode();
		AssertUtil.notBlank(skuCode, "skuCode编码不能为空 !");
		
		if(skuCode.startsWith("SP1")) {
			AssertUtil.notNull(null, "代发商品暂时不支持售后!");
		}
	}



    private void cancelOutboundOrder(ShopOrder shopOrder, TaiRanAfterSaleOrderDetail taiRanAfterSaleOrderDetail) {
		//根据系统订单号查询发货单号
		OutboundOrder selectOutboundOrder=new OutboundOrder();
		selectOutboundOrder.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		OutboundOrder outboundOrder=outBoundOrderService.selectOne(selectOutboundOrder);
		AssertUtil.notNull(outboundOrder, "根据scmShopOrderCode"+shopOrder.getScmShopOrderCode()+"查询发货单为空!");
		String outboundOrderCode=outboundOrder.getOutboundOrderCode();
		
		OutboundDetail selectOutboundDetail=new OutboundDetail();
		selectOutboundDetail.setOutboundOrderCode(outboundOrderCode);
		selectOutboundDetail.setSkuCode(taiRanAfterSaleOrderDetail.getSkuCode());
		
	}

	private boolean judgeCanCancel(String shopOrderCode, TaiRanAfterSaleOrderDetail taiRanAfterSaleOrderDetail) {
		OrderItem orderItemSelect=new OrderItem();
		orderItemSelect.setShopOrderCode(shopOrderCode);
		AssertUtil.notBlank(taiRanAfterSaleOrderDetail.getSkuCode(), "skuCode不能为空!");
		orderItemSelect.setSkuCode(taiRanAfterSaleOrderDetail.getSkuCode());
		OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
		AssertUtil.notNull(orderItem, "根据订单号"+shopOrderCode+"和sku:"+taiRanAfterSaleOrderDetail.getSkuCode()+"查询子订单为空!");
		if(orderItem.getSupplierOrderStatus().equals(OrderItemDeliverStatusEnum.WAIT_WAREHOUSE_DELIVER.getCode())) {
			return true;
		}
		return false;
	}

	private boolean judgeCanReturn(String shopOrderCode, TaiRanAfterSaleOrderDetail taiRanAfterSaleOrderDetail) {
		OrderItem orderItemSelect=new OrderItem();
		orderItemSelect.setShopOrderCode(shopOrderCode);
		AssertUtil.notBlank(taiRanAfterSaleOrderDetail.getSkuCode(), "skuCode不能为空!");
		orderItemSelect.setSkuCode(taiRanAfterSaleOrderDetail.getSkuCode());
		OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
		AssertUtil.notNull(orderItem, "根据订单号"+shopOrderCode+"和sku:"+taiRanAfterSaleOrderDetail.getSkuCode()+"查询子订单为空!");
		if(orderItem.getSupplierOrderStatus().equals(OrderItemDeliverStatusEnum.ALL_DELIVER.getCode()) ||
				orderItem.getSupplierOrderStatus().equals(OrderItemDeliverStatusEnum.PARTS_DELIVER.getCode())	) {
			return true;
		}
		return false;
	}

	private Map<String,Object> onlineCancel(TairanAfterSaleOrderDO afterSaleOrderDO, ShopOrder shopOrder,int returnScene) throws Exception{
		Map<String, Object> resultmap=new HashMap<>();
		String shopOrderCode=afterSaleOrderDO.getShopOrderCode();
		
		PlatformOrder platformOrderSelect=new PlatformOrder();
		platformOrderSelect.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
		//platformOrderSelect.setChannelCode(shopOrder.getChannelCode());
		PlatformOrder platformOrder=platformOrderService.selectOne(platformOrderSelect);
		AssertUtil.notNull(platformOrder, "根据该平台订单编码"+shopOrder.getPlatformOrderCode()+"查询到的平台订单信息为空!");
		
		WarehouseInfo selectWarehouse=new WarehouseInfo();
		AssertUtil.notBlank(afterSaleOrderDO.getReturnWarehouseCode(), "仓库编号为空!");
		selectWarehouse.setCode(afterSaleOrderDO.getReturnWarehouseCode());
		WarehouseInfo warehouseInfo=warehouseInfoService.selectOne(selectWarehouse);
		AssertUtil.notNull(warehouseInfo,"该仓库编号"+afterSaleOrderDO.getReturnWarehouseCode()+"查询到的仓库为空!");

		//查询该订单创建售后单的数量
		int afterSaleNum=getCount(shopOrder);
		//售后单编号
		String afterSaleCode =SupplyConstants.Serial.AFTER_SALE_CODE+"-"+shopOrder.getScmShopOrderCode()+"-"+(afterSaleNum+1);
        			
		//售后单
		AfterSaleOrder afterSaleOrder=getAfterSaleOrderCancel(afterSaleCode,shopOrder,afterSaleOrderDO,platformOrder,warehouseInfo,returnScene);
		
		TaiRanAfterSaleOrderDetail afterSaleOrderDetailDO=afterSaleOrderDO.getAfterSaleOrderDetailList().get(0);
		OrderItem orderItemSelect=new OrderItem();
		orderItemSelect.setShopOrderCode(shopOrderCode);
		AssertUtil.notBlank(afterSaleOrderDetailDO.getSkuCode(), "子订单的sku不能为空!");
		orderItemSelect.setSkuCode(afterSaleOrderDetailDO.getSkuCode());
		OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
		AssertUtil.notNull(orderItem, "根据订单号"+shopOrderCode+"和sku:"+afterSaleOrderDetailDO.getSkuCode()+"查询子订单为空!");
		//售后单子单
		getAfterSaleOrderDetail(orderItem,afterSaleOrderDetailDO,afterSaleCode);

		//调用 接口通知子系统
		Map<String, Object> map=afterSaleOrderService.deliveryCancel(shopOrder.getScmShopOrderCode(), afterSaleOrderDetailDO.getSkuCode());
		boolean flg=(boolean) map.get("flg");
		if(!flg) {
			//取消失败
			afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_IS_FAIL.getCode());
			resultmap.put("msg", map.get("msg"));
		}else {
			afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_IS_CANCELING.getCode());
		}

        afterSaleOrderService.insert(afterSaleOrder);
        //日志
        logInfoService.recordLog(new AfterSaleOrder(), afterSaleOrder.getId(),
                "admin", "创建", "", null);


		resultmap.put("res", flg);
		resultmap.put("afterSaleCode", afterSaleCode);
		return resultmap;
	}

	private boolean isExistRequestNo(String requestNo) {
		Example example = new Example(AfterSaleOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("requestNo", requestNo);
        int count=afterSaleOrderService.selectCountByExample(example);
        if(count>0) {
        	return true;
        }
		return false;
	}

	private String ReturnGoods(TairanAfterSaleOrderDO afterSaleOrderDO,ShopOrder shopOrder,int returnScene) {
		
		String shopOrderCode=afterSaleOrderDO.getShopOrderCode();
		
		List<TaiRanAfterSaleOrderDetail> details=afterSaleOrderDO.getAfterSaleOrderDetailList();
		
		
		PlatformOrder platformOrderSelect=new PlatformOrder();
		platformOrderSelect.setPlatformOrderCode(shopOrder.getPlatformOrderCode());
		//platformOrderSelect.setChannelCode(shopOrder.getChannelCode());
		PlatformOrder platformOrder=platformOrderService.selectOne(platformOrderSelect);
		AssertUtil.notNull(platformOrder, "根据该平台订单编码"+shopOrder.getPlatformOrderCode()+"查询到的平台订单信息为空!");
		
		WarehouseInfo selectWarehouse=new WarehouseInfo();
		AssertUtil.notBlank(afterSaleOrderDO.getReturnWarehouseCode(), "仓库编号为空!");
		selectWarehouse.setCode(afterSaleOrderDO.getReturnWarehouseCode());
		WarehouseInfo warehouseInfo=warehouseInfoService.selectOne(selectWarehouse);
		AssertUtil.notNull(warehouseInfo,"该仓库编号"+afterSaleOrderDO.getReturnWarehouseCode()+"查询到的仓库为空!");

		//查询该订单创建售后单的数量
		int afterSaleNum=getCount(shopOrder);
		//售后单编号
		String afterSaleCode =SupplyConstants.Serial.AFTER_SALE_CODE+"-"+shopOrder.getScmShopOrderCode()+"-"+(afterSaleNum+1);
        			
		String warehouseNoticeCode = serialUtilService.generateCode(SupplyConstants.Serial.WAREHOUSE_NOTICE_LENGTH,
        		SupplyConstants.Serial.WAREHOUSE_NOTICE_CODE,
        			DateUtils.dateToCompactString(Calendar.getInstance().getTime()));
		//售后单
		AfterSaleOrder afterSaleOrder=getAfterSaleOrder(afterSaleCode,shopOrder,afterSaleOrderDO,platformOrder,warehouseInfo,returnScene);
		
		//退货入库单
		AfterSaleWarehouseNotice afterSaleWarehouseNotice=getAfterSaleWarehouseNotice(afterSaleCode,warehouseNoticeCode,shopOrder,
				afterSaleOrderDO,platformOrder,warehouseInfo,returnScene);
		

		for(TaiRanAfterSaleOrderDetail afterSaleOrderDetailDO:details) {

			OrderItem orderItemSelect=new OrderItem();
			orderItemSelect.setShopOrderCode(shopOrderCode);
			AssertUtil.notBlank(afterSaleOrderDetailDO.getSkuCode(), "子订单的sku不能为空!");
			orderItemSelect.setSkuCode(afterSaleOrderDetailDO.getSkuCode());
			OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
			AssertUtil.notNull(orderItem, "根据订单号"+shopOrderCode+"和sku:"+afterSaleOrderDetailDO.getSkuCode()+"查询子订单为空!");
			//售后单子单
			getAfterSaleOrderDetail(orderItem,afterSaleOrderDetailDO,afterSaleCode);
			//退货入库单子单
			getAfterSaleWarehouseNoticeDetail(orderItem,warehouseNoticeCode,afterSaleOrderDetailDO);
		}

		afterSaleOrderService.insert(afterSaleOrder);
		afterSaleWarehouseNoticeService.insert(afterSaleWarehouseNotice);
		//通知wms，新增退货入库单
		ScmReturnOrderCreateRequest returnOrderCreateRequest=getReturnInOrder(afterSaleCode,warehouseNoticeCode,shopOrder,afterSaleOrderDO,platformOrder,warehouseInfo,returnScene);
		AppResult<ScmReturnOrderCreateResponse> response=warehouseApiService.returnOrderCreate(returnOrderCreateRequest);
		if(!StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
			AssertUtil.notNull(null, response.getDatabuffer());
		}
		
		//日志
		logInfoService.recordLog(new AfterSaleOrder(), afterSaleOrder.getId(), 
				"admin", "创建", "", null);
		
		return afterSaleCode;
	}
	
	private ScmReturnOrderCreateRequest getReturnInOrder(String afterSaleCode, String warehouseNoticeCode,
			ShopOrder shopOrder, TairanAfterSaleOrderDO afterSaleOrderDO, PlatformOrder platformOrder,
			WarehouseInfo warehouseInfo,int returnScene) {
		ScmReturnOrderCreateRequest returnOrderCreateRequest=new ScmReturnOrderCreateRequest();
		returnOrderCreateRequest.setWarehouseType(WarehouseTypeEnum.Zy.getCode());
		returnOrderCreateRequest.setAfterSaleCode(afterSaleCode);
		returnOrderCreateRequest.setWarehouseNoticeCode(warehouseNoticeCode);
		returnOrderCreateRequest.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		returnOrderCreateRequest.setShopOrderCode(shopOrder.getShopOrderCode());
		returnOrderCreateRequest.setWarehouseCode(afterSaleOrderDO.getReturnWarehouseCode());
		returnOrderCreateRequest.setSender(platformOrder.getReceiverName());
		returnOrderCreateRequest.setSenderAddress(platformOrder.getReceiverAddress());
		returnOrderCreateRequest.setSenderCity(platformOrder.getReceiverCity());
		returnOrderCreateRequest.setSenderNumber(platformOrder.getReceiverMobile());
		returnOrderCreateRequest.setSenderProvince(platformOrder.getReceiverProvince());
		returnOrderCreateRequest.setReceiver(warehouseInfo.getWarehouseContact());
		returnOrderCreateRequest.setReceiverAddress(warehouseInfo.getAddress());
		returnOrderCreateRequest.setReceiverCity(warehouseInfo.getCity());
		returnOrderCreateRequest.setReceiverNumber(warehouseInfo.getWarehouseContactNumber());
		returnOrderCreateRequest.setReceiverProvince(warehouseInfo.getProvince());
		returnOrderCreateRequest.setSkuNum(afterSaleOrderDO.getAfterSaleOrderDetailList().size());
		returnOrderCreateRequest.setOperator("系统");
//		returnOrderCreateRequest.setRemark(afterSaleOrderAddDO.getMemo());
		returnOrderCreateRequest.setChannelCode(shopOrder.getChannelCode());
		returnOrderCreateRequest.setSellCode(shopOrder.getSellCode());
		returnOrderCreateRequest.setShopId(shopOrder.getShopId());
		returnOrderCreateRequest.setShopName(shopOrder.getShopName());
		returnOrderCreateRequest.setLogisticsCorporation(afterSaleOrderDO.getLogisticsCorporation());
		returnOrderCreateRequest.setLogisticsCorporationCode(afterSaleOrderDO.getLogisticsCorporationCode());
		returnOrderCreateRequest.setWaybillNumber(afterSaleOrderDO.getWaybillNumber());
		returnOrderCreateRequest.setReturnScene(returnScene);
		
		List<ScmReturnInOrderDetail> list=new ArrayList<>();
		for(TaiRanAfterSaleOrderDetail afterSaleOrderDetailDO:afterSaleOrderDO.getAfterSaleOrderDetailList()) {
			OrderItem orderItemSelect=new OrderItem();
			orderItemSelect.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
			orderItemSelect.setSkuCode(afterSaleOrderDetailDO.getSkuCode());
			OrderItem orderItem=orderItemService.selectOne(orderItemSelect);
			
			ScmReturnInOrderDetail detail=new ScmReturnInOrderDetail();
			detail.setWarehouseNoticeCode(warehouseNoticeCode);
			detail.setShopOrderCode(shopOrder.getShopOrderCode());
			detail.setOrderItemCode(orderItem.getOrderItemCode());
			detail.setSkuCode(orderItem.getSkuCode());
			detail.setSkuName(orderItem.getItemName());
			detail.setSpecNatureInfo(orderItem.getSpecNatureInfo());
			detail.setBarCode(orderItem.getBarCode());
			detail.setBrandName(getBrandName(orderItem.getSpuCode()));
			detail.setPicture(orderItem.getPicPath());
			detail.setReturnNum(afterSaleOrderDetailDO.getReturnNum());
			detail.setMaxReturnNum(getMaxReturnNum(orderItem));
			list.add(detail);
		}
		
		returnOrderCreateRequest.setReturnInOrderDetailList(list);
		return returnOrderCreateRequest;
	}

	private void getAfterSaleWarehouseNoticeDetail(OrderItem orderItem, String warehouseNoticeCode,
			TaiRanAfterSaleOrderDetail afterSaleOrderDetailDO) {
		AfterSaleWarehouseNoticeDetail afterSaleWarehouseNoticeDetail=new AfterSaleWarehouseNoticeDetail();
		String afterSaleWarehouseNoticeDetailId=GuidUtil.getNextUid(AFTER_SALE_WAREHOUSE_NOTICE_DETAIL_ID);
		afterSaleWarehouseNoticeDetail.setId(afterSaleWarehouseNoticeDetailId);
		afterSaleWarehouseNoticeDetail.setWarehouseNoticeCode(warehouseNoticeCode);
		afterSaleWarehouseNoticeDetail.setShopOrderCode(orderItem.getShopOrderCode());
		afterSaleWarehouseNoticeDetail.setOrderItemCode(orderItem.getOrderItemCode());
		afterSaleWarehouseNoticeDetail.setSpuCode(orderItem.getSpuCode());
		afterSaleWarehouseNoticeDetail.setSkuCode(orderItem.getSkuCode());
		afterSaleWarehouseNoticeDetail.setSkuName(orderItem.getItemName());
		afterSaleWarehouseNoticeDetail.setBarCode(orderItem.getBarCode());
		afterSaleWarehouseNoticeDetail.setSpecNatureInfo(orderItem.getSpecNatureInfo());
		afterSaleWarehouseNoticeDetail.setPicture(orderItem.getPicPath());
		afterSaleWarehouseNoticeDetail.setCreateTime(new Date());
		afterSaleWarehouseNoticeDetail.setUpdateTime(new Date());
		afterSaleWarehouseNoticeDetail.setBrandName(getBrandName(orderItem.getSpuCode()));
		afterSaleWarehouseNoticeDetail.setReturnNum(afterSaleOrderDetailDO.getReturnNum());
		afterSaleWarehouseNoticeDetailService.insert(afterSaleWarehouseNoticeDetail);
		
	}

	private void getAfterSaleOrderDetail(OrderItem orderItem, TaiRanAfterSaleOrderDetail afterSaleOrderDetailDO,
			String afterSaleCode) {
		AfterSaleOrderDetail afterSaleOrderDetail=new AfterSaleOrderDetail();
		String afterSaleOrderDetailId=GuidUtil.getNextUid(AFTER_SALE_ORDER_DETAIL_ID);
		afterSaleOrderDetail.setId(afterSaleOrderDetailId);
		afterSaleOrderDetail.setAfterSaleCode(afterSaleCode);
		afterSaleOrderDetail.setShopOrderCode(orderItem.getShopOrderCode());
		afterSaleOrderDetail.setScmShopOrderCode(orderItem.getScmShopOrderCode());
		afterSaleOrderDetail.setBrandName(getBrandName(orderItem.getSpuCode()));
		afterSaleOrderDetail.setOrderItemCode(orderItem.getOrderItemCode());
		afterSaleOrderDetail.setSkuCode(orderItem.getSkuCode());
		afterSaleOrderDetail.setSpuCode(orderItem.getSpuCode());
		afterSaleOrderDetail.setSkuName(orderItem.getItemName());
		afterSaleOrderDetail.setBarCode(orderItem.getBarCode());
		afterSaleOrderDetail.setSpecNatureInfo(orderItem.getSpecNatureInfo());
		afterSaleOrderDetail.setNum(orderItem.getNum());
		afterSaleOrderDetail.setMaxReturnNum(getMaxReturnNum(orderItem));
		afterSaleOrderDetail.setReturnNum(afterSaleOrderDetailDO.getReturnNum());
		afterSaleOrderDetail.setRefundAmont(afterSaleOrderDetailDO.getRefundAmont());
		afterSaleOrderDetail.setInNum(afterSaleOrderDetailDO.getInNum());
		afterSaleOrderDetail.setDefectiveInNum(afterSaleOrderDetailDO.getDefectiveInNum());
		afterSaleOrderDetail.setPicture(orderItem.getPicPath());
		WarehouseOrder warehouseOrder=getWarehouseOrder(orderItem.getWarehouseOrderCode());
		afterSaleOrderDetail.setDeliverWarehouseCode(warehouseOrder.getWarehouseCode());
		afterSaleOrderDetail.setDeliverWarehouseName(warehouseOrder.getWarehouseName());
		afterSaleOrderDetail.setCreateTime(new Date());
		afterSaleOrderDetail.setUpdateTime(new Date());
		afterSaleOrderDetailService.insert(afterSaleOrderDetail);
		
	}

	private Integer getMaxReturnNum(OrderItem orderItem) {
		
		//根据系统订单号查询发货单号
		OutboundOrder selectOutboundOrder=new OutboundOrder();
		selectOutboundOrder.setScmShopOrderCode(orderItem.getScmShopOrderCode());
		List<OutboundOrder> outboundOrderList=outBoundOrderService.select(selectOutboundOrder);
		AssertUtil.notNull(outboundOrderList, "没有该订单的发货单!");
		
		List<OutboundDetail> list=getOutboundDetailList(outboundOrderList);
		AssertUtil.notNull(list, "没有该订单的发货单详情!");
		//实际发货的数量-退货数量
		int realSendNum=(int) getRealSendNum(list,orderItem.getSkuCode());
		//全部发货、部分发货的SKU   可退货数量=正向订单出库数量-已退货入库数量    其他状态可退都为0
		if(orderItem.getSupplierOrderStatus().equals(OrderItemDeliverStatusEnum.PARTS_DELIVER.getCode())  ||
				orderItem.getSupplierOrderStatus().equals(OrderItemDeliverStatusEnum.ALL_DELIVER.getCode())) {
			//退货数量
			int refundNum=getAlreadyRefundNum(orderItem);
			return (realSendNum-refundNum);
			
		}
		return 0;
	}
	
	private List<OutboundDetail> getOutboundDetailList(List<OutboundOrder> outboundOrderList) {
		List<String> outboundOrderCodes=new ArrayList<>();
		for(OutboundOrder outboundOrder:outboundOrderList) {
			outboundOrderCodes.add(outboundOrder.getOutboundOrderCode());
		}
		//根据条件分页查询
		Example example = new Example(OutboundDetail.class);
		Example.Criteria criteria = example.createCriteria();
		
		criteria.andIn("outboundOrderCode", outboundOrderCodes);
		return outboundDetailService.selectByExample(example);
	}

	private long getRealSendNum(List<OutboundDetail> list, String skuCode) {
		for(OutboundDetail outboundDetail: list) {
			if(outboundDetail.getSkuCode().equals(skuCode)) {
				return outboundDetail.getRealSentItemNum()==null?0:outboundDetail.getRealSentItemNum();
			}
		}
		return 0;
	}

	/**
	 * 订单退货数量
	 * @param orderItem
	 * @return
	 */
	private int getAlreadyRefundNum(OrderItem orderItem) {
		AfterSaleOrderDetail afterSaleOrderDetailSelect=new AfterSaleOrderDetail();
		afterSaleOrderDetailSelect.setShopOrderCode(orderItem.getShopOrderCode());
		afterSaleOrderDetailSelect.setSkuCode(orderItem.getSkuCode());

		int num=0;
		List<AfterSaleOrderDetail> afterSaleOrderDetailsList=afterSaleOrderDetailService.select(afterSaleOrderDetailSelect);
		if(afterSaleOrderDetailsList!=null) {
			for(AfterSaleOrderDetail afterSaleOrderDetail:afterSaleOrderDetailsList) {
				int inNum=afterSaleOrderDetail.getInNum()==null?0:afterSaleOrderDetail.getInNum();
				int defectiveInNum=afterSaleOrderDetail.getDefectiveInNum()==null?0:afterSaleOrderDetail.getDefectiveInNum();;
				num=num+inNum+defectiveInNum;
			}

		}
		return num;
	}

	private WarehouseOrder getWarehouseOrder(String warehouseOrderCode) {
		WarehouseOrder select=new WarehouseOrder();
		select.setWarehouseOrderCode(warehouseOrderCode);
		return warehouseOrderService.selectOne(select);
	}

	private String getBrandName(String spuCode) {
		Items selectItems=new Items();
		selectItems.setSpuCode(spuCode);
		Items items=itemsService.selectOne(selectItems);
		
		Brand selectBrand=new Brand();
		selectBrand.setId(items.getBrandId());
		return brandService.selectOne(selectBrand).getName();
	}

	private AfterSaleWarehouseNotice getAfterSaleWarehouseNotice(String afterSaleCode, String warehouseNoticeCode,
			ShopOrder shopOrder, TairanAfterSaleOrderDO afterSaleOrderDO, PlatformOrder platformOrder,
			WarehouseInfo warehouseInfo,int returnScene) {
		
		AfterSaleWarehouseNotice afterSaleWarehouseNotice=new AfterSaleWarehouseNotice();
		String afterSaleWarehouseNoticeId=GuidUtil.getNextUid(AFTER_SALE_WAREHOUSE_NOTICE_ID);
		afterSaleWarehouseNotice.setId(afterSaleWarehouseNoticeId);
		afterSaleWarehouseNotice.setWarehouseNoticeCode(warehouseNoticeCode);
		afterSaleWarehouseNotice.setAfterSaleCode(afterSaleCode);
		afterSaleWarehouseNotice.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		afterSaleWarehouseNotice.setShopOrderCode(shopOrder.getShopOrderCode());
		afterSaleWarehouseNotice.setChannelCode(shopOrder.getChannelCode());
		afterSaleWarehouseNotice.setSellCode(shopOrder.getSellCode());
		afterSaleWarehouseNotice.setShopName(shopOrder.getShopName());
		afterSaleWarehouseNotice.setShopId(shopOrder.getShopId());
		afterSaleWarehouseNotice.setWarehouseName(warehouseInfo.getWarehouseName());
		afterSaleWarehouseNotice.setWarehouseCode(warehouseInfo.getCode());
		afterSaleWarehouseNotice.setSender(platformOrder.getReceiverName());
		afterSaleWarehouseNotice.setSenderAddress(platformOrder.getReceiverAddress());
		afterSaleWarehouseNotice.setSenderCity(platformOrder.getReceiverCity());
		afterSaleWarehouseNotice.setSenderNumber(platformOrder.getReceiverMobile());
		afterSaleWarehouseNotice.setSenderProvince(platformOrder.getReceiverProvince());
		afterSaleWarehouseNotice.setReceiverNumber(warehouseInfo.getWarehouseContactNumber());
		afterSaleWarehouseNotice.setReceiver(warehouseInfo.getWarehouseContact());
		afterSaleWarehouseNotice.setReceiverProvince(warehouseInfo.getProvince());
		afterSaleWarehouseNotice.setReceiverAddress(warehouseInfo.getAddress());
		afterSaleWarehouseNotice.setReceiverCity(warehouseInfo.getCity());
		afterSaleWarehouseNotice.setSkuNum(afterSaleOrderDO.getAfterSaleOrderDetailList().size());
		afterSaleWarehouseNotice.setOperator("系统");
		afterSaleWarehouseNotice.setRemark(afterSaleOrderDO.getMemo());
		//afterSaleWarehouseNotice.setCreateOperator(aclUserAccreditInfo.getUserId());
		afterSaleWarehouseNotice.setCreateTime(new Date());
		afterSaleWarehouseNotice.setLogisticsCorporation(afterSaleOrderDO.getLogisticsCorporation());
		afterSaleWarehouseNotice.setLogisticsCorporationCode(afterSaleOrderDO.getLogisticsCorporationCode());
		afterSaleWarehouseNotice.setWaybillNumber(afterSaleOrderDO.getWaybillNumber());
		//线下退货
		if(returnScene==returnSceneEnum.STATUS_0.getCode()) {
			afterSaleWarehouseNotice.setReturnScene(returnSceneEnum.STATUS_0.getCode());
			afterSaleWarehouseNotice.setStatus(AfterSaleWarehouseNoticeStatusEnum.STATUS_2.getCode());
		}else {
			afterSaleWarehouseNotice.setReturnScene(returnSceneEnum.STATUS_1.getCode());
			afterSaleWarehouseNotice.setStatus(AfterSaleWarehouseNoticeStatusEnum.STATUS_0.getCode());
			
		}
		return afterSaleWarehouseNotice;
	}

	private AfterSaleOrder getAfterSaleOrder(String afterSaleCode, ShopOrder shopOrder,TairanAfterSaleOrderDO afterSaleOrderDO
			,PlatformOrder platformOrder,WarehouseInfo warehouseInfo,int returnScene) {
		
		
		AfterSaleOrder afterSaleOrder=new AfterSaleOrder();
		String afterSaleOrderId=GuidUtil.getNextUid(AFTER_SALE_ORDER_ID);
		afterSaleOrder.setId(afterSaleOrderId);
		afterSaleOrder.setAfterSaleCode(afterSaleCode);
		afterSaleOrder.setShopOrderCode(shopOrder.getShopOrderCode());
		afterSaleOrder.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		afterSaleOrder.setChannelCode(shopOrder.getChannelCode());
		afterSaleOrder.setSellCode(shopOrder.getSellCode());
		afterSaleOrder.setPicture(afterSaleOrderDO.getPicture());
		afterSaleOrder.setShopId(shopOrder.getShopId());
		afterSaleOrder.setShopName(shopOrder.getShopName());
		afterSaleOrder.setSender(platformOrder.getReceiverName());
		afterSaleOrder.setSenderAddress(platformOrder.getReceiverAddress());
		afterSaleOrder.setSenderCity(platformOrder.getReceiverCity());
		afterSaleOrder.setSenderNumber(platformOrder.getReceiverMobile());
		afterSaleOrder.setSenderProvince(platformOrder.getReceiverProvince());
		afterSaleOrder.setUserId(platformOrder.getUserId());
		afterSaleOrder.setUserName(platformOrder.getUserName());
		afterSaleOrder.setReceiverProvince(warehouseInfo.getProvince());
		afterSaleOrder.setReceiverCity(warehouseInfo.getCity());
		afterSaleOrder.setReceiverDistrict(warehouseInfo.getArea());
		afterSaleOrder.setReceiverAddress(warehouseInfo.getAddress());
		afterSaleOrder.setReceiverName(warehouseInfo.getWarehouseContact());
		afterSaleOrder.setReceiverPhone(warehouseInfo.getWarehouseContactNumber());
		afterSaleOrder.setReceiverMobile(warehouseInfo.getWarehouseContactNumber());
		afterSaleOrder.setPayTime(shopOrder.getPayTime());
		afterSaleOrder.setReturnWarehouseCode(warehouseInfo.getCode());
		afterSaleOrder.setReturnAddress(warehouseInfo.getAddress());
		afterSaleOrder.setReturnWarehouseName(warehouseInfo.getWarehouseName());
		afterSaleOrder.setMemo(afterSaleOrderDO.getMemo());
		afterSaleOrder.setLogisticsCorporationCode(afterSaleOrderDO.getLogisticsCorporationCode());
		afterSaleOrder.setLogisticsCorporation(afterSaleOrderDO.getLogisticsCorporation());
		afterSaleOrder.setWaybillNumber(afterSaleOrderDO.getWaybillNumber());
		afterSaleOrder.setAfterSaleType(AfterSaleTypeEnum.RETURN_GOODS.getCode());
		afterSaleOrder.setLaunchType(launchTypeEnum.STATUS_0.getCode());
		afterSaleOrder.setAfterSaleType(AfterSaleTypeEnum.RETURN_GOODS.getCode());
		afterSaleOrder.setCreateTime(new Date());
		afterSaleOrder.setUpdateTime(new Date());
		//实体店退货
		if(returnScene==returnSceneEnum.STATUS_0.getCode()) {
			afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_2.getCode());
			afterSaleOrder.setReturnScene(returnSceneEnum.STATUS_0.getCode());
		}else{
			afterSaleOrder.setReturnScene(returnSceneEnum.STATUS_1.getCode());
			if(StringUtils.isNotBlank(afterSaleOrderDO.getLogisticsCorporationCode()) && StringUtils.isNotBlank(afterSaleOrderDO.getWaybillNumber())) {
				afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_1.getCode());
			}else {
				afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_0.getCode());
			}
		}
		return afterSaleOrder;
	}
	
	
	private AfterSaleOrder getAfterSaleOrderCancel(String afterSaleCode, ShopOrder shopOrder,TairanAfterSaleOrderDO afterSaleOrderDO
			,PlatformOrder platformOrder,WarehouseInfo warehouseInfo,int returnScene) {
		
		
		AfterSaleOrder afterSaleOrder=new AfterSaleOrder();
		String afterSaleOrderId=GuidUtil.getNextUid(AFTER_SALE_ORDER_ID);
		afterSaleOrder.setId(afterSaleOrderId);
		afterSaleOrder.setAfterSaleCode(afterSaleCode);
		afterSaleOrder.setShopOrderCode(shopOrder.getShopOrderCode());
		afterSaleOrder.setScmShopOrderCode(shopOrder.getScmShopOrderCode());
		afterSaleOrder.setChannelCode(shopOrder.getChannelCode());
		afterSaleOrder.setSellCode(shopOrder.getSellCode());
		afterSaleOrder.setPicture(afterSaleOrderDO.getPicture());
		afterSaleOrder.setShopId(shopOrder.getShopId());
		afterSaleOrder.setShopName(shopOrder.getShopName());
		afterSaleOrder.setSender(platformOrder.getReceiverName());
		afterSaleOrder.setSenderAddress(platformOrder.getReceiverAddress());
		afterSaleOrder.setSenderCity(platformOrder.getReceiverCity());
		afterSaleOrder.setSenderNumber(platformOrder.getReceiverMobile());
		afterSaleOrder.setSenderProvince(platformOrder.getReceiverProvince());
		afterSaleOrder.setUserId(platformOrder.getUserId());
		afterSaleOrder.setUserName(platformOrder.getUserName());
		afterSaleOrder.setReceiverProvince(warehouseInfo.getProvince());
		afterSaleOrder.setReceiverCity(warehouseInfo.getCity());
		afterSaleOrder.setReceiverDistrict(warehouseInfo.getArea());
		afterSaleOrder.setReceiverAddress(warehouseInfo.getAddress());
		afterSaleOrder.setReceiverName(warehouseInfo.getWarehouseContact());
		afterSaleOrder.setReceiverPhone(warehouseInfo.getWarehouseContactNumber());
		afterSaleOrder.setReceiverMobile(warehouseInfo.getWarehouseContactNumber());
		afterSaleOrder.setPayTime(shopOrder.getPayTime());
		afterSaleOrder.setReturnWarehouseCode(warehouseInfo.getCode());
		afterSaleOrder.setReturnAddress(warehouseInfo.getAddress());
		afterSaleOrder.setReturnWarehouseName(warehouseInfo.getWarehouseName());
		afterSaleOrder.setMemo(afterSaleOrderDO.getMemo());
//		afterSaleOrder.setLogisticsCorporationCode(afterSaleOrderDO.getLogisticsCorporationCode());
//		afterSaleOrder.setLogisticsCorporation(afterSaleOrderDO.getLogisticsCorporation());
//		afterSaleOrder.setWaybillNumber(afterSaleOrderDO.getWaybillNumber());
		afterSaleOrder.setAfterSaleType(AfterSaleTypeEnum.CANCEL_DELIVER.getCode());
		afterSaleOrder.setLaunchType(launchTypeEnum.STATUS_0.getCode());
		afterSaleOrder.setCreateTime(new Date());
		afterSaleOrder.setUpdateTime(new Date());
		afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_0.getCode());
		return afterSaleOrder;
	}
	
	private int getCount(ShopOrder shopOrder) {
		AfterSaleOrder select=new AfterSaleOrder();
		select.setShopOrderCode(shopOrder.getShopOrderCode());
		List<AfterSaleOrder> list=afterSaleOrderService.select(select);
		if(list!=null ) {
			return list.size();
		}
		return 0;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> cancelAfterSaleOrder(String afterSaleCode) {
		Map<String, Object> data=new HashMap<>();
		data.put("afterSaleCode", afterSaleCode);
		//返回泰然城   取消状态结果:0-取消失败,1取消成功
		data.put("afterSaleOrderState", Integer.parseInt(ZeroToNineEnum.ZERO.getCode()));

		AssertUtil.notBlank(afterSaleCode, "售后单号不能为空!");
		AfterSaleOrder select=new AfterSaleOrder();
		select.setAfterSaleCode(afterSaleCode);
		AfterSaleOrder afterSaleOrder=afterSaleOrderService.selectOne(select);
		AssertUtil.notNull(afterSaleOrder, "根据售后单号"+afterSaleOrder+"查询到的售后单为空!");
		if(!(afterSaleOrder.getStatus()==AfterSaleOrderStatusEnum.STATUS_0.getCode())) {
			AssertUtil.notNull(null,"只有待客户发货状态才能取消!");
		}

		AfterSaleWarehouseNotice selectWarehouseNotice=new AfterSaleWarehouseNotice();
		selectWarehouseNotice.setAfterSaleCode(afterSaleCode);
		AfterSaleWarehouseNotice afterSaleWarehouseNotice=afterSaleWarehouseNoticeService.selectOne(selectWarehouseNotice);
		AssertUtil.notNull(afterSaleWarehouseNotice,"根据售后单号"+afterSaleCode+"查询退货入库单为空!");

		//调用子系统接口 取消售后单  //取消成功
		ScmCancelAfterSaleOrderRequest scmCancelAfterSaleOrderRequest=new ScmCancelAfterSaleOrderRequest();
		scmCancelAfterSaleOrderRequest.setAfterSaleCode(afterSaleCode);
        scmCancelAfterSaleOrderRequest.setWarehouseType(WarehouseTypeEnum.Zy.getCode());

		AppResult<ScmCancelAfterSaleOrderResponse> response=warehouseApiService.returnInOrderCancel(scmCancelAfterSaleOrderRequest);
		if(StringUtils.equals(response.getAppcode(), ResponseAck.SUCCESS_CODE)) {
			//是否取消成功: 1-取消成功, 2-取消失败
			ScmCancelAfterSaleOrderResponse scmCancelAfterSaleOrderResponse=(ScmCancelAfterSaleOrderResponse) response.getResult();
			String flag=scmCancelAfterSaleOrderResponse.getFlag();
			if(ZeroToNineEnum.ONE.getCode().equals(flag)) {
				afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_3.getCode());
				afterSaleWarehouseNotice.setStatus(AfterSaleWarehouseNoticeStatusEnum.STATUS_3.getCode());
				afterSaleOrderService.updateByPrimaryKey(afterSaleOrder);
				afterSaleWarehouseNoticeService.updateByPrimaryKey(afterSaleWarehouseNotice);
				
				data.put("afterSaleOrderState", Integer.parseInt(ZeroToNineEnum.ONE.getCode()));
			}
            //日志
            logInfoService.recordLog(new AfterSaleOrder(), afterSaleOrder.getId(),
                    "admin", scmCancelAfterSaleOrderResponse.getMessage(), "", null);

		}
		return data;
	}

	

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void submitWaybill(AfterSaleWaybillForm afterSaleWaybillForm) throws Exception {
	    AssertUtil.notNull(afterSaleWaybillForm,"提交的物流信息为空!");
	    AssertUtil.notBlank(afterSaleWaybillForm.getLogisticsCorporationCode(),"物流公司编码不能为空!");
	    AssertUtil.notBlank(afterSaleWaybillForm.getLogisticsCorporation(),"物流公司名称不能为空!");
	    AssertUtil.notBlank(afterSaleWaybillForm.getWaybillNumber(),"物流单号不能为空!");

        AfterSaleOrder afterSaleOrder = new AfterSaleOrder();
        afterSaleOrder.setAfterSaleCode(afterSaleWaybillForm.getAfterSaleCode());
        //售后单类型为退货的
        afterSaleOrder.setAfterSaleType(AfterSaleTypeEnum.RETURN_GOODS.getCode());
        afterSaleOrder = afterSaleOrderService.selectOne(afterSaleOrder);
        AssertUtil.notNull(afterSaleOrder,"根据售后单号:"+afterSaleWaybillForm.getAfterSaleCode()+"查询售后单信息为空!");
        //更新售后单
        afterSaleOrder.setLogisticsCorporationCode(afterSaleWaybillForm.getLogisticsCorporationCode());
        afterSaleOrder.setLogisticsCorporation(afterSaleWaybillForm.getLogisticsCorporation());
        afterSaleOrder.setWaybillNumber(afterSaleWaybillForm.getWaybillNumber());
        //修改状态
        afterSaleOrder.setStatus(AfterSaleOrderStatusEnum.STATUS_1.getCode());

        int count =  afterSaleOrderService.updateByPrimaryKeySelective(afterSaleOrder);

        if(count == 0){
            String msg = CommonUtil.joinStr("修改售后单信息",JSON.toJSONString(afterSaleOrder),"数据库操作失败").toString();
            logger.error(msg);
            throw new AfterSaleException(ExceptionEnum.AFTER_SALE_ORDER_UPDATE_EXCEPTION, msg);
        }
        //修改退货入库单
        AfterSaleWarehouseNotice selectWarehouseNotice=new AfterSaleWarehouseNotice();
        selectWarehouseNotice.setAfterSaleCode(afterSaleOrder.getAfterSaleCode());
        AfterSaleWarehouseNotice afterSaleWarehouseNotice=afterSaleWarehouseNoticeService.selectOne(selectWarehouseNotice);
        AssertUtil.notNull(afterSaleWarehouseNotice,"根据售后单号"+afterSaleOrder.getAfterSaleCode()+"查询退货入库单为空!");
        afterSaleWarehouseNotice.setWaybillNumber(afterSaleOrder.getWaybillNumber());
        afterSaleWarehouseNotice.setLogisticsCorporationCode(afterSaleWaybillForm.getLogisticsCorporationCode());
        afterSaleWarehouseNotice.setLogisticsCorporation(afterSaleWaybillForm.getLogisticsCorporation());
        int count2 =  afterSaleWarehouseNoticeService.updateByPrimaryKeySelective(afterSaleWarehouseNotice);

        if(count2 == 0){
            String msg = CommonUtil.joinStr("修改退货入库单信息",JSON.toJSONString(afterSaleWarehouseNotice),"数据库操作失败").toString();
            logger.error(msg);
            throw new AfterSaleWarehousesNoticeException(ExceptionEnum.SYSTEM_EXCEPTION, msg);
        }
        //通知自营仓
        ScmSubmitAfterSaleOrderLogisticsRequest logisticsRequest  = new ScmSubmitAfterSaleOrderLogisticsRequest();
        logisticsRequest.setAfterSaleCode(afterSaleOrder.getAfterSaleCode());
        logisticsRequest.setWarehouseType(WarehouseTypeEnum.Zy.getCode());
        logisticsRequest.setLogisticsCorporationCode(afterSaleOrder.getLogisticsCorporationCode());
        logisticsRequest.setLogisticsCorporation(afterSaleOrder.getLogisticsCorporation());
        logisticsRequest.setWaybillNumber(afterSaleOrder.getWaybillNumber());
        AppResult appResult = warehouseApiService.submitAfterSaleLogistics(logisticsRequest);
        if (!StringUtils.equals(appResult.getAppcode(),ResponseAck.SUCCESS_CODE)){
           throw new AfterSaleException(ExceptionEnum.SYSTEM_EXCEPTION,appResult.getDatabuffer());
        }else {
            //记录日志
            logInfoService.recordLog(afterSaleOrder,afterSaleOrder.getId(),"admin",LogOperationEnum.UPDATE.getMessage(),"接收物流单号","");
        }
    }



}
