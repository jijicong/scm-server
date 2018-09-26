package org.trc.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Auther: hzluoxingcheng
 * @Date: 2018/9/3 20:09
 * @Description:1-待发货,2-部分发货,3-全部发货,4-已取消
 */
public enum ShopOrderStatusEnum {
    IS_NOT_SEND("1","待发货"),
    IS_PART_SEND("2","部分发货"),
    IS_ALL_SEND("3","全部发货"),
    IS_CANCEL("4","已取消");

    private String code;
    private String name;

    ShopOrderStatusEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    /**
     *
     * @Title: getResultEnumByName
     * @Description: 根据枚举名称获取枚举
     * @param @param name
     * @param @return
     * @return ResultEnum
     * @throws
     */
    public static ShopOrderStatusEnum getShopOrderStatusEnumByName(String name){
        for(ShopOrderStatusEnum resultEnum : ShopOrderStatusEnum.values()){
            if(StringUtils.equals(name, resultEnum.getName())){
                return resultEnum;
            }
        }
        return null;
    }

    /**
     *
     * @Title: getResultEnumByCode
     * @Description: 根据枚举编码获取枚举
     * @param @param name
     * @param @return
     * @return ResultEnum
     * @throws
     */
    public static ShopOrderStatusEnum getShopOrderStatusEnumByCode(String code){
        for(ShopOrderStatusEnum resultEnum : ShopOrderStatusEnum.values()){
            if(StringUtils.equals(resultEnum.getCode(), code)){
                return resultEnum;
            }
        }
        return null;
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
