package org.trc.common;

import java.io.Serializable;

/**
 * Created by george on 2017/2/23.
 */
public class RealNameCheckAck implements Serializable {

    public static final String SUCCESS_CODE = "1";

    public static final String IS_REALNAME = "YES";

    private String resultCode;  //默认1为成功

    private String isRealName;  //是否实名:YES已实名，NO未实名

    private String resultMsg;   //提示信息

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getIsRealName() {
        return isRealName;
    }

    public void setIsRealName(String isRealName) {
        this.isRealName = isRealName;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    @Override
    public String toString() {
        return "RealnameCheckAck{" +
                "resultCode='" + resultCode + '\'' +
                ", isRealName='" + isRealName + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                '}';
    }

    public static RealNameCheckAck renderFailure() {
        RealNameCheckAck realNameCheckAck = new RealNameCheckAck();
        realNameCheckAck.setResultCode("9999");
        realNameCheckAck.setResultMsg("网络连接异常，请稍后重试!");
        return realNameCheckAck;
    }
}
