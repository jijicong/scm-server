package org.trc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class AppResult<T> {
	
	public static final String APPCODE = "appcode";
	public static final String DATABUFFER = "databuffer";
	public static final String RESULT = "result";

	private String appcode;
	private String databuffer;
	private T result;
	
	public AppResult(){
	}
	
	public AppResult(String appcode, String databuffer, T result){
		this.appcode = appcode;
		this.databuffer = databuffer;
		this.result = result;
	}
	
	public String returnJson(){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("appcode", this.appcode);
		jsonObject.put("databuffer", this.databuffer);
		jsonObject.put("result", this.result);
		return jsonObject.toString();
	}
	
	public static boolean resultValide(String result){
		if(StringUtils.isEmpty(result)){
			return false;
		}else{
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject = JSON.parseObject(result);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	
	public static AppResult getAppResult(String result){
		JSONObject jsonObject = JSON.parseObject(result);
		AppResult appResult = new AppResult(jsonObject.getString("appcode"),
				jsonObject.getString("databuffer"),
				jsonObject.getString("result"));
		return appResult;
	}

	public String getAppcode() {
		return appcode;
	}
	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}
	public String getDatabuffer() {
		return databuffer;
	}
	public void setDatabuffer(String databuffer) {
		this.databuffer = databuffer;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}

}
