package org.trc.model;

/**
 * 返回值封装
 * Created by hzdzf on 2017/6/5.
 */
public class ResultModel {

    private String status;

    private String msg;

    public ResultModel() {
    }

    public ResultModel(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
