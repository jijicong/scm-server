package org.trc.enums;

/**
 * Created by wangyz on 2017/11/21.
 * 分布式锁枚举
 */
public enum DistributeLockEnum {
    WAREHOSE_NOTICE_STOCK("stock_","库存更新"),
    SUBMIT_JINGDONG_ORDERßßß("submitJingdongOrder_","提交京东订单")
    ;

    DistributeLockEnum(String code, String name) {
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
