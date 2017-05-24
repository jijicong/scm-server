package org.trc.form.category;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created by hzszy on 2017/5/5.
 */
public class TableDate {

    private Long id;
    private String name;
    private Integer index;
    private String sortStatus;
    private String source;
    private String status;
    private String typeCode;
    private Long propertyId;
    private String valueType;

    public Long getId() {
        return id;
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

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getSortStatus() {
        return sortStatus;
    }

    public void setSortStatus(String sortStatus) {
        this.sortStatus = sortStatus;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getValueType() {
        return valueType;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }
}
