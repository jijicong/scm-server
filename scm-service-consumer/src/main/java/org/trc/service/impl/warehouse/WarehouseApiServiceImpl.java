package org.trc.service.impl.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trc.enums.*;
import org.trc.form.JDModel.ExternalSupplierConfig;
import org.trc.form.warehouse.*;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service("warehouseApiService")
public class WarehouseApiServiceImpl implements IWarehouseApiService {

    private final static Logger log = LoggerFactory.getLogger(WarehouseApiServiceImpl.class);

    @Value("${mock.outer.interface}")
    private String mockOuterInterface;

    //仓库商品ID编码前缀
    private static final String ITEM_ID_MOCK_PREFIX = "ITEM-";
    //仓库入库单编码前缀
    private static final String ENTRY_ORDER_CODE_MOCK_PREFIX = "ENTRY-";
    //仓库发货单编码前缀
    private static final String DELIVERY_ORDER_CODE_MOCK_PREFIX = "DEVER-";

    @Autowired
    private ExternalSupplierConfig externalSupplierConfig;

    @Override
    public AppResult<List<ScmItemSyncResponse>> itemSync(ScmItemSyncRequest scmItemSyncRequest) {
        return wmsInvoke(scmItemSyncRequest);
    }

    @Override
    public AppResult<List<ScmInventoryQueryResponse>> inventoryQuery(ScmInventoryQueryRequest inventoryQueryRequest) {
        return wmsInvoke(inventoryQueryRequest);
    }

    @Override
    public AppResult<String> entryOrderCreate(ScmEntryOrderCreateRequest entryOrderCreateRequest) {
        return wmsInvoke(entryOrderCreateRequest);
    }

    @Override
    public AppResult<List<ScmDeliveryOrderCreateResponse>> deliveryOrderCreate(ScmDeliveryOrderCreateRequest deliveryOrderCreateRequest) {
        return wmsInvoke(deliveryOrderCreateRequest);
    }

    @Override
    public AppResult<ScmReturnOrderCreateResponse> returnOrderCreate(ScmReturnOrderCreateRequest returnOrderCreateRequest) {
        return wmsInvoke(returnOrderCreateRequest);
    }

    @Override
    public AppResult<ScmOrderCancelResponse> orderCancel(ScmOrderCancelRequest orderCancelRequest) {
        return wmsInvoke(orderCancelRequest);
    }

    @Override
    public AppResult<List<ScmEntryOrderDetailResponse>> entryOrderDetail(ScmEntryOrderDetailRequest entryOrderDetailRequest) {
        return wmsInvoke(entryOrderDetailRequest);
    }

    @Override
    public AppResult<ScmDeliveryOrderDetailResponse> deliveryOrderDetail(ScmDeliveryOrderDetailRequest deliveryOrderDetailRequest) {
        return wmsInvoke(deliveryOrderDetailRequest);
    }

    @Override
    public AppResult<ScmOrderPacksResponse> orderPack(ScmOrderPacksRequest orderPacksRequest) {
        return wmsInvoke(orderPacksRequest);
    }


    private AppResult wmsInvoke(ScmWarehouseRequestBase scmWarehouseRequestBase){
        if(StringUtils.equals(mockOuterInterface, ZeroToNineEnum.ONE.getCode())){
            return wmsInvokeMock(scmWarehouseRequestBase);
        }
        String url = "";
        String method = "";
        if(scmWarehouseRequestBase instanceof ScmItemSyncRequest){
            url = externalSupplierConfig.getItemsSyncUrl();
            method = "商品同步";
        }else if(scmWarehouseRequestBase instanceof ScmInventoryQueryRequest){
            url = externalSupplierConfig.getInventoryQueryUrl();
            method = "库存查询";
        }else if(scmWarehouseRequestBase instanceof ScmEntryOrderCreateRequest){
            url = externalSupplierConfig.getEntryOrderCreateUrl();
            method = "入库单创建";
        }else if(scmWarehouseRequestBase instanceof ScmDeliveryOrderCreateRequest){
            url = externalSupplierConfig.getDeliveryOrderCreateUrl();
            method = "发货单创建";
        }else if(scmWarehouseRequestBase instanceof ScmOrderCancelRequest){
            url = externalSupplierConfig.getOrderCancelUrl();
            method = "单据取消";
        }else if(scmWarehouseRequestBase instanceof ScmEntryOrderDetailRequest){
            url = externalSupplierConfig.getEntryOrderDetailQueryUrl();
            method = "入库单详情";
        }else if(scmWarehouseRequestBase instanceof ScmDeliveryOrderDetailRequest){
            url = externalSupplierConfig.getDeliveryOrderDetailQueryUrl();
            method = "出库单详情";
        }else if(scmWarehouseRequestBase instanceof ScmOrderPacksRequest){
            url = externalSupplierConfig.getOrderPackUrl();
            method = "物流详情";
        }
        url = String.format("%s%s", externalSupplierConfig.getScmExternalUrl(), url);
        String jsonParam = JSON.toJSONString(scmWarehouseRequestBase);

        log.debug(String.format("开始调用仓库%s接口%s,参数: %s. 开始时间%s", method, url, jsonParam,
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT)));
        String response = null;
        AppResult appResult = null;
        try{
            Map<String, Object> params = new HashedMap();
            params.put("request", jsonParam);
            response = HttpClientUtil.httpPostRequest(url, params, 10000);
            log.debug(String.format("结束调用仓库%s接口%s,返回结果: %s. 结束时间%s", method, url, response,
                    DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT)));
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(SuccessFailureEnum.FAILURE.getCode(), appResult.getAppcode())){
                    log.error(String.format("调用仓库%s接口失败,失败信息:%s", method, appResult.getResult()));
                }else{
                    setResponseData(scmWarehouseRequestBase, appResult);
                }
            }else {
                appResult = new AppResult(CommonExceptionEnum.REMOTE_ERROR.getCode(), String.format("调用仓库%s接口返回结果为空", method), "");
            }
        }catch (IOException e){
            String msg = String.format("调用仓库%s接口网络超时,错误信息:%s", method, e.getMessage());
            log.error(msg, e);
            appResult = new AppResult(CommonExceptionEnum.REMOTE_TIMEOUT.getCode(), msg, "");
        }catch (JSONException e){
            String msg = String.format("调用仓库%s接口返回数据格式错误,错误信息:%s", method, e.getMessage());
            log.error(msg, e);
            appResult = new AppResult(CommonExceptionEnum.REMOTE_ERROR.getCode(), msg, "");
        }catch (Exception e){
            String msg = String.format("调用仓库%s接口异常,错误信息:%s", method, e.getMessage());
            log.error(msg, e);
            appResult = new AppResult(CommonExceptionEnum.REMOTE_ERROR.getCode(), msg, "");
        }
        return appResult;
    }

    private void setResponseData(ScmWarehouseRequestBase scmWarehouseRequestBase, AppResult appResult){
        Object response = null;
        if (appResult.getResult() == null) {
        	return;
        }
        if(scmWarehouseRequestBase instanceof ScmItemSyncRequest){
            response = JSON.parseArray(appResult.getResult().toString(), ScmItemSyncResponse.class);
        }else if(scmWarehouseRequestBase instanceof ScmInventoryQueryRequest){
            response = JSON.parseArray(appResult.getResult().toString(), ScmInventoryQueryResponse.class);
        }else if(scmWarehouseRequestBase instanceof ScmEntryOrderCreateRequest){
            response = appResult.getResult();
        }else if(scmWarehouseRequestBase instanceof ScmDeliveryOrderCreateRequest){
            response = JSON.parseArray(appResult.getResult().toString(), ScmDeliveryOrderCreateResponse.class);
        }else if(scmWarehouseRequestBase instanceof ScmOrderCancelRequest){
            response = JSON.parseObject(appResult.getResult().toString()).toJavaObject(ScmOrderCancelResponse.class);
        }else if(scmWarehouseRequestBase instanceof ScmEntryOrderDetailRequest){
            response = JSON.parseArray(appResult.getResult().toString(), ScmEntryOrderDetailResponse.class);
        }else if(scmWarehouseRequestBase instanceof ScmDeliveryOrderDetailRequest){
            response = JSON.parseObject(appResult.getResult().toString()).toJavaObject(ScmDeliveryOrderDetailResponse.class);
        }else if(scmWarehouseRequestBase instanceof  ScmOrderPacksRequest){
            response = JSON.parseObject(appResult.getResult().toString()).toJavaObject(ScmOrderPacksResponse.class);
        }
        appResult.setResult(response);
    }

    /**
     * 调用仓库mock
     * @param scmWarehouseRequestBase
     * @return
     */
    private AppResult wmsInvokeMock(ScmWarehouseRequestBase scmWarehouseRequestBase){
        AppResult appResult = new AppResult(ResponseAck.SUCCESS_CODE, "", "");
        String url = "";
        String method = "";
        if(scmWarehouseRequestBase instanceof ScmItemSyncRequest){
            url = externalSupplierConfig.getItemsSyncUrl();
            method = "商品同步";
            appResult = mockScmItemSyncResponse(scmWarehouseRequestBase, appResult);
        }else if(scmWarehouseRequestBase instanceof ScmInventoryQueryRequest){
            url = externalSupplierConfig.getInventoryQueryUrl();
            method = "库存查询";
            appResult = mockScmInventoryQueryResponse(scmWarehouseRequestBase, appResult);
        }else if(scmWarehouseRequestBase instanceof ScmEntryOrderCreateRequest){
            url = externalSupplierConfig.getEntryOrderCreateUrl();
            method = "入库单创建";
            appResult = mockScmEntryOrderCreateResponse(scmWarehouseRequestBase, appResult);
        }else if(scmWarehouseRequestBase instanceof ScmDeliveryOrderCreateRequest){
            url = externalSupplierConfig.getDeliveryOrderCreateUrl();
            method = "发货单创建";
            appResult = mockScmDeliveryOrderCreateResponse(scmWarehouseRequestBase, appResult);
        }else if(scmWarehouseRequestBase instanceof ScmOrderCancelRequest){
            url = externalSupplierConfig.getOrderCancelUrl();
            method = "单据取消";
            appResult = mockScmOrderCancelResponse(scmWarehouseRequestBase, appResult);
        }else if(scmWarehouseRequestBase instanceof ScmEntryOrderDetailRequest){
            url = externalSupplierConfig.getEntryOrderDetailQueryUrl();
            method = "入库单详情";
        }else if(scmWarehouseRequestBase instanceof ScmDeliveryOrderDetailRequest){
            url = externalSupplierConfig.getDeliveryOrderDetailQueryUrl();
            method = "出库单详情";
        }else if(scmWarehouseRequestBase instanceof ScmOrderPacksRequest){
            url = externalSupplierConfig.getOrderPackUrl();
            method = "物流详情";
        }
        url = String.format("%s%s", externalSupplierConfig.getScmExternalUrl(), url);
        String jsonParam = JSON.toJSONString(scmWarehouseRequestBase);
        log.debug(String.format("开始调用仓库%s接口%s,参数: %s. 开始时间%s", method, url, jsonParam,
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT)));
        log.debug(String.format("结束调用仓库%s接口%s,返回结果: %s. 结束时间%s", method, url, JSON.toJSON(appResult).toString(),
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT)));
        return appResult;
    }

    /**
     * mock商品同步接口返回结果
     * @param scmWarehouseRequestBase
     * @return
     */
    private AppResult mockScmItemSyncResponse(ScmWarehouseRequestBase scmWarehouseRequestBase, AppResult appResult){
        ScmItemSyncRequest scmItemSyncRequest = (ScmItemSyncRequest)scmWarehouseRequestBase;
        List<ScmItemSyncResponse> scmItemSyncResponseList = new ArrayList<>();
        for(ScmWarehouseItem warehouseItem: scmItemSyncRequest.getWarehouseItemList()){
            ScmItemSyncResponse response = new ScmItemSyncResponse();
            response.setCode(ResponseAck.SUCCESS_CODE);
            response.setItemCode(warehouseItem.getItemCode());
            if(StringUtils.isNotBlank(warehouseItem.getItemId())){
                response.setItemId(warehouseItem.getItemId());
            }else {
                response.setItemId(GuidUtil.getNextUid(ITEM_ID_MOCK_PREFIX));
            }
            scmItemSyncResponseList.add(response);
        }
        appResult.setResult(scmItemSyncResponseList);
        return appResult;
    }

    /**
     * mock库存查询接口返回结果
     * @param scmWarehouseRequestBase
     * @return
     */
    private AppResult mockScmInventoryQueryResponse(ScmWarehouseRequestBase scmWarehouseRequestBase, AppResult appResult){
        ScmInventoryQueryRequest scmInventoryQueryRequest = (ScmInventoryQueryRequest)scmWarehouseRequestBase;
        List<ScmInventoryQueryResponse> scmInventoryQueryResponseList = new ArrayList<>();
        for(ScmInventoryQueryItem queryItem: scmInventoryQueryRequest.getScmInventoryQueryItemList()){
            ScmInventoryQueryResponse response = new ScmInventoryQueryResponse();
            response.setWarehouseCode(queryItem.getWarehouseCode());
            response.setOwnerCode(queryItem.getOwnerCode());
            response.setItemCode(queryItem.getItemCode());
            response.setItemId(queryItem.getItemId());
            response.setInventoryType(JingdongInventoryTypeEnum.SALE.getCode());//可销售
            response.setTotalNum(100L);
            response.setQuantity(100L);
            response.setInventoryStatus(JingdongInventoryStateEnum.GOOD.getCode());//良品
            response.setLockQuantity(0L);
            scmInventoryQueryResponseList.add(response);

            ScmInventoryQueryResponse response2 = new ScmInventoryQueryResponse();
            response2.setWarehouseCode(queryItem.getWarehouseCode());
            response2.setOwnerCode(queryItem.getOwnerCode());
            response2.setItemCode(queryItem.getItemCode());
            response2.setItemId(queryItem.getItemId());
            response2.setInventoryType(JingdongInventoryTypeEnum.SALE.getCode());//可销售
            response2.setTotalNum(10L);
            response2.setQuantity(10L);
            response2.setInventoryStatus(JingdongInventoryStateEnum.Quality.getCode());//残品
            response2.setLockQuantity(0L);
            scmInventoryQueryResponseList.add(response2);
        }
        appResult.setResult(scmInventoryQueryResponseList);
        return appResult;
    }

    /**
     * mock入库通知单创建接口返回结果
     * @param scmWarehouseRequestBase
     * @param appResult
     * @return
     */
    private AppResult mockScmEntryOrderCreateResponse(ScmWarehouseRequestBase scmWarehouseRequestBase, AppResult appResult){
        appResult.setResult(GuidUtil.getNextUid(ENTRY_ORDER_CODE_MOCK_PREFIX));
        return appResult;
    }

    /**
     * mock发货通知单创建接口返回结果
     * @param scmWarehouseRequestBase
     * @param appResult
     * @return
     */
    private AppResult mockScmDeliveryOrderCreateResponse(ScmWarehouseRequestBase scmWarehouseRequestBase, AppResult appResult){
        ScmDeliveryOrderCreateRequest scmDeliveryOrderCreateRequest = (ScmDeliveryOrderCreateRequest)scmWarehouseRequestBase;
        List<ScmDeliveryOrderCreateResponse> scmDeliveryOrderCreateResponseList = new ArrayList<>();
        for(ScmDeliveryOrderDO scmDeliveryOrderDO: scmDeliveryOrderCreateRequest.getScmDeleveryOrderDOList()){
            ScmDeliveryOrderCreateResponse response = new ScmDeliveryOrderCreateResponse();
            response.setCode(ResponseAck.SUCCESS_CODE);
            response.setDeliveryOrderCode(scmDeliveryOrderDO.getDeliveryOrderCode());
            response.setWmsOrderCode(GuidUtil.getNextUid(DELIVERY_ORDER_CODE_MOCK_PREFIX));
            response.setMessage("创建成功");
            scmDeliveryOrderCreateResponseList.add(response);
        }
        appResult.setResult(scmDeliveryOrderCreateResponseList);
        return appResult;
    }

    /**
     * mock发货通知单取消接口返回结果
     * @param scmWarehouseRequestBase
     * @param appResult
     * @return
     */
    private AppResult mockScmOrderCancelResponse(ScmWarehouseRequestBase scmWarehouseRequestBase, AppResult appResult){
        ScmOrderCancelResponse response = new ScmOrderCancelResponse();
        response.setFlag(ZeroToNineEnum.ONE.getCode());//取消成功
        response.setMessage("取消成功");
        appResult.setResult(response);
        return appResult;
    }









}
