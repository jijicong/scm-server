package org.trc.domain.System;

import org.hibernate.validator.constraints.Length;
import org.trc.domain.util.ScmDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * Created by hzwdx on 2017/8/8.
 */
public class LogisticsCompany extends ScmDO{
    /**
     * 主键ID
     */
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 公司编码
     */
    @FormParam("companyCode")
    @Length(max = 32, message = "物流公司编码长度不能超过32个字符")
    private String companyCode;
    /**
     * 公司名称
     */
    @FormParam("companyName")
    @Length(max = 32, message = "物流公司名称长度不能超过256个字符")
    private String companyName;
    /**
     *对接的物流平台类型: TRC-泰然城,XT-小泰乐活,100-快递100
     */
    @FormParam("type")
    @Length(max = 32, message = "对接的物流平台类型长度不能超过10个字符")
    private String type;
    /**
     * 是否有效:0-无效,1-有效
     */
    @FormParam("isValid")
    @Length(max = 32, message = "是否有效长度不能超过2个字符")
    private String isValid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }
}
