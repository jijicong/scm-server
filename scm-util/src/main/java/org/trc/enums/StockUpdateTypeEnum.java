package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;


public enum StockUpdateTypeEnum {

	PLUS("0", "加"),
    MINUS("1", "减");

    private String code;
    private String name;

    StockUpdateTypeEnum(String code, String name){
        this.code = code;
        this.name = name;
    }


    public static StockUpdateTypeEnum getStateEnumByName(String name){
        for(StockUpdateTypeEnum stateEnum : StockUpdateTypeEnum.values()){
            if(StringUtils.equals(name, stateEnum.getName())){
                return stateEnum;
            }
        }
        return null;
    }

    public static StockUpdateTypeEnum getStateEnumByCode(String code){
        for(StockUpdateTypeEnum stateEnum : StockUpdateTypeEnum.values()){
            if(StringUtils.equals(stateEnum.getCode(), code)){
                return stateEnum;
            }
        }
        return null;
    }

    /**
     *
     * @Title: toJSONArray
     * @Description: 转换成json数组
     * @param @return
     * @return JSONArray
     * @throws
     */
    public static JSONArray toJSONArray(){
        JSONArray array = new JSONArray();
        for(StockUpdateTypeEnum sexEnum : StockUpdateTypeEnum.values()){
            JSONObject obj = new JSONObject();
            obj.put("code", sexEnum.getCode());
            obj.put("name", sexEnum.getName());
            array.add(obj);
        }
        return array;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
