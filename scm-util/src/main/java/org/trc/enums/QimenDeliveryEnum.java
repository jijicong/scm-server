package org.trc.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by hzcyn on 2017/12/1.
 */
public enum QimenDeliveryEnum {
    NEW("NEW","未开始处理"),
    ACCEPT("ACCEPT","仓库接单"),
    PARTDELIVERED("PARTDELIVERED","部分发货完成"),
    DELIVERED("DELIVERED","发货完成"),
    EXCEPTION("EXCEPTION","异常"),
    CANCELED("CANCELED","取消"),
    CLOSED("CLOSED","关闭"),
    REJECT("REJECT","拒单"),
    CANCELEDFAIL("CANCELEDFAIL","取消失败");

    private String code;
    private String name;

    QimenDeliveryEnum(String code, String name){
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
    public static QimenDeliveryEnum getResultEnumByName(String name){
        for(QimenDeliveryEnum resultEnum : QimenDeliveryEnum.values()){
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
    public static QimenDeliveryEnum getResultEnumByCode(String code){
        for(QimenDeliveryEnum resultEnum : QimenDeliveryEnum.values()){
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
