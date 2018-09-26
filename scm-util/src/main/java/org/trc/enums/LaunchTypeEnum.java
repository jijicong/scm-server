package org.trc.enums;

import org.apache.commons.lang3.StringUtils;

public enum LaunchTypeEnum {
    SYSTEM_LAUNCH(0, "系统发起"),
    Manual_NEW(1, "手动新建");
    private int code;
    private String name;

    LaunchTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public static LaunchTypeEnum getLaunchTypeEnumByName(String name){
        for(LaunchTypeEnum launchTypeEnum : LaunchTypeEnum.values()){
            if(StringUtils.equals(name, launchTypeEnum.getName())){
                return launchTypeEnum;
            }
        }
        return null;
    }
    
    public static LaunchTypeEnum getLaunchTypeEnumByCode(int code){
        for(LaunchTypeEnum launchTypeEnum : LaunchTypeEnum.values()){
            if(launchTypeEnum.getCode()== code){
                return launchTypeEnum;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
