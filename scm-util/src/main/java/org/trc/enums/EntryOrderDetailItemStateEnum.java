package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 查询入库详情时的商品状态
 */
public enum EntryOrderDetailItemStateEnum {
    QUALITY_PRODUCTS("1","良品"),
    DEFECTIVE_PRODUCTS("2","残次"),
    SAMPLE_PRODUCTS("3","样品");

    private String code;
    private String name;

    EntryOrderDetailItemStateEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    /**
     * @Title: getEntryOrderDetailItemStateEnumByName
     * @Description: 根据枚举名称获取枚举
     * @param @param name
     * @param @return
     * @return EntryOrderDetailItemStateEnum
     * @throws
     */
    public static EntryOrderDetailItemStateEnum getEntryOrderDetailItemStateEnum(String name){
        for(EntryOrderDetailItemStateEnum entryOrderDetailItemStateEnum : EntryOrderDetailItemStateEnum.values()){
            if(StringUtils.equals(name, entryOrderDetailItemStateEnum.getName())){
                return entryOrderDetailItemStateEnum;
            }
        }
        return null;
    }

    /**
     *
     * @Title: getEntryOrderDetailItemStateEnumByCode
     * @Description: 根据枚举编码获取枚举
     * @param @param name
     * @param @return
     * @return EntryOrderDetailItemStateEnum
     * @throws
     */
    public static EntryOrderDetailItemStateEnum getEntryOrderDetailItemStateEnumByCode(String code){
        for(EntryOrderDetailItemStateEnum entryOrderDetailItemStateEnum : EntryOrderDetailItemStateEnum.values()){
            if(StringUtils.equals(entryOrderDetailItemStateEnum.getCode(), code)){
                return entryOrderDetailItemStateEnum;
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
        for(EntryOrderDetailItemStateEnum sexEnum : EntryOrderDetailItemStateEnum.values()){
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
