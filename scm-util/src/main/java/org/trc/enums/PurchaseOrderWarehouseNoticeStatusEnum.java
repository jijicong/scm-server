package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * v2.5 入库单 入库状态
 * 入库状态: 1-全部入库,2-部分入库,3-入库异常,4-其他情况
 */
public enum PurchaseOrderWarehouseNoticeStatusEnum {
    WAIT_RECEIVE("0", "等待入库"),
    ALL_GOODS("1","全部入库"),
    RECEIVE_PARTIAL_GOODS("2","部分入库"),
    RECEIVE_GOODS_EXCEPTION("3","入库异常");

    private String code;
    private String name;

    PurchaseOrderWarehouseNoticeStatusEnum(String code, String name){
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
    public static PurchaseOrderWarehouseNoticeStatusEnum getValidEnumByName(String name){
        for(PurchaseOrderWarehouseNoticeStatusEnum validEnum : PurchaseOrderWarehouseNoticeStatusEnum.values()){
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
    public static PurchaseOrderWarehouseNoticeStatusEnum getValidEnumByCode(String code){
        for(PurchaseOrderWarehouseNoticeStatusEnum validEnum : PurchaseOrderWarehouseNoticeStatusEnum.values()){
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
        for(PurchaseOrderWarehouseNoticeStatusEnum sexEnum : PurchaseOrderWarehouseNoticeStatusEnum.values()){
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
