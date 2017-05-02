package org.trc.form;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/4/21.
 */
public class DictForm extends QueryModel {
    /**
     * 字典类型编码
     */
    @QueryParam("typeCode")
    private String typeCode;
    /**
     * 字典名称
     */
    @QueryParam("name")
    private String name;
    /**
     * 字典值
     */
    @QueryParam("value")
    private String value;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
