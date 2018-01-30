package org.trc.domain.System;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 *
 * @author sone
 * @date 2017/5/2
 */
public class Channel extends BaseDO{
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("code")
    @Length(max = 32, message = "渠道编码字母和数字不能超过32个,汉字不能超过16个")
    private String code;
    @FormParam("name")
    @NotEmpty
    @Length(max = 256, message = "渠道名称字母和数字不能超过256个,汉字不能超过128个")
    private String name;
    @FormParam("remark")
    @Length(max = 1024,message = "渠道备注字母和数字不能超过1024个,汉字不能超过512个")
    private String remark;

    /**
     * 业务销售渠道id
     */
    @Transient
    @FormParam("sellChannel")
    private String sellChannel;
    /**
     * 业务销售渠道name
     */
    @Transient
    private String sellChannelName;


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSellChannel() {
        return sellChannel;
    }

    public void setSellChannel(String sellChannel) {
        this.sellChannel = sellChannel;
    }

    public String getSellChannelName() {
        return sellChannelName;
    }

    public void setSellChannelName(String sellChannelName) {
        this.sellChannelName = sellChannelName;
    }
}
