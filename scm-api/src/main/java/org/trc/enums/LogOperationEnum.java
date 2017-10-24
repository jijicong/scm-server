package org.trc.enums;

/**
 * Created by hzqph on 2017/6/26.
 */
public enum LogOperationEnum {
    ADD("新增"),UPDATE("修改"),DELETE("删除"), AUDIT_REJECT("审核驳回"),CANCEL("作废"),RENEWAL("更新"),
    FREEZE("冻结"),UN_FREEZE("解冻"),WAREHOUSE_NOTICE("入库通知"),NOTICE_RECEIVE("通知收货"),
    SUBMIT_JINGDONG_ORDER("映射京东地址并发送"),SUBMIT_ORDER("发送"),SUBMIT_ORDER_FIALURE("下单失败"), DELIVER("已发货"),SYNCHRONIZE("同步"),

    WAIT_FOR_SUBMIT("待发送供应商"),ORDER_EXCEPTION("供应商下单异常"),WAIT_FOR_DELIVER("等待供应商发货"),ALL_DELIVER("全部发货"),
    ORDER_FAILURE("供应商下单失败"),PARTS_DELIVER("部分发货"),ORDER_CANCEL("已取消"),ORDER_CLOSE("关闭"),ORDER_REOPEN("取消关闭");


    private String message;

    LogOperationEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
