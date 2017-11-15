package org.trc.domain.System;

import org.hibernate.validator.constraints.Length;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * @author hzszy
 */
public class SellChannel extends BaseDO{
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("sellCode")
    @Length(max = 32, message = "销售渠道编码字母和数字不能超过32个,汉字不能超过16个")
    private String sellCode;

    @FormParam("sellName")
    @Length(max = 100, message = "销售渠道编码长度不能超过100")
    private String sellName;


    @FormParam("sellType")
    @Length(max = 2, message = "销售渠道编码字母和数字不能超过2个,汉字不能超过1个")
    private String sellType;

    @FormParam("remark")
    @Length(max = 300, message = "销售渠道备注长度不能超过300")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSellCode() {
        return sellCode;
    }

    public void setSellCode(String sellCode) {
        this.sellCode = sellCode;
    }

    public String getSellName() {
        return sellName;
    }

    public void setSellName(String sellName) {
        this.sellName = sellName;
    }

    public String getSellType() {
        return sellType;
    }

    public void setSellType(String sellType) {
        this.sellType = sellType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
