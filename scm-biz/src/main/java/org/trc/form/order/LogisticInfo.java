package org.trc.form.order;

import java.util.Date;

/**
 * Created by hzwdx on 2017/7/5.
 */
public class LogisticInfo {

    //配送时间
    private Date msgTime;
    //内容
    private String content;
    //操作人
    private String operator;

    public Date getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(Date msgTime) {
        this.msgTime = msgTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
