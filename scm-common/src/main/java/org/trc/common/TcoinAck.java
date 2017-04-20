package org.trc.common;

import java.io.Serializable;

/**
 * Created by george on 2016/12/17.
 */
public class TcoinAck implements Serializable{

    public static final String SUCCESS_CODE = "1";

    private String resultCode;  //默认1为成功

    private String resultMsg;   //提示信息

    private Long avail;         //可用数量

    private Long toExpire;      //即将过期数量

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Long getAvail() {
        return avail;
    }

    public void setAvail(Long avail) {
        this.avail = avail;
    }

    public Long getToExpire() {
        return toExpire;
    }

    public void setToExpire(Long toExpire) {
        this.toExpire = toExpire;
    }

    public static TcoinAck renderFailure(){
        TcoinAck tcoinAck = new TcoinAck();
        tcoinAck.setResultCode("9999");
        tcoinAck.setResultMsg("网络连接异常，请稍后重试!");
        return tcoinAck;
    }

    @Override
    public String toString() {
        return "TcoinAck{" +
                "resultCode='" + resultCode + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                ", avail=" + avail +
                ", toExpire=" + toExpire +
                '}';
    }

}
