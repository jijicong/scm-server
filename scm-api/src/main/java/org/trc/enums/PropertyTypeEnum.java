package org.trc.enums;

/**
 * Created by hzqph on 2017/6/9.
 */
public enum PropertyTypeEnum {

    PURCHASE_PROPERTY("purchaseProperty", "采购属性"), NATURE_PROPERTY("natureProperty", "自然属性");
    
    public PropertyTypeEnum queryNameByCode(String code) {
        for (PropertyTypeEnum propertyTypeEnum : PropertyTypeEnum.values()) {
            if (propertyTypeEnum.getCode().equals(code)) {
                return propertyTypeEnum;
            }
        }
        return null;
    }

    PropertyTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
