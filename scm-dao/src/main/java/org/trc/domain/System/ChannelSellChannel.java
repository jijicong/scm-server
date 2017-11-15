package org.trc.domain.System;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.Date;

/**
 * @author hzszy
 * 业务线,销售渠道关联信息
 */
public class ChannelSellChannel {
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("channelCode")
    @Length(max = 32, message = "业务线编码字母和数字不能超过32个,汉字不能超过16个")
    private String channelCode;

    @FormParam("channelId")
    private Long channelId;

    @FormParam("sellChannelCode")
    @Length(max = 32, message = "销售渠道编码字母和数字不能超过32个,汉字不能超过16个")
    private String sellChannelCode;

    @FormParam("sellChannelId")
    private Long sellChannelId;

    //创建时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

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
        this.channelCode = channelCode;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getSellChannelCode() {
        return sellChannelCode;
    }

    public void setSellChannelCode(String sellChannelCode) {
        this.sellChannelCode = sellChannelCode;
    }

    public Long getSellChannelId() {
        return sellChannelId;
    }

    public void setSellChannelId(Long sellChannelId) {
        this.sellChannelId = sellChannelId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
