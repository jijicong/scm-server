package org.trc.util;

/**
 * Created by hzwyz on 2017/8/9 0009.
 */
public class HttpResult {
    private Integer statusCode;

    private String result;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
