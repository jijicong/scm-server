package org.trc.form.impower;

import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import java.util.List;

/**
 * Created by hzszy on 2017/6/14.
 */
public class JurisdictionTreeNode extends BaseDO{
    @FormParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FormParam("code")
    private Long code;
    @FormParam("name")
    private String name;
    @FormParam("url")
    private String url;
    @FormParam("method")
    private String method;
    @FormParam("parentId")
    private Long parentId;
    @FormParam("belong")
    private Integer belong;
    @FormParam("isBelong")
    private Long isBelong;
    @FormParam("operationType")
    private String operationType;

    private List<JurisdictionTreeNode> children;

    public List<JurisdictionTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<JurisdictionTreeNode> children) {
        this.children = children;
    }



    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
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


    public Integer getBelong() {
        return belong;
    }

    public void setBelong(Integer belong) {
        this.belong = belong;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIsBelong() {
        return isBelong;
    }

    public void setIsBelong(Long isBelong) {
        this.isBelong = isBelong;
    }
}
