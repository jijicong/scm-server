package org.trc.form;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Query form
 * Created by sone on 2017/5/4.
 */
public class WarehouseForm extends QueryModel{
    /**
     * use for storage warehouse's name
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
