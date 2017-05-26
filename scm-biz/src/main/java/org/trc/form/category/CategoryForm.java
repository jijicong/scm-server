package org.trc.form.category;

import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzszy on 2017/5/5.
 */
public class CategoryForm extends QueryModel {
    @QueryParam("level")
    private String level;
    @QueryParam("name")
    private String name;
    @QueryParam("sort")
    private String sort;
    @QueryParam("id")
    private String id;


    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

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
