package org.trc.domain.config;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 请求流水记录
 * Created by hzdzf on 2017/6/7.
 */
@Table(name = "request_flow")
public class RequestFlow implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requester;//请求方

    private String responder;//响应方

    private String type; //请求类型

    private String requestNum; //请求号

    private String status; //状态，0-失败，1-成功

    private String requestParam; //请求参数

    private String responseParam; //响应参数

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date requestTime; //请求时间


    public RequestFlow() {
    }

    public RequestFlow(String requester, String responder, String type, String requestNum, String status, String requestParam, String responseParam, Date requestTime) {
        this.requester = requester;
        this.responder = responder;
        this.type = type;
        this.requestNum = requestNum;
        this.status = status;
        this.requestParam = requestParam;
        this.responseParam = responseParam;
        this.requestTime = requestTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getResponder() {
        return responder;
    }

    public void setResponder(String responder) {
        this.responder = responder;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestNum() {
        return requestNum;
    }

    public void setRequestNum(String requestNum) {
        this.requestNum = requestNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getResponseParam() {
        return responseParam;
    }

    public void setResponseParam(String responseParam) {
        this.responseParam = responseParam;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }
}
