package org.trc.domain.goods;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.util.Date;

public class ChangeInventoryRequestFlow implements Serializable {
    private static final long serialVersionUID = 7829684815786180576L;

    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("channelCode")
    @NotEmpty
    @Length(max = 32, message = "渠道编号长度不能超过32个")
    private String channelCode;
    @FormParam("requestCode")
    @NotEmpty
    @Length(max = 32, message = "请求编码长度不能超过32个")
    private String requestCode;
    @FormParam("orderType")
    @NotEmpty
    @Length(max = 32, message = "单据类型长度不能超过32个")
    private String orderType;
    @FormParam("orderCode")
    @NotEmpty
    @Length(max = 32, message = "单据编码长度不能超过32个")
    private String orderCode;
    @FormParam("requestType")
    @NotEmpty
    @Length(max = 16, message = "请求类型长度不能超过16个")
    private String requestType;
    @FormParam("state")
    @NotEmpty
    @Length(max = 16, message = "处理结果状态长度不能超过16个")
    private String state;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode == null ? null : channelCode.trim();
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode == null ? null : requestCode.trim();
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType == null ? null : orderType.trim();
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType == null ? null : requestType.trim();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}