package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by hzcyn on 2018/5/4.
 */
public enum OperationalTypeEnum {

    ONLY_WAREHOUSE("0", "纯仓库"),
    NORMAL_STORE("1","普通门店"),
    UNMANNED_STORE("2","无人门店");

    private String code;
    private String name;

    OperationalTypeEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    /**
     *
     * @Title: getValidEnumByName
     * @Description: 根据枚举名称获取枚举
     * @param @param name
     * @param @return
     * @return ValidEnum
     * @throws
     */
    public static OperationalTypeEnum getClearanceEnumByName(String name){
        for(OperationalTypeEnum validEnum : OperationalTypeEnum.values()){
            if(StringUtils.equals(name, validEnum.getName())){
                return validEnum;
            }
        }
        return null;
    }

    /**
     *
     * @Title: getValidEnumByCode
     * @Description: 根据枚举编码获取枚举
     * @param @param name
     * @param @return
     * @return ValidEnum
     * @throws
     */
    public static OperationalTypeEnum getClearanceEnumByCode(String code){
        for(OperationalTypeEnum validEnum : OperationalTypeEnum.values()){
            if(StringUtils.equals(validEnum.getCode(), code)){
                return validEnum;
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
        for(OperationalTypeEnum sexEnum : OperationalTypeEnum.values()){
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
