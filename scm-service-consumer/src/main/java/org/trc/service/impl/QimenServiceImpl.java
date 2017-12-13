package org.trc.service.impl;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.SuccessFailureEnum;
import org.trc.form.QimenConfig;
import org.trc.form.JDModel.ExternalSupplierConfig;
import org.trc.service.IQimenService;
import org.trc.util.AppResult;
import org.trc.util.DateUtils;
import org.trc.util.HttpClientUtil;
import org.trc.util.ResultUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qimen.api.QimenRequest;
import com.qimen.api.request.DeliveryorderBatchcreateRequest;
import com.qimen.api.request.DeliveryorderCreateRequest;
import com.qimen.api.request.EntryorderCreateRequest;
import com.qimen.api.request.InventoryQueryRequest;
import com.qimen.api.request.ItemsSynchronizeRequest;
import com.qimen.api.request.OrderCancelRequest;
import com.qimen.api.request.OrderPendingRequest;
import com.qimen.api.request.ReturnorderCreateRequest;
import com.qimen.api.request.StockoutCreateRequest;
import com.qimen.api.response.DeliveryorderBatchcreateResponse;
import com.qimen.api.response.DeliveryorderCreateResponse;
import com.qimen.api.response.InventoryQueryResponse;
import com.qimen.api.response.ItemsSynchronizeResponse;
import com.qimen.api.response.OrderCancelResponse;
import com.qimen.api.response.OrderPendingResponse;
import com.qimen.api.response.ReturnorderCreateResponse;
import com.qimen.api.response.StockoutCreateResponse;

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
    public AppResult<ItemsSynchronizeResponse> itemsSync(ItemsSynchronizeRequest req) {
        return invokeExternal("itemsSyncRequest", req, "qimenItemsSyncUrl");
    }

    @Override
	public AppResult entryOrderCreate(EntryorderCreateRequest req) {
		 return invokeExternal("entryOrderCreateRequest", req, "qimenEntryorderCreateUrl");
	}

    @Override
    public AppResult<DeliveryorderCreateResponse> deliveryOrderCreate(DeliveryorderCreateRequest req) {
        return invokeExternal("deliveryorderCreateRequest", req, "qimenDeliveryOrderCreateUrl");
    }

    @Override
    public AppResult<DeliveryorderBatchcreateResponse> deliveryorderBatchcreate(DeliveryorderBatchcreateRequest req) {
        return invokeExternal("deliveryorderBatchcreate", req, "qimenDeliveryOrderCreateUrl");
    }

    @Override
    public AppResult<OrderCancelResponse> orderCancel(OrderCancelRequest req) {
        return invokeExternal("orderCancelRequest", req, "qimenOrderCancelUrl");
    }

    @Override
    public AppResult<InventoryQueryResponse> inventoryQuery(InventoryQueryRequest inventoryQueryRequest) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("request", JSON.toJSONString(inventoryQueryRequest));
        return invokeExternal_Get(paramsMap, qimenConfig.getQimenInventoryQueryUrl());
        //return invokeExternal("request", req, "qimenInventoryQueryUrl");
    }
    
	@Override
	public AppResult<ReturnorderCreateResponse> returnOrderCreate(ReturnorderCreateRequest req) {
        return invokeExternal("returnOrderCreateRequest", req, "qimenReturnOrderCreateUrl");
	}

	@Override
	public AppResult<StockoutCreateResponse> stockoutCreate(StockoutCreateRequest req) {
        return invokeExternal("stockOutCreateRequest", req, "qimenStockoutCreateUrl");
	}

	@Override
	public AppResult<OrderPendingResponse> orderPending(OrderPendingRequest req) {
        return invokeExternal("orderPendingRequest", req, "qimenOrderPendingUrl");
	}
	
	/**
	 * 调用奇门external接口
	 * @param key  map参数的键
	 * @param req  请求实体
	 * @param filedName qimenConfig中，url的对应字段名
	 * @return
	 */
	private AppResult invokeExternal(String key, QimenRequest req, String filedName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, JSON.toJSONString(req));
        String serverUrl = getRequestUrl(filedName, this.qimenConfig);
        return _invoke(map, serverUrl);
	}

    private AppResult _invoke (Map paramsMap, String serverUrl) {
		String resStr = "";
		
		log.debug("开始调用external奇门接口:{}, 参数:{}, 开始时间:{}", serverUrl, JSON.toJSONString(paramsMap),
				DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        try {
        	resStr = HttpClientUtil.httpPostRequest(serverUrl, paramsMap, TIME_OUT);
            if (StringUtils.isNotBlank(resStr)) {
            	AppResult appResult = JSON.parseObject(resStr, AppResult.class);
            	String msgDesc = "正常";
                if (!StringUtils.equals(appResult.getAppcode(), SUCCESS_CODE)) {
                	msgDesc = "异常";
                }
            	log.info(serverUrl + ":调用external奇门接口" + msgDesc + "返回:" +
            			appResult.getAppcode() + "," + appResult.getDatabuffer());
            	return appResult;
            } else {
            	/**
            	 *  return code = "0"
            	 **/
            	return ResultUtil.createFailAppResult("调用external奇门接口返回结果为空");
            }
        } catch (Exception e) {
            log.error("调用external奇门接口:{}异常,错误信息:{},详细:", serverUrl, e.getMessage(), e);
        }
        
		log.debug("结束调用external奇门接口:{}, 返回结果:{}, 结束时间:{}", serverUrl, resStr,
				DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
		
		return ResultUtil.createFailAppResult("调用external奇门接口失败");
	}
    
    /**
     * 反射获取url地址
     * @param fileName
     * @param obj
     * @return
     */
    private String getRequestUrl(String filedName, Object obj) {
		try {
			Method getMethod = obj.getClass().getMethod("get" + toUpperFristChar(filedName));
	    	String subUrl = getMethod.invoke(obj).toString();
	    	return externalSupplierConfig.getScmExternalUrl() + subUrl;
		} catch (Exception e) {
			log.error("获取奇门请求地址异常:{}", e.getMessage());
			e.printStackTrace();
		}
		return "";

    }
    
    /**
     * 首字母大写
     * 后期可移动到Stringutils里面  
     */
	private String toUpperFristChar(String string) {  
	    char[] charArray = string.toCharArray();  
	    charArray[0] -= 32;  
	    return String.valueOf(charArray);  
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
        /*StringBuilder sb = new StringBuilder(serverUrl);
        Set<Map.Entry<String, Object>> entrys = paramsMap.entrySet();
        if(entrys.size() > 0){
            sb.append("?");
            for(Map.Entry<String, Object> entry: entrys){
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }*/
        log.debug("开始调用奇门接口" + serverUrl + ", 参数：" + JSON.toJSONString(paramsMap) +  ". 开始时间" +
                DateUtils.dateToString(Calendar.getInstance().getTime(), DateUtils.DATETIME_FORMAT));
        AppResult appResult = new AppResult();
        appResult.setAppcode(SuccessFailureEnum.FAILURE.getCode());
        try{
            String response = HttpClientUtil.httpGetRequest(serverUrl, paramsMap);
            log.debug("结束调用奇门接口" + serverUrl + ", 返回结果：" + response + ". 结束时间" +
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
