package org.trc.enums;


/**
 * @author hzszy
 */

public enum UserTypeEnum {
    CHANNEL_USER("channelUser","业务线用户"),OVERALL_USER("overallUser","全局用户"),MIXTURE_USER("mixtureUser","混合用户");


    public static UserTypeEnum queryNameByCode(String code){
        for(UserTypeEnum sourceEnum: UserTypeEnum.values()){
            if (sourceEnum.getCode().equals(code)){
                return sourceEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    UserTypeEnum(String code, String name) {
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
