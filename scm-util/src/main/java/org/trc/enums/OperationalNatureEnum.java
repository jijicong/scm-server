package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by hzcyn on 2018/5/4.
 */
public enum OperationalNatureEnum {
    THIRD_PARTY("0", "第三方仓库"),
    SELF_SUPPORT("1","自营仓库");

    private String code;
    private String name;

    OperationalNatureEnum(String code, String name){
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
    public static OperationalNatureEnum getClearanceEnumByName(String name){
        for(OperationalNatureEnum validEnum : OperationalNatureEnum.values()){
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
    public static OperationalNatureEnum getClearanceEnumByCode(String code){
        for(OperationalNatureEnum validEnum : OperationalNatureEnum.values()){
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
        for(OperationalNatureEnum sexEnum : OperationalNatureEnum.values()){
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
