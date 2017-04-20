package org.trc.common;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by george on 2016/12/21.
 */
public class TrCouponAck implements Serializable {

    private String code;        //应答码

    private String message;     //应答消息

    private String data;        //详细描述

    private Long packageFrom;   //卡券领取起始时间long型

    private Long packageTo;   //卡券领取结束时间

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getPackageFrom() {
        return packageFrom;
    }

    public void setPackageFrom(Long packageFrom) {
        this.packageFrom = packageFrom;
    }

    public Long getPackageTo() {
        return packageTo;
    }

    public void setPackageTo(Long packageTo) {
        this.packageTo = packageTo;
    }

    public Date getStartTime() {
        if(null!=packageFrom) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(packageFrom);
            return cal.getTime();
        }
        return null;
    }

    public Date getEndTime() {
        if(null!=packageTo) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(packageTo);
            return cal.getTime();
        }
        return null;
    }

    public static TrCouponAck renderFailure(){
        TrCouponAck trCouponAck = new TrCouponAck();
        trCouponAck.setCode("400");
        trCouponAck.setMessage("批次检查失败!");
        return trCouponAck;
    }

    @Override
    public String toString() {
        return "TrCouponAck{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                ", packageFrom=" + packageFrom +
                ", packageTo=" + packageTo +
                '}';
    }
}
