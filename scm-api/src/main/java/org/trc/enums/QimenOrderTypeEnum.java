package org.trc.enums;

/**
 * Created by hzqph on 2017/5/2.
 * 奇门出库单类型枚举
 */
public enum QimenOrderTypeEnum {
    JYCK("JYCK","一般交易出库单"),HHCK("HHCK","换货出库单"),BFCK("BFCK","补发出库单"),QTCK("QTCK","其他出库单");


    private String code;
    private String name;
    QimenOrderTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static QimenOrderTypeEnum queryNameByCode(Integer code){
        for(QimenOrderTypeEnum sourceEnum: QimenOrderTypeEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

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
