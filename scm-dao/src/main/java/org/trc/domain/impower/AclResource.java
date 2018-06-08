package org.trc.domain.impower;

import org.trc.domain.util.CommonDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;

/**
 * Created by sone on 2017/5/11.
 */
public class AclResource extends CommonDO {
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long code;

    private String name;

    private String url;

    private String method;

    private Long parentId;

    private Integer belong;

    private String type;

    @Transient
    private String check;

    private Long isBelong;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheck() {
        return check;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public Integer getBelong() {
        return belong;
    }

    public void setBelong(Integer belong) {
        this.belong = belong;
    }

    public String getChecked() {
        return this.check;
    }//为zTree的是否被选中做准备

    public void setCheck(String check) {
        this.check = check;
    }

    public Long getpId(){
        return  parentId;
    }  //为zTree的父id做准备

    public Long getId() {
        return code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getIsBelong() {
        return isBelong;
    }

    public void setIsBelong(Long isBelong) {
        this.isBelong = isBelong;
    }
}
