package org.trc.service.impl.warehouse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.ExceptionEnum;
import org.trc.form.JDModel.ExternalSupplierConfig;
import org.trc.form.warehouse.*;
import org.trc.service.impl.JDServiceImpl;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.util.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service("warehouseApiService")
public class WarehouseApiServiceImpl implements IWarehouseApiService {

    private final static Logger log = LoggerFactory.getLogger(WarehouseApiServiceImpl.class);

    @Autowired
    private ExternalSupplierConfig externalSupplierConfig;

    @Override
    public AppResult<Object> itemSync(ScmItemSyncRequest scmItemSyncRequest) {
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
    public AppResult<ScmEntryOrderDetailResponse> entryOrderDetail(ScmEntryOrderDetailRequest entryOrderDetailRequest) {
        return wmsInvoke(entryOrderDetailRequest);
    }

    @Override
    public AppResult<ScmDeliveryOrderDetailResponse> deliveryOrderDetail(ScmDeliveryOrderDetailRequest deliveryOrderDetailRequest) {
        return wmsInvoke(deliveryOrderDetailRequest);
    }

    private AppResult wmsInvoke(ScmWarehouseRequestBase scmWarehouseRequestBase){
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
        }
        url = String.format("%s/%s", externalSupplierConfig.getScmExternalUrl(), url);
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
            }else {
                appResult = ResultUtil.createFailAppResult(String.format("调用仓库%s接口返回结果为空", method));
            }
        }catch (IOException e){
            String msg = String.format("调用仓库%s接口网络超时,错误信息:%s", method, e.getMessage());
            log.error(msg, e);
            appResult = ResultUtil.createFailAppResult(msg);
        }catch (JSONException e){
            String msg = String.format("调用仓库%s接口返回数据格式错误,错误信息:%s", method, e.getMessage());
            log.error(msg, e);
            appResult = ResultUtil.createFailAppResult(msg);
        }catch (Exception e){
            String msg = String.format("调用仓库%s接口异常,错误信息:%s", method, e.getMessage());
            log.error(msg, e);
            appResult = ResultUtil.createFailAppResult(msg);
        }
        return appResult;
    }



}
