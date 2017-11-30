package org.trc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qimen.api.QimenRequest;
import com.qimen.api.request.EntryorderCreateRequest;
import com.qimen.api.request.ItemsSynchronizeRequest;
import com.qimen.api.response.EntryorderCreateResponse;
import com.qimen.api.response.ItemsSynchronizeResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.JDModel.ExternalSupplierConfig;
import org.trc.form.JDModel.ReturnTypeDO;
import org.trc.form.QimenConfig;
import org.trc.service.IQimenService;
import org.trc.util.AppResult;
import org.trc.util.DateUtils;
import org.trc.util.HttpClientUtil;
import org.trc.util.ResultUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzcyn on 2017/11/22.
 */
@Service("qimenService")
public class QimenServiceImpl implements IQimenService {
    private final static Logger log = LoggerFactory.getLogger(QimenServiceImpl.class);

    @Autowired
    private ExternalSupplierConfig externalSupplierConfig;
    @Autowired
    private QimenConfig qimenConfig;

    //接口调用超时时间
    public final static Integer TIME_OUT = 10000;
    public final static String SUCCESS_CODE = "200";

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
}
