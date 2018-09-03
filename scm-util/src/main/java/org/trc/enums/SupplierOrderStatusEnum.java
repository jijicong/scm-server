package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/9/3 19:13
 * @Description:
 */
public enum SupplierOrderStatusEnum {

    STATUS_1("1","待发货"),
    STATUS_2("2","部分发货"),
    STATUS_3("3","全部发货"),
    STATUS_4("3","已取消");

    private String code;
    private String name;

    SupplierOrderStatusEnum(String code, String name){
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
    public static SupplierOrderStatusEnum getSupplierOrderStatusEnumByName(String name){
        for(SupplierOrderStatusEnum validEnum : SupplierOrderStatusEnum.values()){
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
    public static SupplierOrderStatusEnum getSupplierOrderStatusEnumByCode(Integer code){
        for(SupplierOrderStatusEnum validEnum : SupplierOrderStatusEnum.values()){
            if(Objects.equals(validEnum.getCode(),code)){
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
        for(SupplierOrderStatusEnum sexEnum : SupplierOrderStatusEnum.values()){
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
