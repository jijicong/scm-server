package org.trc.form;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/4/21.
 */
public class DictTypeForm extends QueryModel {
    /**
     * 字典类型名称
     */
    @QueryParam("name")
    @Length(max = 64)
    private String name;
    @QueryParam("code")
    @Length(max = 64)
    private String code;
    @QueryParam("description")
    @Length(max = 64)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
