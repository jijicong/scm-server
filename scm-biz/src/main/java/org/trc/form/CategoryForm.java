package org.trc.form;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzszy on 2017/5/5.
 */
public class CategoryForm extends QueryModel {
    @QueryParam("name")
    private String name;
    @QueryParam("id")
    private String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
