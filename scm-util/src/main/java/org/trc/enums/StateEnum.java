package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @ClassName: ValidEnum
 * @Description: 是否有效枚举
 * @author A18ccms a18ccms_gmail_com
 * @date 2017年4月6日 上午9:16:13
 *
 */
public enum StateEnum {

    ONSTATE("1", "上架"),
    SHELFSTATE("0", "下架");

    private String code;
    private String name;

    StateEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    /**
     * @Title: getValidEnumByName
     * @Description: 根据枚举名称获取枚举
     * @param @param name
     * @param @return
     * @return ValidEnum
     * @throws
     */
    public static StateEnum getStateEnumByName(String name){
        for(StateEnum stateEnum : StateEnum.values()){
            if(StringUtils.equals(name, stateEnum.getName())){
                return stateEnum;
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
    public static StateEnum getStateEnumByCode(String code){
        for(StateEnum stateEnum : StateEnum.values()){
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
        for(StateEnum sexEnum : StateEnum.values()){
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
