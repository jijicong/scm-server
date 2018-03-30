package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 查询入库详情时的商品状态
 */
public enum InventoryQueryResponseEnum {
    MARKETABLE("1","可销售"),
    RETREATS("2","待退品"),
    RESERVATION("3","商家预留"),
    WAREHOUSE_LOCK("4","仓库锁定"),
    ADVENT_LOCK("5","临期锁定"),
    CHECK_LOCK("6","盘点锁定"),
    MRB_STOCK("7","隔离库存");

    private String code;
    private String name;

    InventoryQueryResponseEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    /**
     * @Title: getInventoryQueryResponseEnumByName
     * @Description: 根据枚举名称获取枚举
     * @param @param name
     * @param @return
     * @return InventoryQueryResponseEnum
     * @throws
     */
    public static InventoryQueryResponseEnum getInventoryQueryResponseEnum(String name){
        for(InventoryQueryResponseEnum inventoryQueryResponseEnum : InventoryQueryResponseEnum.values()){
            if(StringUtils.equals(name, inventoryQueryResponseEnum.getName())){
                return inventoryQueryResponseEnum;
            }
        }
        return null;
    }

    /**
     *
     * @Title: getInventoryQueryResponseEnumByCode
     * @Description: 根据枚举编码获取枚举
     * @param @param name
     * @param @return
     * @return InventoryQueryResponseEnum
     * @throws
     */
    public static InventoryQueryResponseEnum getInventoryQueryResponseEnumByCode(String code){
        for(InventoryQueryResponseEnum inventoryQueryResponseEnum : InventoryQueryResponseEnum.values()){
            if(StringUtils.equals(inventoryQueryResponseEnum.getCode(), code)){
                return inventoryQueryResponseEnum;
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
        for(InventoryQueryResponseEnum sexEnum : InventoryQueryResponseEnum.values()){
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
