package org.trc.form;

/**
 * /**
 * 泰然城参数
 * Created by hzwdx on 2017/7/19.
 */
public class TrcParam {
    /**
     * 执行动作
     */
    private String action;
    /**
     * 操作时间,时间戳格式
     */
    private Long operateTime;
    /**
     * 请求流水号
     */
    private String noticeNum;
    /**
     * md5签名
     */
    private String sign;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getNoticeNum() {
        return noticeNum;
    }

    public void setNoticeNum(String noticeNum) {
        this.noticeNum = noticeNum;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
