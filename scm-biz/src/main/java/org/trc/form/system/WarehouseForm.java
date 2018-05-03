package org.trc.form.system;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Query trc
 * Created by sone on 2017/5/4.
 */
public class WarehouseForm extends QueryModel{
    /**
     * use for storage warehouse's name
     */
    @QueryParam("name")
    @Length(max = 64)
    private String name;

    @QueryParam("operationalNature")
    @Length(max = 1)
    private String operationalNature;

    @QueryParam("operationalType")
    @Length(max = 1)
    private String operationalType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperationalNature() {
        return operationalNature;
    }

    public void setOperationalNature(String operationalNature) {
        this.operationalNature = operationalNature;
    }

    public String getOperationalType() {
        return operationalType;
    }

    public void setOperationalType(String operationalType) {
        this.operationalType = operationalType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
