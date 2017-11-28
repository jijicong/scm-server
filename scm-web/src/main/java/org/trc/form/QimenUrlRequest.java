package org.trc.form;

import javax.ws.rs.QueryParam;

/**
 * @author hzszy
 */
public class QimenUrlRequest {
    @QueryParam("sign_method")
    private  String sign_method;
    @QueryParam("timestamp")
    private String timestamp;
    @QueryParam("method")
    private String method;
    @QueryParam("v")
    private String v;
    @QueryParam("app_key")
    private  String app_key;
    @QueryParam("customerId")
    private String customerId;
    @QueryParam("format")
    private String format;

    public String getSign_method() {
        return sign_method;
    }

    public void setSign_method(String sign_method) {
        this.sign_method = sign_method;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}


