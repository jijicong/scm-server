package org.trc.enums;

/**
 * Description〈序列号〉
 *
 * @author hzliuwei
 * @create 2018/7/25
 */
public enum SequenceEnum {

    CGTHD_PREFIX("CGTHD", "采购退货单");

    public static SequenceEnum queryNameByCode(String code){
        for(SequenceEnum sourceEnum: SequenceEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    SequenceEnum(String code, String name) {
        this.code = code;
        this.name = name;
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
