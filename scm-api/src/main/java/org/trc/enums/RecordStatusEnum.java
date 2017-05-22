package org.trc.enums;

/**
 * Created by hzqph on 2017/5/18.
 */
public enum RecordStatusEnum {
    DEFAULT(0,"未改动"),ADD(1,"新增"),UPDATE(2,"更新"),DELETE(3,"删除");

    public static RecordStatusEnum queryNameByCode(Integer code){
        for(RecordStatusEnum recordStatusEnum: RecordStatusEnum.values()){
            if (recordStatusEnum.getCode().equals(code)){
                return recordStatusEnum;
            }
        }
        return null;
    }
    RecordStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
