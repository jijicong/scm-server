package org.trc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qimen.api.QimenRequest;
import com.qimen.api.request.*;
import com.qimen.api.response.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.SuccessFailureEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.OrderException;
import org.trc.form.JDModel.ExternalSupplierConfig;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.form.QimenConfig;
import org.trc.service.IQimenService;
import org.trc.util.*;

import java.util.*;

/**
 * Created by hzcyn on 2017/11/22.
 */
@Service("qimenService")
public class QimenServiceImpl implements IQimenService {
    //接口调用超时时间
    public final static Integer TIME_OUT = 10000;
    public final static String SUCCESS_CODE = "200";
    private final static Logger log = LoggerFactory.getLogger(QimenServiceImpl.class);
    @Autowired
    private ExternalSupplierConfig externalSupplierConfig;
    @Autowired
    private QimenConfig qimenConfig;

    @Override
    public ReturnTypeDO itemsSync(String warehouseCode, String ownerCode, List<ItemsSynchronizeRequest.Item> items) {
        String url = externalSupplierConfig.getScmExternalUrl()+qimenConfig.getQimenItemsSyncUrl();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("warehouseCode", warehouseCode);
        map.put("ownerCode", ownerCode);
        map.put("items", JSON.toJSONString(items));
        log.debug("开始调用奇门商品同步接口" + url + ", 参数：warehouseCode=" + warehouseCode + "," +
                " ownerCode=" + ownerCode + ", items=" + JSON.toJSONString(items) + ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        ReturnTypeDO returnTypeDO = new ReturnTypeDO();
        returnTypeDO.setSuccess(false);
        String response = null;
            try{
            response = HttpClientUtil.httpPostRequest(url, map, TIME_OUT);
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                AppResult appResult = jbo.toJavaObject(AppResult.class);
                if(StringUtils.equals(appResult.getAppcode(), ZeroToNineEnum.ONE.getCode())){
                    returnTypeDO.setSuccess(true);
                    returnTypeDO.setResult(appResult.getResult());
                }
                returnTypeDO.setResultMessage(appResult.getDatabuffer());
            }else {
                returnTypeDO.setResultMessage("调用奇门商品同步接口返回结果为空");
            }
        }catch (Exception e){
            String msg = String.format("调用奇门商品同步接口异常,错误信息:%s", e.getMessage());
            log.error(msg, e);
            returnTypeDO.setResultMessage(msg);
        }
        log.debug("结束调用奇门商品同步接口" + url + ", 返回结果：" + JSONObject.toJSON(returnTypeDO) + ". 结束时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        return returnTypeDO;
    }

	@Override
	public AppResult entryOrderCreate(EntryorderCreateRequest req) {
		 String url = qimenConfig.getQimenEntryorderCreateUrl();
		 Map<String, Object> map = new HashMap<String, Object>();
		 map.put("entryOrderCreateRequest", JSON.toJSONString(req));
		 return invokeExternal(map, url);
	}

    @Override
    public AppResult<DeliveryorderCreateResponse> deliveryOrderCreate(DeliveryorderCreateRequest req) {
        String url = qimenConfig.getQimenDeliveryOrderCreateUrl();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("deliveryorderCreateRequest", JSON.toJSONString(req));
        return invokeExternal(map, url);
    }

    @Override
    public AppResult<DeliveryorderBatchcreateResponse> deliveryorderBatchcreate(DeliveryorderBatchcreateRequest req) {
        String url = qimenConfig.getQimenDeliveryOrderCreateUrl();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("deliveryorderBatchcreateRequest", JSON.toJSONString(req));
        return invokeExternal(map, url);
    }

    @Override
    public AppResult<OrderCancelResponse> orderCancel(OrderCancelRequest req) {
        String url = qimenConfig.getQimenOrderCancelUrl();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderCancelRequest", JSON.toJSONString(req));
        return invokeExternal(map, url);
    }

    @Override
    public AppResult<InventoryQueryResponse> inventoryQuery(InventoryQueryRequest inventoryQueryRequest) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("request", JSON.toJSONString(inventoryQueryRequest));
        return invokeExternal_Get(paramsMap, qimenConfig.getQimenInventoryQueryUrl());
    }

    private AppResult invokeExternal (Map paramsMap, String url) {
		String serverUrl = externalSupplierConfig.getScmExternalUrl() + url;
		String resStr = "";
		
		log.debug("开始调用external奇门接口:{}, 参数:{}, 开始时间:{}", url, JSON.toJSONString(paramsMap), 
				DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        try {
        	resStr = HttpClientUtil.httpPostRequest(serverUrl, paramsMap, TIME_OUT);
            if (StringUtils.isNotBlank(resStr)) {
            	AppResult appResult = JSON.parseObject(resStr, AppResult.class);
            	String msgDesc = "正常";
                if (!StringUtils.equals(appResult.getAppcode(), SUCCESS_CODE)) {
                	msgDesc = "异常";
                }
            	log.info(url + ":调用external奇门接口" + msgDesc + "返回:" +
            			appResult.getAppcode() + "," + appResult.getDatabuffer());
            	return appResult;
            } else {
            	/**
            	 *  return code = "0"
            	 **/
            	return ResultUtil.createFailAppResult("调用external奇门接口返回结果为空");
            }
        } catch (Exception e) {
            log.error("调用external奇门接口:{}异常,错误信息:{},详细:", url, e.getMessage(), e);
        }
        
		log.debug("结束调用external奇门接口:{}, 返回结果:{}, 结束时间:{}", url, resStr, 
				DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
		
		return ResultUtil.createFailAppResult("调用external奇门接口失败");
	}

    /**
     * 调用external服务get方法
     * 传参说明：url参数
     * @param paramsMap
     * @param url
     * @return
     */
	private AppResult invokeExternal_Get(Map<String, Object> paramsMap, String url){
        String serverUrl = externalSupplierConfig.getScmExternalUrl() + url;
        StringBuilder sb = new StringBuilder(serverUrl);
        Set<Map.Entry<String, Object>> entrys = paramsMap.entrySet();
        if(entrys.size() > 0){
            sb.append("?");
            for(Map.Entry<String, Object> entry: entrys){
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        log.debug("开始调用奇门接口" + url + ", 参数：" + JSON.toJSONString(paramsMap) +  ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        AppResult appResult = new AppResult();
        appResult.setAppcode(SuccessFailureEnum.FAILURE.getCode());
        try{
            String response = HttpClientUtil.httpGetRequest(url);
            log.debug("结束调用奇门接口" + url + ", 返回结果：" + response + ". 结束时间" +
                    DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
            if(StringUtils.isNotBlank(response)){
                JSONObject jbo = JSONObject.parseObject(response);
                appResult = jbo.toJavaObject(AppResult.class);
            }else {
                appResult.setDatabuffer("调用奇门接口返回结果为空");
            }
        }catch (ClassCastException e) {
            String msg = String.format("调用奇门接口返回结果格式错误,%s", e.getMessage());
            log.error(msg, e);
            appResult.setDatabuffer(msg);
        }catch (Exception e){
            String msg = String.format("调用奇门商品库存查询接口异常,%s", e.getMessage());
            log.error(msg, e);
            appResult.setDatabuffer(msg);
        }
        return appResult;
    }

}
