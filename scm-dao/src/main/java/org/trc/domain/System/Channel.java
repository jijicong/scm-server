package org.trc.domain.System;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.domain.BaseDO;

import javax.persistence.*;
import javax.ws.rs.FormParam;

/**
 * Created by sone on 2017/5/2.
 */
public class Channel extends BaseDO{
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("code")
    @NotEmpty
    @Length(max = 32, message = "渠道编码字母和数字不能超过32个,汉字不能超过16个")
    private String code;
    @FormParam("name")
    @NotEmpty
    @Length(max = 64, message = "渠道名称字母和数字不能超过64个,汉字不能超过32个")
    private String name;
    @FormParam("remark")
    @Length(max = 1024,message = "渠道备注字母和数字不能超过1024个,汉字不能超过512个")
    private String remark;


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
}
