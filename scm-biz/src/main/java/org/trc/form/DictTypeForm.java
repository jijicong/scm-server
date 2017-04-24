package org.trc.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.trc.util.QueryModel;

import javax.validation.constraints.Size;
import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/4/21.
 */
public class DictTypeForm extends QueryModel{
    /**
     * 字典类型名称
     */
    @QueryParam("name")
    @Length(max = 64)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
